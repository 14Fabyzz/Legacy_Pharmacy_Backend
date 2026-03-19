package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.dto.ClienteDTO;
import com.farmacia.ms_transacciones.model.Cliente;
import com.farmacia.ms_transacciones.repository.ClienteRepository;
import com.farmacia.ms_transacciones.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    @Transactional
    public Cliente crearCliente(Cliente cliente) {
        // Validación: No crear si ya existe la cédula
        if (clienteRepository.findByNumeroIdentificacion(cliente.getNumeroIdentificacion()).isPresent()) {
            throw new RuntimeException("El cliente ya existe con esa identificación.");
        }

        // Asignación de campos por defecto
        cliente.setActivo(true);
        cliente.setEstado("ACTIVO");
        cliente.setCreatedAt(java.time.LocalDateTime.now());
        cliente.setUpdatedAt(java.time.LocalDateTime.now());

        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO buscarPorIdentificacion(String identificacion) {
        // 1. Buscamos (Si no existe, lanza error 404 explícito)
        Cliente cliente = clienteRepository.findByNumeroIdentificacion(identificacion)
                .orElseThrow(
                        () -> new RuntimeException("Cliente con identificación " + identificacion + " no encontrado"));

        // 2. Mapeamos a DTO (Para devolver JSON limpio)
        return mapearADTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNombre(String nombre) {
        List<Cliente> clientes = clienteRepository.findByNombreContainingIgnoreCase(nombre);

        // Validación agregada: si la lista está vacía, lanza excepción
        if (clientes.isEmpty()) {
            throw new RuntimeException("No se encontraron clientes con el nombre: " + nombre);
        }

        return clientes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarClientesActivos() {
        return clienteRepository.findByActivoTrue().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarClientesDinamicamente(String termino) {
        return clienteRepository.buscarDinamicamenteActivos(termino).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteActualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));

        validarClienteProtegido(cliente);

        // Validar que la nueva identificación no pertenezca a otro cliente
        if (!cliente.getNumeroIdentificacion().equals(clienteActualizado.getNumeroIdentificacion())) {
            if (clienteRepository.findByNumeroIdentificacion(clienteActualizado.getNumeroIdentificacion())
                    .isPresent()) {
                throw new RuntimeException("Ya existe otro cliente con la identificación: "
                        + clienteActualizado.getNumeroIdentificacion());
            }
        }

        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setApellido(clienteActualizado.getApellido());
        cliente.setNumeroIdentificacion(clienteActualizado.getNumeroIdentificacion());
        cliente.setEmail(clienteActualizado.getEmail());
        cliente.setTelefono(clienteActualizado.getTelefono());
        cliente.setTipoCliente(clienteActualizado.getTipoCliente());

        // Mapeo del estado
        if (clienteActualizado.getActivo() != null) {
            cliente.setActivo(clienteActualizado.getActivo());
            cliente.setEstado(clienteActualizado.getActivo() ? "ACTIVO" : "INACTIVO");
        } else if (clienteActualizado.getEstado() != null) {
            cliente.setEstado(clienteActualizado.getEstado());
            cliente.setActivo("ACTIVO".equalsIgnoreCase(clienteActualizado.getEstado()));
        }

        cliente.setUpdatedAt(java.time.LocalDateTime.now());

        Cliente guardado = clienteRepository.save(cliente);
        return mapearADTO(guardado);
    }

    @Override
    @Transactional
    public void desactivarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));

        validarClienteProtegido(cliente);

        cliente.setActivo(false);
        cliente.setEstado("INACTIVO");
        cliente.setUpdatedAt(java.time.LocalDateTime.now());
        clienteRepository.save(cliente);
    }

    private void validarClienteProtegido(Cliente cliente) {
        if ("999999999".equals(cliente.getNumeroIdentificacion()) ||
                "9999999999".equals(cliente.getNumeroIdentificacion()) ||
                "CONSUMIDOR FINAL".equalsIgnoreCase(cliente.getNombre())) {
            throw new RuntimeException(
                    "El cliente CONSUMIDOR FINAL es de uso del sistema y no puede ser modificado ni desactivado.");
        }
    }

    private ClienteDTO mapearADTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNumeroIdentificacion(cliente.getNumeroIdentificacion());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setTipoCliente(cliente.getTipoCliente());
        dto.setEstado(cliente.getEstado());
        dto.setActivo(cliente.getActivo());
        return dto;
    }
}