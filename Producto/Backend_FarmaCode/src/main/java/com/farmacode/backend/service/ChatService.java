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
            "FORMATO: Responde SIEMPRE en texto plano, sin markdown, sin asteriscos, sin negritas, " +
            "sin guiones de lista al inicio. Usa saltos de línea para separar ideas. " +
            "Cuando enumeres opciones, escríbelas como '1.', '2.', '3.' sin asteriscos ni guiones. " +
            "AVISO MÉDICO: Cuando la pregunta involucre diagnóstico, tratamiento de enfermedades " +
            "específicas, síntomas, o dosis exactas para un paciente concreto, al final de tu respuesta " +
            "agrega siempre: " +
            "\"⚠️ Esta información es solo orientativa. Para un diagnóstico o tratamiento específico, " +
            "consulta con un médico o profesional de la salud.\"";

    public ChatResponseDTO chat(ChatRequestDTO request) {
        String respuesta = geminiApiService.chatFarmaceutico(
                request.historial(), request.mensaje(), SYSTEM_PROMPT);
        return new ChatResponseDTO(respuesta);
    }
}
