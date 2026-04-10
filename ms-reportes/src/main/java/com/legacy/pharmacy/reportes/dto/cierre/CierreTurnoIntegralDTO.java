package com.legacy.pharmacy.reportes.dto.cierre;

import java.util.List;

public class CierreTurnoIntegralDTO {
    private CierreTurnoConciliacionDTO encabezado;
    private List<MovimientoTurnoDTO> movimientos;

    public CierreTurnoIntegralDTO() {}

    public CierreTurnoIntegralDTO(CierreTurnoConciliacionDTO encabezado, List<MovimientoTurnoDTO> movimientos) {
        this.encabezado = encabezado;
        this.movimientos = movimientos;
    }

    public CierreTurnoConciliacionDTO getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(CierreTurnoConciliacionDTO encabezado) {
        this.encabezado = encabezado;
    }

    public List<MovimientoTurnoDTO> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoTurnoDTO> movimientos) {
        this.movimientos = movimientos;
    }
}
