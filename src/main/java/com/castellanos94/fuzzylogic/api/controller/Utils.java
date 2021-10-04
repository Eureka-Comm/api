package com.castellanos94.fuzzylogic.api.controller;

import com.castellanos94.fuzzylogic.api.model.impl.Generator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class Utils {
    public static boolean isCSVFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "text/csv");
    }

    public static boolean isValidGenerator(Generator generator) {
        if (generator == null)
            return false;
        if (generator.getDepth() == null)
            return false;
        if (generator.getDepth() <= 0)
            return false;
        if (generator.getLabel() == null)
            return false;
        if (generator.getLabel().isBlank())
            return false;
        if (generator.getOperators() == null)
            return false;
        if (generator.getOperators().isEmpty())
            return false;
        if (generator.getVariables() == null)
            return false;
        return !generator.getVariables().isEmpty();
    }
}
