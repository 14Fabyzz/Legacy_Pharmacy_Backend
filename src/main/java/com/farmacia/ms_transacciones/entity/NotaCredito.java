package com.farmacia.ms_transacciones.entity;

import com.farmacia.ms_transacciones.enums.EstadoNota;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "notas_credito")
@Data
public class NotaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String numeroNota;
    
    @ManyToOne
    @JoinColumn(name = "devolucion_id")
    private Devolucion devolucion;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    private BigDecimal monto;
    private BigDecimal saldo;
    
    private LocalDateTime fechaEmision;
    private LocalDate fechaVencimiento;
    
    @Enumerated(EnumType.STRING)
    private EstadoNota estado;
}