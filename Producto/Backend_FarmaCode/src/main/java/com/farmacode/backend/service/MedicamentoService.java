package com.farmacode.backend.service;

import com.farmacode.backend.dto.response.MedicamentoResponseDTO;
import com.farmacode.backend.entity.Medicamento;
import com.farmacode.backend.entity.Precio;
import com.farmacode.backend.exception.ResourceNotFoundException;
import com.farmacode.backend.repository.MedicamentoRepository;
import com.farmacode.backend.repository.PrecioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para operaciones relacionadas con medicamentos.
 * Encapsula la lógica de consulta, filtrado y construcción de DTOs de respuesta.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final PrecioRepository precioRepository;

    /**
     * Retorna todos los medicamentos de forma paginada.
     *
     * @param pageable parámetros de paginación y ordenamiento
     * @return página de medicamentos en formato DTO
     */
    public Page<MedicamentoResponseDTO> getAllMedicamentos(Pageable pageable) {
        return medicamentoRepository.findAll(pageable)
                .map(this::toDTO);
    }

    /**
     * Retorna el detalle de un medicamento por su ID.
     *
     * @param id identificador del medicamento
     * @return DTO con los datos del medicamento
     * @throws ResourceNotFoundException si no existe un medicamento con ese ID
     */
    public MedicamentoResponseDTO getMedicamentoById(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicamento no encontrado con ID: " + id));
        return toDTO(medicamento);
    }

    /**
     * Busca medicamentos cuyo nombre comercial contenga el texto dado.
     *
     * @param nombre fragmento del nombre comercial a buscar
     * @return lista de medicamentos que coincidan parcialmente
     */
    public List<MedicamentoResponseDTO> searchByNombre(String nombre) {
        return medicamentoRepository.findByNombreComercialContainingIgnoreCase(nombre)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna todos los medicamentos que comparten el principio activo dado,
     * ordenados por precio vigente de menor a mayor.
     * Medicamentos sin precio vigente quedan al final de la lista.
     *
     * @param nombrePrincipioActivo nombre del principio activo a buscar
     * @return lista de medicamentos ordenada por precio ascendente
     */
    public List<MedicamentoResponseDTO> getByPrincipioActivo(String nombrePrincipioActivo) {
        return medicamentoRepository.findByPrincipioActivo_NombreIgnoreCase(nombrePrincipioActivo)
                .stream()
                .map(this::toDTO)
                .sorted(Comparator.comparing(
                        dto -> dto.precioActual() != null ? dto.precioActual() : BigDecimal.valueOf(Long.MAX_VALUE)
                ))
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad {@link Medicamento} a su DTO de respuesta,
     * incluyendo el precio vigente actual si existe.
     *
     * @param medicamento entidad a convertir
     * @return DTO de respuesta con todos los datos del medicamento
     */
    public MedicamentoResponseDTO toDTO(Medicamento medicamento) {
        // Obtener el precio vigente más reciente para este medicamento
        BigDecimal precioActual = precioRepository
                .findTopByMedicamento_IdAndVigenteOrderByFechaRegistroDesc(medicamento.getId(), true)
                .map(Precio::getValor)
                .orElse(null);

        return new MedicamentoResponseDTO(
                medicamento.getId(),
                medicamento.getNombreComercial(),
                medicamento.getPrincipioActivo() != null
                        ? medicamento.getPrincipioActivo().getNombre() : null,
                medicamento.getPrincipioActivo() != null
                        ? medicamento.getPrincipioActivo().getCategoria() : null,
                medicamento.getLaboratorio() != null
                        ? medicamento.getLaboratorio().getNombre() : null,
                medicamento.getLaboratorio() != null
                        ? medicamento.getLaboratorio().getPais() : null,
                medicamento.getDosis(),
                medicamento.getPresentacion(),
                medicamento.getAdministracion(),
                medicamento.getTipo() != null
                        ? medicamento.getTipo().getLabel() : null,
                medicamento.getCertIsp(),
                medicamento.getDescripcion(),
                precioActual
        );
    }
}
