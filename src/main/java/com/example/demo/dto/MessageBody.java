package com.example.demo.dto;

import lombok.Data;

@Data
public class MessageBody {
    private String className; // 类名
    private String method;     // 方法名
    private Object[] params;   // 方法参数
}