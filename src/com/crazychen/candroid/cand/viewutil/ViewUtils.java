package com.crazychen.candroid.cand.viewutil;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;

public class ViewUtils {
	static final Map<Class<?>, AbstractInjector<Object>> INJECTORS = new LinkedHashMap<Class<?>, AbstractInjector<Object>>();
	
	public static void inject(Activity activity){
		AbstractInjector<Object> injector = findInjector(activity);
		injector.inject(activity);
	}

	private static AbstractInjector<Object> findInjector(Object activity){
		Class<?> clazz = activity.getClass();
		AbstractInjector<Object> injector = INJECTORS.get(clazz);
		if (injector == null){
			try{
				Class injectorClazz = Class.forName(clazz.getName() + "$$"
						+ ProxyInfo.PROXY);
				injector = (AbstractInjector<Object>) injectorClazz
						.newInstance();
				INJECTORS.put(clazz, injector);
			} catch (ClassNotFoundException e){
				e.printStackTrace();
			} catch (InstantiationException e){
				e.printStackTrace();
			} catch (IllegalAccessException e){
				e.printStackTrace();
			}
		}
		return injector;
	}
}
