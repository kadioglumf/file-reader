package com.kadioglumf.reader;

import com.kadioglumf.dto.BaseDto;
import com.kadioglumf.enums.FileExtension;
import com.kadioglumf.reader.cvs.CsvFileReader;
import com.kadioglumf.reader.excel.ExcelFileReader;
import java.io.InputStream;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ReaderService {

  public <T extends BaseDto> T readFile(
      @NonNull Class<T> clazz, @NonNull InputStream inputStream, @NonNull String fileExtension)
      throws Exception {
    if (FileExtension.XLS.getValue().equals(fileExtension)
        || FileExtension.XLSX.getValue().equals(fileExtension)) {
      ExcelFileReader readerUtil =
          new ExcelFileReader(inputStream, FileExtension.getFileExtensionByValue(fileExtension));
      return readerUtil.read(clazz);
    } else if (FileExtension.CSV.getValue().equals(fileExtension)) {
      CsvFileReader readerUtil = new CsvFileReader(inputStream, clazz);
      return readerUtil.read(clazz);
    }
    return null;
  }
}
