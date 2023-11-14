package com.kadioglumf.annotations.csv;

import com.kadioglumf.cellprocessor.CellProcessor;
import com.kadioglumf.cellprocessor.StringCell;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CsvColumn {
    int columnIndex();
    Class<? extends CellProcessor> cellProcessor() default StringCell.class;
}
