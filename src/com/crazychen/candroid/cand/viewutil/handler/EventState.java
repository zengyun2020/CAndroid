package com.crazychen.candroid.cand.viewutil.handler;

import java.lang.annotation.Annotation;

import com.crazychen.cannotation.annotations.event.OnClick;
import com.crazychen.cannotation.annotations.event.OnLongClick;
/**
 * 事件注册类
 * 每新增一种事件，必须在这里注册
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
