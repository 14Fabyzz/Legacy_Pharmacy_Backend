package com.farmacia.ms_transacciones.service;
import com.farmacia.ms_transacciones.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    Cliente crearCliente(Cliente cliente);
    Cliente buscarPorIdentificacion(String identificacion);
    List<Cliente> buscarPorNombre(String nombre);
}