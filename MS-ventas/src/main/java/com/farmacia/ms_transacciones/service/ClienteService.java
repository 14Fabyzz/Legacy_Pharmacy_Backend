package com.farmacia.ms_transacciones.service;

import com.farmacia.ms_transacciones.model.Cliente;
import java.util.List;
import com.farmacia.ms_transacciones.dto.ClienteDTO;

public interface ClienteService {
    Cliente crearCliente(Cliente cliente);

    ClienteDTO buscarPorIdentificacion(String identificacion);

    List<Cliente> buscarPorNombre(String nombre);

    List<ClienteDTO> listarClientesActivos();

    List<ClienteDTO> buscarClientesDinamicamente(String termino);

    ClienteDTO actualizarCliente(Long id, ClienteDTO clienteActualizado);

    void desactivarCliente(Long id);
}