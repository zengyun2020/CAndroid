package com.crazychen.candroid.cand.viewutil.annotations.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 事件类型
 * 设置事件的方法名
 * 事件方法名
 *
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface EventBase {
    String listenerType();
    String listenerSetter();
    String methodName();
    String ReturnType() default "void";
}
