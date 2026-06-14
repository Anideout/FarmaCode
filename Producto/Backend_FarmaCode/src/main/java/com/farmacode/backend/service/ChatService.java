package com.farmacode.backend.service;

import com.farmacode.backend.dto.request.ChatRequestDTO;
import com.farmacode.backend.dto.response.ChatResponseDTO;
import com.farmacode.backend.service.external.GeminiApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final GeminiApiService geminiApiService;

    private static final String SYSTEM_PROMPT =
            "Eres un asistente farmacéutico especializado integrado en la aplicación FarmaCode. " +
            "SOLO puedes responder preguntas relacionadas con: medicamentos, principios activos, " +
            "dosis, efectos secundarios, interacciones medicamentosas, contraindicaciones, " +
            "alternativas genéricas, bioequivalentes, laboratorios farmacéuticos, " +
            "vías de administración, certificaciones ISP, y temas de salud directamente " +
            "vinculados a fármacos. " +
            "Si el usuario pregunta sobre cualquier tema NO relacionado con medicamentos o farmacología, " +
            "responde educadamente que solo puedes ayudar con consultas farmacéuticas. " +
            "Responde SIEMPRE en el mismo idioma que usa el usuario. " +
            "Sé conciso, claro y profesional. " +
            "IMPORTANTE: No reemplazas a un médico. Si el usuario describe síntomas graves, " +
            "indícale que consulte a un profesional de salud.";

    public ChatResponseDTO chat(ChatRequestDTO request) {
        String respuesta = geminiApiService.chatFarmaceutico(
                request.historial(), request.mensaje(), SYSTEM_PROMPT);
        return new ChatResponseDTO(respuesta);
    }
}
