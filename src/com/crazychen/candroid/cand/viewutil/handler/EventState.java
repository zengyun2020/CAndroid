package com.crazychen.candroid.cand.viewutil.handler;

import java.lang.annotation.Annotation;

import com.crazychen.cannotation.annotations.event.OnClick;
import com.crazychen.cannotation.annotations.event.OnLongClick;
/**
 * �¼�ע����
 * ÿ����һ���¼�������������ע��
 * @author Administrator
 *
 */
public enum EventState {
	OnClick(OnClick.class),
	OnLongClick(OnLongClick.class);
	
	public Class<? extends Annotation> clazz;
	private EventState(Class<? extends Annotation> clazz) {
		this.clazz = clazz;
	}
		
}
