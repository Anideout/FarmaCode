package com.farmacode.backend.controller;

import com.farmacode.backend.dto.request.ChatRequestDTO;
import com.farmacode.backend.dto.response.ChatResponseDTO;
import com.farmacode.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequestDTO request) {
        return ResponseEntity.ok(chatService.chat(request));
    }
}
