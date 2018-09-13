package com.study.anaotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
* @author niebiaofei
*
*/
@Inherited
@Retention(RUNTIME)
@Target({ TYPE })
public @interface Table {
    String value() default "";

}
