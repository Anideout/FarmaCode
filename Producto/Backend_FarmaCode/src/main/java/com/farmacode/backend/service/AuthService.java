package com.farmacode.backend.service;

import com.farmacode.backend.dto.request.LoginRequestDTO;
import com.farmacode.backend.dto.request.RegisterRequestDTO;
import com.farmacode.backend.dto.response.AuthResponseDTO;
import com.farmacode.backend.entity.Usuario;
import com.farmacode.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .activo(true)
                .build();
        usuario = usuarioRepository.save(usuario);
        return new AuthResponseDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas"));
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }
        return new AuthResponseDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }
}
