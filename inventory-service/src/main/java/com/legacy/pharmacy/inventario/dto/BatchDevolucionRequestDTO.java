package com.legacy.pharmacy.inventario.dto;

import java.util.List;

public class BatchDevolucionRequestDTO {
    private String documentoRef;
    private List<BatchItemDevolucionDTO> items;

    public BatchDevolucionRequestDTO() {
    }

    public BatchDevolucionRequestDTO(String documentoRef, List<BatchItemDevolucionDTO> items) {
        this.documentoRef = documentoRef;
        this.items = items;
    }

    public String getDocumentoRef() {
        return documentoRef;
    }

    public void setDocumentoRef(String documentoRef) {
        this.documentoRef = documentoRef;
    }

    public List<BatchItemDevolucionDTO> getItems() {
        return items;
    }

    public void setItems(List<BatchItemDevolucionDTO> items) {
        this.items = items;
    }
}
