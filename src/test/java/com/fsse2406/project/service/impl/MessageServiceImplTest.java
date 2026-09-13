package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.message.dto.MessageRequestDto;
import com.fsse2406.project.data.message.entity.MessageEntity;
import com.fsse2406.project.repository.MessageRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MessageServiceImplTest {
    private final MessageRepository messageRepository = mock(MessageRepository.class);
    private final MessageServiceImpl messageService = new MessageServiceImpl(messageRepository);

    @Test
    void createMessageSavesSubmittedContactData() {
        MessageRequestDto request = new MessageRequestDto();
        request.setName(" Alex ");
        request.setEmail("alex@example.com");
        request.setPhone("12345678");
        request.setMessage(" I need help ");

        messageService.createMessage(request);

        verify(messageRepository).save(any(MessageEntity.class));
    }
}
