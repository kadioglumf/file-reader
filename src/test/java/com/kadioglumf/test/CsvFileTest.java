package com.kadioglumf.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.kadioglumf.dto.CsvDataDto;
import com.kadioglumf.service.ReaderService;
import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;

@ExtendWith(MockitoExtension.class)
@Log4j2
public class CsvFileTest {

  @InjectMocks private ReaderService readerService;

  @BeforeAll
  static void setup() {
    LocaleContextHolder.setLocale(Locale.forLanguageTag("tr"));
  }

  @Test
  public void shouldGetExcelDtoList() {
    try {
      File file = new File(System.getProperty("user.dir") + "/src/main/resources/test.csv");
      CsvDataDto dataDto =
          readerService.readFile(
              CsvDataDto.class,
              new FileInputStream(file),
              FilenameUtils.getExtension(file.getName()));
      assertNotNull(dataDto, "Csv file should not be null!");
    } catch (Exception ex) {
      log.error("shouldGetExcelDtoList method error: ", ex);
    }
  }
}
