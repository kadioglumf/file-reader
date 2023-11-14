package com.kadioglumf.dto;

import com.kadioglumf.annotations.csv.CsvColumn;
import com.kadioglumf.annotations.csv.ExportCsvSettings;
import com.kadioglumf.cellprocessor.DateCell;
import com.kadioglumf.cellprocessor.NumberCell;
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
@ExportCsvSettings
public class TestCsvDto implements Serializable {

    @CsvColumn(columnIndex = 0, cellProcessor = DateCell.class)
    private Date date;

    @CsvColumn(columnIndex = 1, cellProcessor = DateCell.class)
    private LocalTime localTime;

    @CsvColumn(columnIndex = 2, cellProcessor = NumberCell.class)
    private BigDecimal total;

    @CsvColumn(columnIndex = 3, cellProcessor = DateCell.class)
    private ZonedDateTime zonedDateTime;

    @CsvColumn(columnIndex = 4, cellProcessor = DateCell.class)
    private Instant instant;

    @CsvColumn(columnIndex = 5, cellProcessor = DateCell.class)
    private LocalDateTime localDateTime;

    @CsvColumn(columnIndex = 6, cellProcessor = DateCell.class)
    private LocalDate localDate;

    @CsvColumn(columnIndex = 7, cellProcessor = NumberCell.class)
    private Long longg;

    @CsvColumn(columnIndex = 8, cellProcessor = NumberCell.class)
    private int intt;

    @CsvColumn(columnIndex = 9, cellProcessor = NumberCell.class)
    private Integer integer;
}
