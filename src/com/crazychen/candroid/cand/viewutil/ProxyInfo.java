package com.crazychen.candroid.cand.viewutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;
/**
 * 代理类
 * @author crazychen
 *
 */
public class ProxyInfo{
	private String packageName;//包名
	private String targetClassName;//类名
	private String proxyClassName;//代理类名
	private TypeElement typeElement;//类类型

	private int layoutId;
	private Map<Integer, ViewInfo> idViewMap = new HashMap<Integer, ViewInfo>();
	private Map<Integer, ArrayList<EventInfo>> idEventsMap = new HashMap<Integer, ArrayList<EventInfo>>();
	
	public static final String PROXY = "PROXY";

	public ProxyInfo(String packageName, String className){
		this.packageName = packageName;
		this.targetClassName = className;
		this.proxyClassName = className + "$$" + PROXY;
	}

	public void putViewInfo(int id, ViewInfo viewInfo){
		idViewMap.put(id, viewInfo);
	}

	public Map<Integer, ViewInfo> getIdViewMap(){
		return idViewMap;
	}

	public String getProxyClassFullName(){
		return packageName + "." + proxyClassName;
	}

	/**
	 * 拼凑java代码
	 * @return
	 */
	public String generateJavaCode(){
		StringBuilder builder = new StringBuilder();
		builder.append("// Generated code from HyViewInjector. Do not modify!\n");
		builder.append("package ").append(packageName).append(";\n\n");

		builder.append("import android.view.View;\n");		
		builder.append("import com.crazychen.cannotation.AbstractInjector;\n");
		
		builder.append('\n');

		builder.append("public class ").append(proxyClassName);
		builder.append("<T extends ").append(getTargetClassName()).append(">");
		builder.append(" implements AbstractInjector<T>");
		builder.append(" {\n");
		
		generateInjectMethod(builder);
		builder.append('\n');

		builder.append("}\n");
		return builder.toString();
	}

	private String getTargetClassName(){
		return targetClassName.replace("$",".");
	}

	private void generateInjectMethod(StringBuilder builder){
		builder.append("  @Override ")
		.append("public void inject(Object obj) {\n");
		String typeName = typeElement.getSimpleName().toString();
		builder.append("final ").append(typeName).append(" mObj=").append("(").append(typeName)
		.append(")obj;\n");		
		//contentview		
		if (layoutId > 0){
			builder.append("mObj.setContentView(").append(layoutId).append(");\n");
		}
		//findviewbyid
		for(Integer key : idViewMap.keySet()){
			ViewInfo viewInfo = idViewMap.get(key);
			builder.append("mObj.");
			builder.append(viewInfo.getName()).append("=(").append(viewInfo.getType())
			.append(")mObj.findViewById(").append(viewInfo.getId()).append(");\n");
		}
		
		//事件
		for(Integer key : idEventsMap.keySet()){
			ViewInfo viewInfo = idViewMap.get(key);
			String viewName;					
			if(viewInfo!=null)
				viewName = viewInfo.getName();
			else{
				viewName = "mObj.findViewById("+key+")";
			}	
			ArrayList<EventInfo> eventInfos = idEventsMap.get(key);
			for(EventInfo einfo:eventInfos){	
				builder.append("mObj.").append(viewName).append(".")
				.append(einfo.listenerSetter).append("(new ").append(einfo.listenerType)
				.append("(){\n").append("@Override\n public ").append(einfo.returnType)
				.append(" ").append(einfo.MethodName)
				.append("(View v){\n");
				if(!"void".equals(einfo.returnType)){
					builder.append("return ");
				}
				builder.append("mObj.").append(einfo.exeMethod).append("(v);\n");			
				builder.append("}\n").append("});\n");
			}
		}			
		builder.append("  }\n");		
	}

	public TypeElement getTypeElement(){
		return typeElement;
	}

	public void setTypeElement(TypeElement typeElement){
		this.typeElement = typeElement;
	}

	public int getLayoutId(){
		return layoutId;
	}

	public void setLayoutId(int layoutId){
		this.layoutId = layoutId;
	}

	public Map<Integer, ArrayList<EventInfo>> getIdEventsMap() {
		return idEventsMap;
	}
}