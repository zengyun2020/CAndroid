package com.crazychen.candroid.cand.viewutil.annotations.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(
        listenerType = "android.widget.AbsListView.OnScrollListener",
        listenerSetter = "setOnScrollListener",
        methodName = "onScroll")
public @interface OnScroll {
    int[] value();   
    int[] parentId() default 0;
}
