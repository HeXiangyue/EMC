package com.terry.interview.emc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for referencing a directory in the resources. 
 * 
 * @author xianghe
 */


@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
public @interface ResFile {

	/**
	 * @return The name for the resource folder relative to the resources namespace.
	 */

	String value();
}
