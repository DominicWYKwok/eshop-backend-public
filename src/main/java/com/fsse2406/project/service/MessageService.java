package com.fsse2406.project.service;

import com.fsse2406.project.data.message.dto.MessageRequestDto;

public interface MessageService {
    void createMessage(MessageRequestDto request);
}
