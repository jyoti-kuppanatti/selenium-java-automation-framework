package com.qa.framework.utils;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Automatically applies RetryAnalyzer to every @Test method via TestNG's annotation
 * transformer hook, so individual tests don't need retryAnalyzer = RetryAnalyzer.class
 * added by hand. Wired in testng.xml as a <listener>.
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        Class<? extends IRetryAnalyzer> currentAnalyzer = annotation.getRetryAnalyzerClass();
        if (currentAnalyzer == null || currentAnalyzer.equals(IRetryAnalyzer.class)) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
