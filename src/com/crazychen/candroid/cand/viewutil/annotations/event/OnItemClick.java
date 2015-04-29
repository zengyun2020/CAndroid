package com.crazychen.candroid.cand.viewutil.annotations.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(
        listenerType = "android.widget.AdapterView.OnItemClickListener",
        listenerSetter = "setOnItemClickListener",
        methodName = "onItemClick")
public @interface OnItemClick {
    int[] value();  
    int[] parentId() default 0;
}
