package com.kadioglumf.dto;

import com.kadioglumf.annotations.excel.ExcelColumn;
import com.kadioglumf.annotations.excel.ImportExcelSettings;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@ImportExcelSettings
public class TestExcelDto implements Serializable {

    @ExcelColumn(columnIndex = 0)
    private Date date;

    @ExcelColumn(columnIndex = 1)
    private LocalTime localTime;

    @ExcelColumn(columnIndex = 2)
    private BigDecimal total;

    @ExcelColumn(columnIndex = 3)
    private ZonedDateTime zonedDateTime;

    @ExcelColumn(columnIndex = 4)
    private Instant instant;

    @ExcelColumn(columnIndex = 5)
    private LocalDateTime localDateTime;

    @ExcelColumn(columnIndex = 6)
    private LocalDate localDate;

    @ExcelColumn(columnIndex = 7)
    private Long longg;

    @ExcelColumn(columnIndex = 8)
    private int intt;

    @ExcelColumn(columnIndex = 9)
    private Integer integer;

}

