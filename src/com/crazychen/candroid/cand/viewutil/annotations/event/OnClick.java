package com.crazychen.candroid.cand.viewutil.annotations.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@EventBase(
        listenerType = "android.view.View.OnClickListener",
        listenerSetter = "setOnClickListener",
        methodName = "onClick")
/**
 * µã»÷ÊÂ¼þ
 * @author Administrator
 *
 */
public @interface OnClick {
    int value();
    int[] parentId() default 0;  
}
