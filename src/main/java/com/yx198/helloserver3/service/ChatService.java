package com.yx198.helloserver3.service;

import com.yx198.helloserver3.model.dto.ChatRequestDTO;
import com.yx198.helloserver3.model.vo.ChatResponseVO;

public interface ChatService {
    ChatResponseVO chat(ChatRequestDTO requestDTO);  // 改为接收 DTO
}