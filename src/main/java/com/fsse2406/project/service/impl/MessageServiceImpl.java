package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.message.dto.MessageRequestDto;
import com.fsse2406.project.data.message.entity.MessageEntity;
import com.fsse2406.project.repository.MessageRepository;
import com.fsse2406.project.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void createMessage(MessageRequestDto request) {
        messageRepository.save(new MessageEntity(request));
    }
}
