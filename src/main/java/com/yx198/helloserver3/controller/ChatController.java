package com.yx198.helloserver3.controller;

import com.yx198.helloserver3.common.Result;
import com.yx198.helloserver3.model.dto.ChatRequestDTO;
import com.yx198.helloserver3.model.vo.ChatResponseVO;
import com.yx198.helloserver3.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Result<ChatResponseVO> chat(@RequestBody ChatRequestDTO requestDTO) {
        String answer = chatService.chat(requestDTO.getMessage());
        ChatResponseVO responseVO = new ChatResponseVO(requestDTO.getMessage(), answer);
        return Result.success(responseVO);
    }
}