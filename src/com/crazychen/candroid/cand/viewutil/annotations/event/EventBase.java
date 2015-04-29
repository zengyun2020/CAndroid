package com.crazychen.candroid.cand.viewutil.annotations.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * �¼�����
 * �����¼��ķ�����
 * �¼�������
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
