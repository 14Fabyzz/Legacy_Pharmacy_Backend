package com.farmacia.ms_transacciones.entity;

import com.farmacia.ms_transacciones.enums.MotivoDevolucion;
import com.farmacia.ms_transacciones.enums.TipoReembolso;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "devoluciones")
@Data
public class Devolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String numeroDevolucion;
    
    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;
    
    private LocalDateTime fechaDevolucion;
    
    @Enumerated(EnumType.STRING)
    private MotivoDevolucion motivo;
    
    private String descripcionMotivo;
    private BigDecimal totalDevolucion;

    private TipoReembolso tipoReembolso;
    
    private String destinoStock;
    private String vendedorAutorizaId;
    private String estado;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}