package com.kadioglumf.annotations.csv;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportCsvSettings {
    char quoteChar() default '"';
    char delimiterChar() default ',';
    String endOfLineSymbols() default "\r\n";
    boolean isFirstRowHeader() default true;
}
