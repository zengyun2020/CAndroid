package com.crazychen.candroid.cand.viewutil.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
/**
 * view_id
 * @author Administrator
 *
 */
public @interface ViewById {
	int value();  
}
