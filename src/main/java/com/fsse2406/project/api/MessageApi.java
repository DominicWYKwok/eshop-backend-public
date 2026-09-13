package com.fsse2406.project.api;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.data.cart.dto.SuccessResponseDto;
import com.fsse2406.project.data.message.dto.MessageRequestDto;
import com.fsse2406.project.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/message")
@CrossOrigin({EnvConfig.DEV_BASE_URL, EnvConfig.PROD_BASE_URL})
public class MessageApi {
    private final MessageService messageService;

    public MessageApi(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public SuccessResponseDto sendMessage(@Valid @RequestBody MessageRequestDto request) {
        messageService.createMessage(request);
        return new SuccessResponseDto();
    }
}
