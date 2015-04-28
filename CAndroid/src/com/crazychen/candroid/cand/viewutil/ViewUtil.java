package com.crazychen.candroid.cand.viewutil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.view.View;

import com.crazychen.candroid.cand.viewutil.annotation.ContentView;
import com.crazychen.candroid.cand.viewutil.annotation.ViewInject;
import com.crazychen.candroid.cand.viewutil.annotation.event.EventBase;

public class ViewUtil {
	private ViewUtil() {}
	
	public static void inject(Activity activity) {
		injectObject(activity,new ViewFinder(activity));
    }
	
	private static void injectObject(Object handler,ViewFinder finder) {
		Class<?> handlerType = handler.getClass();

        //ContentView注解		
        ContentView contentView = handlerType.getAnnotation(ContentView.class);
        if (contentView != null) {
            try {
                Method setContentViewMethod = handlerType.getMethod("setContentView", int.class);
                setContentViewMethod.invoke(handler, contentView.value());
            } catch (Throwable e) {
                //LogUtils.e(e.getMessage(), e);
            }
        }
        
        //view注解
        Field[] fields = handlerType.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                if (viewInject != null) {
                    try {
                        View view = finder.findViewById(viewInject.value(),viewInject.parentId());
                        if (view != null) {
                            field.setAccessible(true);
                            field.set(handler, view);
                        }
                    } catch (Throwable e) {
                        //LogUtils.e(e.getMessage(), e);
                    }
                } 
            }
        }
        
        //事件注解
        Method[] methods = handlerType.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
            	//获取方法的注解
                Annotation[] annotations = method.getDeclaredAnnotations();
                if (annotations != null && annotations.length > 0) {
                    for (Annotation annotation : annotations) {
                        Class<?> annType = annotation.annotationType();
                        //如果是事件注解
                        if (annType.getAnnotation(EventBase.class) != null) {
                            method.setAccessible(true);
                            try {
                                Method valueMethod = annType.getDeclaredMethod("value");
                                Method parentIdMethod = null;
                                try {
                                    parentIdMethod = annType.getDeclaredMethod("parentId");
                                } catch (Throwable e) {
                                }
                                Object values = valueMethod.invoke(annotation);
                                Object parentIds = parentIdMethod == null ? null : parentIdMethod.invoke(annotation);
                                int parentIdsLen = parentIds == null ? 0 : Array.getLength(parentIds);
                                int len = Array.getLength(values);
                                for (int i = 0; i < len; i++) {
                                    ViewInjectInfo info = new ViewInjectInfo();
                                    info.value = Array.get(values, i);
                                    info.parentId = parentIdsLen > i ? (Integer) Array.get(parentIds, i) : 0;
                                    EventListenerManager.addEventMethod(finder, info, annotation, handler, method);
                                }
                            } catch (Throwable e) {
                                //LogUtils.e(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
	}
}
