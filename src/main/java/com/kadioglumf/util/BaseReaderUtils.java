package com.kadioglumf.util;

import com.kadioglumf.dto.BaseDto;

public abstract class BaseReaderUtils {
  protected abstract <T extends BaseDto> T read(Class<T> clazz) throws Exception;
}
