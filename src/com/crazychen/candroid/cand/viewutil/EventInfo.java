package com.crazychen.candroid.cand.viewutil;
/**
 * �¼���Ϣ��
 * @author crazychen
 *
 */
public class EventInfo {
	String MethodName;//��Ӧ���ݷ�����(mMethod)
	String listenerType;//�¼�����(onClickListener)
	String listenerSetter;//�����¼��ķ�����(setOnclickListener)
	String exeMethod;//�¼�ִ����(onclick)
	String returnType;//����ֵ������(void)
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
