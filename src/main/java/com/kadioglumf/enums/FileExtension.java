package com.kadioglumf.enums;

public enum FileExtension {
    XLS("xls"),
    XLSX("xlsx"),
    CSV("csv"),
    PDF("pdf");

    private final String value;

    FileExtension(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FileExtension getFileExtensionByValue(String value) throws Exception {
        for (FileExtension extension : values()) {
            if (extension.getValue().equals(value)) {
                return extension;
            }
        }
        throw new Exception(); //TODO
    }
}
