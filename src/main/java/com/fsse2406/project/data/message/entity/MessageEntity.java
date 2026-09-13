package com.fsse2406.project.data.message.entity;

import com.fsse2406.project.data.message.dto.MessageRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Document(collection = "message")
public class MessageEntity {
    @Id
    private String id;

    private String name;
    private String email;
    private String phone;
    private String message;
    private Instant createdAt;

    public MessageEntity(MessageRequestDto request) {
        this.name = request.getName().trim();
        this.email = request.getEmail().trim();
        this.phone = request.getPhone().trim();
        this.message = request.getMessage().trim();
        this.createdAt = Instant.now();
    }
}
