package com.vet.manadawoof.dtos.response;

import lombok.*;

// DTO genérico de respuesta para cualquier operación
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    // Constructor de apoyo cuando no hay datos
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }
}
