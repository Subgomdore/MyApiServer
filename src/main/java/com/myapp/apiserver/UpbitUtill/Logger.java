package com.myapp.apiserver.UpbitUtill;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Logger {
    public static void classInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement currentElement = stackTrace[2];  // 호출한 메서드의 정보를 가져옴

        log.info("Class: " + currentElement.getClassName());
    }

    public static void methodInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement currentElement = stackTrace[2];  // 호출한 메서드의 정보를 가져옴

        log.info("Class: " + currentElement.getClassName() +
                ", Method: " + currentElement.getMethodName());
    }


    public static void errInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement currentElement = stackTrace[2];  // 호출한 메서드의 정보를 가져옴

        log.error("Class: " + currentElement.getClassName() +
                ", Method: " + currentElement.getMethodName() +
                ", Line: " + currentElement.getLineNumber());
    }
}
