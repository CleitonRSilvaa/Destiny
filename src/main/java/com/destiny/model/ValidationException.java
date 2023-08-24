package com.destiny.model;

import lombok.Data;
import java.util.List;

@Data
public class ValidationException extends  RuntimeException{

    private List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}
