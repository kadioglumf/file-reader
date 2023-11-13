package com.kadioglumf.service;

import com.kadioglumf.enums.FileExtension;
import com.kadioglumf.util.excel.ExcelReaderUtil;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class ReaderService<T> {

    private final Class<T> clazz;

    public ReaderService(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> readFile(@NonNull InputStream inputStream, @NonNull String fileExtension, @NonNull boolean isFirstRowHeader) throws Exception {
        if (FileExtension.XLS.getValue().equals(fileExtension)
                || FileExtension.XLSX.getValue().equals(fileExtension)) {
            ExcelReaderUtil readerUtil = new ExcelReaderUtil(inputStream, FileExtension.getFileExtensionByValue(fileExtension));
            return readerUtil.read(clazz, isFirstRowHeader);
        }
        return null;
    }
}