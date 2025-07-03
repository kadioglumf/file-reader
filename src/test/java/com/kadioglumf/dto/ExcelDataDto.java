package com.kadioglumf.dto;

import com.kadioglumf.annotations.excel.Sheet;
import java.util.List;
import lombok.Data;

@Data
public class ExcelDataDto implements BaseDto {
  @Sheet(index = 0)
  private List<TestExcelDto> sheet1;

  @Sheet(index = 1)
  private List<Test2ExcelDto> sheet2;
}
