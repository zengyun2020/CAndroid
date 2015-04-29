package com.crazychen.candroid.cand.viewutil;
/**
 * 事件信息类
 * @author crazychen
 *
 */
public class EventInfo {
	String MethodName;//对应内容方法名(mMethod)
	String listenerType;//事件类型(onClickListener)
	String listenerSetter;//设置事件的方法名(setOnclickListener)
	String exeMethod;//事件执行名(onclick)
	String returnType;//返回值类型名(void)
	public EventInfo(String methodName, String listenerType,
			String listenerSetter,String exeMethod,String returnType) {
		super();
		this.MethodName = methodName;
		this.listenerType = listenerType;
		this.listenerSetter = listenerSetter;
		this.exeMethod = exeMethod;
		this.returnType = returnType;
	}		
}
