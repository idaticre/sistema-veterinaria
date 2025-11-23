package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseWithMetadata<T> {
    private Boolean success;
    private String message;
    private T data;
    private Metadata metadata;
    
    // Constructor simple sin metadata
    public ApiResponseWithMetadata(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.metadata = null;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Metadata {
        private Long timestamp;
        private String version;
        private Integer totalRecords;
        private String operation;
    }
}
