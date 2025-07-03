package com.kadioglumf.dto;

import java.util.List;
import lombok.Data;

@Data
public class CsvDataDto implements BaseDto {
  private List<TestCsvDto> data;
}
