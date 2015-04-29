package com.crazychen.candroid.cand.viewutil.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
/**
 * contentview
 * @author Administrator
 *
 */
public @interface ContentView {
    int value();
}
