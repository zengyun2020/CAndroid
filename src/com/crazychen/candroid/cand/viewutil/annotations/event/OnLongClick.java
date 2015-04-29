package com.crazychen.candroid.cand.viewutil.annotations.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@EventBase(
        listenerType = "android.view.View.OnLongClickListener",
        listenerSetter = "setOnLongClickListener",
        methodName = "onLongClick",
        ReturnType = "boolean")
/**
 * 长按事件 
 */
public @interface OnLongClick {
    int value();	
    int[] parentId() default 0;
}
