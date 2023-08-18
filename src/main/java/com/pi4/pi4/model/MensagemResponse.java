package com.pi4.pi4.model;

import lombok.Data;

import java.util.List;

@Data
public class MensagemResponse {
    private int status;
    private String message;
    private List<String> details;

}
