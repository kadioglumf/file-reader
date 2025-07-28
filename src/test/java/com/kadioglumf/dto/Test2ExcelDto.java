package com.kadioglumf.dto;

import com.kadioglumf.annotations.excel.ExcelColumn;
import com.kadioglumf.annotations.excel.ImportExcelSettings;
import com.kadioglumf.cellprocessor.NumberCell;
import lombok.Data;

@Data
@ImportExcelSettings
public class Test2ExcelDto implements BaseDto {

  @ExcelColumn(columnIndex = 0, cellProcessor = NumberCell.class)
  private Integer date;
}
