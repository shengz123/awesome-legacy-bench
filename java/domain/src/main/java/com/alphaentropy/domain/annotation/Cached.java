package com.alphaentropy.domain.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {
    String frequency() default "daily";
    Class effectiveClz() default String.class;
    String effectiveDate() default "actualPublishDate";
    boolean preLoaded() default false;
}
