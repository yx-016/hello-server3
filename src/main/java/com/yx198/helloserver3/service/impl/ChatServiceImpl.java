package com.yx198.helloserver3.service.impl;

import com.yx198.helloserver3.model.dto.ChatRequestDTO;
import com.yx198.helloserver3.model.vo.ChatResponseVO;
import com.yx198.helloserver3.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, StringRedisTemplate stringRedisTemplate) {
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一名专业、友好、简洁的中文智能助手，请结合历史对话回答用户问题。")
                .build();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public ChatResponseVO chat(ChatRequestDTO requestDTO) {
        String sessionId = requestDTO.getSessionId();
        String message = requestDTO.getMessage();

        if (sessionId == null || sessionId.isEmpty()) {
            return new ChatResponseVO(message, "错误：sessionId 不能为空");
        }

        String redisKey = "chat:session:" + sessionId;

        // 读取历史消息
        List<String> records = stringRedisTemplate.opsForList().range(redisKey, -6, -1);

        StringBuilder historyContext = new StringBuilder();
        if (records != null && !records.isEmpty()) {
            historyContext.append("以下是历史对话：\n");
            for (String record : records) {
                historyContext.append(record).append("\n");
            }
            historyContext.append("\n");
        }

        String fullPrompt = historyContext.toString() + "当前用户问题：" + message;

        String answer;
        try {
            answer = chatClient.prompt()
                    .user(fullPrompt)
                    .call()
                    .content();
        } catch (Exception e) {
            e.printStackTrace();
            answer = "抱歉，调用大模型时出现错误：" + e.getMessage();
        }

        String recordText = "用户：" + message + "\n助手：" + answer;
        stringRedisTemplate.opsForList().rightPush(redisKey, recordText);

        Long size = stringRedisTemplate.opsForList().size(redisKey);
        if (size != null && size > 6) {
            stringRedisTemplate.opsForList().trim(redisKey, -6, -1);
        }

        stringRedisTemplate.expire(redisKey, java.time.Duration.ofMinutes(30));

        return new ChatResponseVO(message, answer);
    }
}