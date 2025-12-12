package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.dto.response.NotaCreditoResponseDTO;
import com.farmacia.ms_transacciones.entity.Devolucion;
import com.farmacia.ms_transacciones.entity.NotaCredito;
import com.farmacia.ms_transacciones.entity.Venta;
import com.farmacia.ms_transacciones.enums.EstadoNota;
import com.farmacia.ms_transacciones.repository.DevolucionRepository;
import com.farmacia.ms_transacciones.repository.NotaCreditoRepository;
import com.farmacia.ms_transacciones.service.NotaCreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotaCreditoServiceImpl implements NotaCreditoService {
    private final NotaCreditoRepository notaCreditoRepository;
    private final DevolucionRepository devolucionRepository;

    @Override
    @Transactional
    public NotaCreditoResponseDTO generarNotaCredito(Long devolucionId) {
        Devolucion devolucion = devolucionRepository.findById(devolucionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Devolución no encontrada con ID: " + devolucionId));
        // AÑADE ESTAS LÍNEAS TEMPORALES
        Venta ventaAsociada = devolucion.getVenta();
        System.out.println("DEBUG >>> Venta ID: " + ventaAsociada.getId());
        if (ventaAsociada.getCliente() != null) {
            System.out.println("DEBUG >>> Cliente ID: " + ventaAsociada.getCliente().getId() + " - Nombre: " + ventaAsociada.getCliente().getNombre());
        } else {
            System.out.println("DEBUG >>> ¡ALERTA! EL CLIENTE SIGUE SIENDO NULO.");
        }
        // ***************** FIN DE LOG DE DIAGNÓSTICO *****************

        if (!"APROBADA".equals(devolucion.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden generar notas de crédito para devoluciones aprobadas");
        }

        // ***************************************************************
        // ******* CORRECCIÓN CLAVE: Validar que el cliente exista *******
        // ***************************************************************
        if (devolucion.getVenta().getCliente() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ERROR de Negocio: No se puede emitir una nota de crédito. La venta asociada (ID: "
                            + devolucion.getVenta().getId()
                            + ") se hizo a un cliente anónimo. Las notas de crédito deben asociarse a un cliente registrado.");
        }
        // ***************************************************************


        NotaCredito notaCredito = new NotaCredito();
        notaCredito.setDevolucion(devolucion);
        notaCredito.setCliente(devolucion.getVenta().getCliente());
        notaCredito.setNumeroNota(generarNumeroNotaCredito());
        notaCredito.setMonto(devolucion.getTotalDevolucion());
        notaCredito.setSaldo(devolucion.getTotalDevolucion());
        notaCredito.setFechaEmision(LocalDateTime.now());
        notaCredito.setFechaVencimiento(LocalDate.now().plusMonths(3)); // Validez de 3 meses
        notaCredito.setEstado(EstadoNota.ACTIVA);

        return convertirADTO(notaCreditoRepository.save(notaCredito));
    }

    @Override
    public NotaCreditoResponseDTO obtenerPorId(Long id) {
        return convertirADTO(notaCreditoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nota de crédito no encontrada con ID: " + id)));
    }

    @Override
    public Page<NotaCreditoResponseDTO> listarNotasCredito(Pageable pageable) {
        return notaCreditoRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<NotaCreditoResponseDTO> buscarPorCliente(Long clienteId, Pageable pageable) {
        // Nota: Asumiendo que findByClienteId devuelve List<NotaCredito>
        return notaCreditoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertirADTO)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    @Override
    @Transactional
    public NotaCreditoResponseDTO aplicarNotaCredito(Long id, Double montoAplicar) {
        NotaCredito notaCredito = notaCreditoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nota de crédito no encontrada con ID: " + id));

        if (notaCredito.getEstado() != EstadoNota.ACTIVA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La nota de crédito no está activa");
        }

        BigDecimal montoAplicarBD = BigDecimal.valueOf(montoAplicar);
        if (montoAplicarBD.compareTo(notaCredito.getSaldo()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El monto a aplicar es mayor que el saldo disponible");
        }

        notaCredito.setSaldo(notaCredito.getSaldo().subtract(montoAplicarBD));

        if (notaCredito.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            notaCredito.setEstado(EstadoNota.USADA);
        }

        return convertirADTO(notaCreditoRepository.save(notaCredito));
    }

    private String generarNumeroNotaCredito() {
        return "NC-" + System.currentTimeMillis();
    }

    private NotaCreditoResponseDTO convertirADTO(NotaCredito notaCredito) {
        NotaCreditoResponseDTO dto = new NotaCreditoResponseDTO();
        dto.setId(notaCredito.getId());
        dto.setNumeroNota(notaCredito.getNumeroNota());
        // Se asume que Devolucion está precargada por el JpaRepository
        dto.setNumeroDevolucion(notaCredito.getDevolucion().getNumeroDevolucion());

        // *** CORRECCIÓN NPE aquí ***
        if (notaCredito.getCliente() != null) {
            dto.setClienteId(notaCredito.getCliente().getId());
            dto.setClienteNombre(notaCredito.getCliente().getNombre());
        } else {
            // Manejo seguro en caso de que la nota se genere en el futuro sin cliente (aunque la validación lo impide)
            dto.setClienteId(null);
            dto.setClienteNombre("Cliente Anónimo");
        }
        // ***************************

        dto.setMonto(notaCredito.getMonto());
        dto.setSaldo(notaCredito.getSaldo());
        dto.setFechaEmision(notaCredito.getFechaEmision());
        dto.setFechaVencimiento(notaCredito.getFechaVencimiento());
        dto.setEstado(notaCredito.getEstado());
        return dto;
    }
}