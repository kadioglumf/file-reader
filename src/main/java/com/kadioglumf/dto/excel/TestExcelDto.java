package com.kadioglumf.dto.excel;

import com.kadioglumf.annotations.excel.ExcelColumn;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;

@Data
public class TestExcelDto implements Serializable {

    @ExcelColumn(columnIndex = 0)
    private Date date;

    @ExcelColumn(columnIndex = 1)
    private LocalTime time;

    @ExcelColumn(columnIndex = 2)
    private BigDecimal total;

    @ExcelColumn(columnIndex = 3)
    private BigDecimal dogalgaz;

    @ExcelColumn(columnIndex = 4)
    private BigDecimal ruzgar;

    @ExcelColumn(columnIndex = 5)
    private BigDecimal linyit;

    @ExcelColumn(columnIndex = 6)
    private BigDecimal taskomur;

    @ExcelColumn(columnIndex = 7)
    private BigDecimal ithalkomur;

    @ExcelColumn(columnIndex = 8)
    private BigDecimal fueloil;

    @ExcelColumn(columnIndex = 9)
    private BigDecimal jeotermal;
    @ExcelColumn(columnIndex = 10)
    private BigDecimal barajli;
    @ExcelColumn(columnIndex = 11)
    private BigDecimal nafta;
    @ExcelColumn(columnIndex = 12)
    private BigDecimal biyokutle;
    @ExcelColumn(columnIndex = 13)
    private BigDecimal akarsu;
    @ExcelColumn(columnIndex = 14)
    private BigDecimal diger;
}
