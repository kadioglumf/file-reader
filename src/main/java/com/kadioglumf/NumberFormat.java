package com.kadioglumf;

import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Getter
public class NumberFormat {

    private final Locale locale;
    private final DecimalFormatSymbols symbols;


    public NumberFormat() {
        this.locale = LocaleContextHolder.getLocale();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        if ("tr".equals(locale.getLanguage())) {
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');
        }
        this.symbols = symbols;
    }
}
