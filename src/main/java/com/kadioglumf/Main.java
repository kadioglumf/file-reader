package com.kadioglumf;

import com.kadioglumf.dto.excel.TestExcelDto;
import com.kadioglumf.enums.FileExtension;
import com.kadioglumf.service.ReaderService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;

public class Main {



    public static void main(String[] args) throws Exception {
        LocaleContextHolder.setLocale(new Locale("tr", "TR"));

        File file = new File(System.getProperty("user.dir") + "/src/main/resources/test.xlsx");
        ReaderService<TestExcelDto> readerService = new ReaderService<>(TestExcelDto.class);

        List<TestExcelDto> list =readerService.readFile(new FileInputStream(file), FilenameUtils.getExtension(file.getName()), true);
        list.forEach(System.out::println);
    }
}