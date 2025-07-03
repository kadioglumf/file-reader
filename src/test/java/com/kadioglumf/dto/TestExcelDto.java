package com.kadioglumf.dto;

import com.kadioglumf.annotations.excel.ExcelColumn;
import com.kadioglumf.annotations.excel.ImportExcelSettings;
import com.kadioglumf.cellprocessor.BooleanCell;
import com.kadioglumf.cellprocessor.DateCell;
import com.kadioglumf.cellprocessor.EnumCell;
import com.kadioglumf.cellprocessor.NumberCell;
import com.kadioglumf.enums.TestEnum;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.Data;

@Data
@ImportExcelSettings
public class TestExcelDto implements BaseDto {

  @ExcelColumn(columnIndex = 0, cellProcessor = DateCell.class)
  private Date date;

  @ExcelColumn(columnIndex = 1, cellProcessor = DateCell.class)
  private LocalTime localTime;

  @ExcelColumn(columnIndex = 2, cellProcessor = NumberCell.class)
  private BigDecimal total;

  @ExcelColumn(columnIndex = 3, cellProcessor = DateCell.class)
  private ZonedDateTime zonedDateTime;

  @ExcelColumn(columnIndex = 4, cellProcessor = DateCell.class)
  private Instant instant;

  @ExcelColumn(columnIndex = 5, cellProcessor = DateCell.class)
  private LocalDateTime localDateTime;

  @ExcelColumn(columnIndex = 6, cellProcessor = DateCell.class)
  private LocalDate localDate;

  @ExcelColumn(columnIndex = 7, cellProcessor = NumberCell.class)
  private Long longg;

  @ExcelColumn(columnIndex = 8, cellProcessor = NumberCell.class)
  private int intt;

  @ExcelColumn(columnIndex = 9, cellProcessor = NumberCell.class)
  private Integer integer;

  @ExcelColumn(columnIndex = 10, cellProcessor = BooleanCell.class)
  private Boolean boole;

  @ExcelColumn(columnIndex = 11, cellProcessor = EnumCell.class)
  private TestEnum testEnum;
}
