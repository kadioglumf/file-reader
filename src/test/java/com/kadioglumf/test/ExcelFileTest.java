package com.kadioglumf.test;

import com.kadioglumf.dto.TestExcelDto;
import com.kadioglumf.service.ReaderService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@Log4j2
public class ExcelFileTest {

    @InjectMocks
    private ReaderService readerService;

    @BeforeAll
    static void setup() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("tr"));
    }

    @Test
    public void shouldGetExcelDtoList() {
        try {
            File file = new File(System.getProperty("user.dir") + "/src/main/resources/test.xlsx");
            List<TestExcelDto> list = readerService.readFile(TestExcelDto.class, new FileInputStream(file), FilenameUtils.getExtension(file.getName()));
            assertNotNull(list, "Excel file should not be null!");
            list.forEach(System.out::println);
        }
        catch (Exception ex) {
            log.error("shouldGetExcelDtoList method error: ", ex);
        }
    }
}
