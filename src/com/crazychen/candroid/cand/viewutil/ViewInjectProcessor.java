package com.crazychen.candroid.cand.viewutil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import com.crazychen.cannotation.annotations.ContentView;
import com.crazychen.cannotation.annotations.ViewById;
import com.crazychen.cannotation.annotations.event.EventBase;
import com.crazychen.cannotation.annotations.event.OnClick;
import com.crazychen.cannotation.annotations.event.OnLongClick;
import com.crazychen.cannotation.handler.EventState;
/**
 * 入口类，用于编译时解析注解，生成代理类
 * @author crazychen
 *
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ViewInjectProcessor extends AbstractProcessor{
	private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();	
	private Elements elementUtils;
	private EventState eState;
	private RoundEnvironment roundEnv;
	private int cur_id;
	@Override
	public synchronized void init(ProcessingEnvironment env){
		super.init(env);
		elementUtils = env.getElementUtils();
	}
		
	/**
	 * 类全名
	 * 类名(除去包名)
	 * 包名
	 */
	private String fqClassName=null, className=null, packageName=null;	
	
	/**
	 * 入口方法
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv){
		this.roundEnv = roundEnv;		
		//获取contentview
		for (Element ele : roundEnv.getElementsAnnotatedWith(ContentView.class)){
			if (ele.getKind() == ElementKind.CLASS){
				TypeElement classElement = (TypeElement) ele;
				PackageElement packageElement = (PackageElement) ele.getEnclosingElement();
				fqClassName = classElement.getQualifiedName().toString();
				className = classElement.getSimpleName().toString();//被代理类类型
				packageName = packageElement.getQualifiedName().toString();				

				int layoutId = classElement.getAnnotation(ContentView.class).value();//layout_id				
				ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
				if (proxyInfo != null){
					proxyInfo.setLayoutId(layoutId);
				} else{
					proxyInfo = new ProxyInfo(packageName, className);
					proxyInfo.setTypeElement(classElement);
					proxyInfo.setLayoutId(layoutId);
					mProxyMap.put(fqClassName, proxyInfo);
				}				
			}
		}		
		//获取viewId
		for (Element ele : roundEnv.getElementsAnnotatedWith(ViewById.class)){
			if(ele.getKind() == ElementKind.FIELD){
				VariableElement varElement = (VariableElement) ele;
				TypeElement classElement = (TypeElement) ele.getEnclosingElement();								
				fqClassName = classElement.getQualifiedName().toString();
				PackageElement packageElement = elementUtils.getPackageOf(classElement);				
				packageName = packageElement.getQualifiedName().toString();				
				className = getClassName(classElement, packageName);		
				
				int id = varElement.getAnnotation(ViewById.class).value();//VIEWid
				String fieldName = varElement.getSimpleName().toString();//域名
				String fieldType = varElement.asType().toString();//View类型				
				ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
				if (proxyInfo == null){
					proxyInfo = new ProxyInfo(packageName, className);
					mProxyMap.put(fqClassName, proxyInfo);
					proxyInfo.setTypeElement(classElement);
				}
				proxyInfo.putViewInfo(id,
						new ViewInfo(id, fieldName, fieldType));					
			}
		}
		//获取事件
		handleEvents();				
		
		//创建代理
		for (String key : mProxyMap.keySet()){
			ProxyInfo proxyInfo = mProxyMap.get(key);
			try{
				JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
						proxyInfo.getProxyClassFullName(),
						proxyInfo.getTypeElement());
				Writer writer = jfo.openWriter();
				writer.write(proxyInfo.generateJavaCode());
				writeLog(proxyInfo.generateJavaCode());
				writer.flush();
				writer.close();
			} catch (IOException e){}
		}
				
		return true;
	}		
	
	/**
	 * 根据事件ain类型，调用对应handle
	 */
	private void handleEvents(){	
		for(EventState e:EventState.values()){
			eState = e;			
			handleEvent(e.clazz);
		}		
	}	

	/**
	 * 不同事件类型的注解做不同的强制类型转换
	 * @param ele
	 * @param a
	 * @return
	 */
	private EventInfo midHandle(Element ele,Class<? extends Annotation> a){	
		String exeName = ele.getSimpleName().toString();//执行方法名	
		Annotation an = ele.getAnnotation(a);							
		EventBase eBase = ele.getAnnotationMirrors().get(0).getAnnotationType().asElement().getAnnotation(EventBase.class);
		if(an instanceof OnClick){	
			OnClick newan = (OnClick) an;	
			cur_id = newan.value();				
		}
		if(an instanceof OnLongClick){
			OnLongClick newan = (OnLongClick) an;	
			cur_id = newan.value();					
		}				
		return new EventInfo(eBase.methodName(),eBase.listenerType(),eBase.listenerSetter(),exeName
				,eBase.ReturnType());
	}
	
	/**
	 * 处理事件注解
	 * @param a
	 */
	private void handleEvent(Class<? extends Annotation> a){		
		for (Element ele : roundEnv.getElementsAnnotatedWith(a)){			
			if(ele.getKind() == ElementKind.METHOD){								
				ExecutableElement exeElement = (ExecutableElement) ele;//注解下的元素
				TypeElement classElement = (TypeElement) ele.getEnclosingElement();								
				fqClassName = classElement.getQualifiedName().toString();
				PackageElement packageElement = elementUtils.getPackageOf(classElement);				
				packageName = packageElement.getQualifiedName().toString();				
				className = getClassName(classElement, packageName);
								
				ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
				EventInfo einfo = midHandle(ele,a);
				if (proxyInfo == null){
					proxyInfo = new ProxyInfo(packageName, className);
					mProxyMap.put(fqClassName, proxyInfo);
					proxyInfo.setTypeElement(classElement);					
				}	
				if(proxyInfo.getIdEventsMap().get(cur_id)==null){
					proxyInfo.getIdEventsMap().put(cur_id, new ArrayList<EventInfo>());
				}
				proxyInfo.getIdEventsMap().get(cur_id).add(einfo);
			}
		}
	}
	
	/**
	 * 获取类名
	 * @param type
	 * @param packageName
	 * @return
	 */
	public static String getClassName(TypeElement type, String packageName){
		int packageLen = packageName.length() + 1;
		return type.getQualifiedName().toString().substring(packageLen)
				.replace('.', '$');
	}		

	/**
	 * 写入log,用于调试
	 * @param str
	 * @param filename
	 */
	private void writeLog(String str,String filename){
		try{
			FileWriter fw = new FileWriter(new File(filename), true);
			fw.write(str + "\n");
			fw.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void writeLog(String str){
		writeLog(str, "D:/process1.txt");
	}
}
