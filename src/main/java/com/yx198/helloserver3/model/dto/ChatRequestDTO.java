package com.yx198.helloserver3.model.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private String sessionId;  // 新增：会话编号
    private String message;     // 用户输入
}