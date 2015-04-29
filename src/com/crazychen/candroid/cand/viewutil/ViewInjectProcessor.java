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
 * ����࣬���ڱ���ʱ����ע�⣬���ɴ�����
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
	 * ��ȫ��
	 * ����(��ȥ����)
	 * ����
	 */
	private String fqClassName=null, className=null, packageName=null;	
	
	/**
	 * ��ڷ���
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv){
		this.roundEnv = roundEnv;		
		//��ȡcontentview
		for (Element ele : roundEnv.getElementsAnnotatedWith(ContentView.class)){
			if (ele.getKind() == ElementKind.CLASS){
				TypeElement classElement = (TypeElement) ele;
				PackageElement packageElement = (PackageElement) ele.getEnclosingElement();
				fqClassName = classElement.getQualifiedName().toString();
				className = classElement.getSimpleName().toString();//������������
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
		//��ȡviewId
		for (Element ele : roundEnv.getElementsAnnotatedWith(ViewById.class)){
			if(ele.getKind() == ElementKind.FIELD){
				VariableElement varElement = (VariableElement) ele;
				TypeElement classElement = (TypeElement) ele.getEnclosingElement();								
				fqClassName = classElement.getQualifiedName().toString();
				PackageElement packageElement = elementUtils.getPackageOf(classElement);				
				packageName = packageElement.getQualifiedName().toString();				
				className = getClassName(classElement, packageName);		
				
				int id = varElement.getAnnotation(ViewById.class).value();//VIEWid
				String fieldName = varElement.getSimpleName().toString();//����
				String fieldType = varElement.asType().toString();//View����				
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
		//��ȡ�¼�
		handleEvents();				
		
		//��������
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
	 * �����¼�ain���ͣ����ö�Ӧhandle
	 */
	private void handleEvents(){	
		for(EventState e:EventState.values()){
			eState = e;			
			handleEvent(e.clazz);
		}		
	}	

	/**
	 * ��ͬ�¼����͵�ע������ͬ��ǿ������ת��
	 * @param ele
	 * @param a
	 * @return
	 */
	private EventInfo midHandle(Element ele,Class<? extends Annotation> a){	
		String exeName = ele.getSimpleName().toString();//ִ�з�����	
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
	 * �����¼�ע��
	 * @param a
	 */
	private void handleEvent(Class<? extends Annotation> a){		
		for (Element ele : roundEnv.getElementsAnnotatedWith(a)){			
			if(ele.getKind() == ElementKind.METHOD){								
				ExecutableElement exeElement = (ExecutableElement) ele;//ע���µ�Ԫ��
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
	 * ��ȡ����
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
	 * д��log,���ڵ���
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
