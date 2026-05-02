package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private String msg;
    private T data;
}
