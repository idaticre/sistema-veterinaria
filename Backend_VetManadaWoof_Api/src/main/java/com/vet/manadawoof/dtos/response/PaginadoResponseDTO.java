package com.vet.manadawoof.dtos.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

   @Data @Builder @AllArgsConstructor @NoArgsConstructor
   public class PaginadoResponseDTO<T> {
       private List<T> contenido;
       private int paginaActual;
       private int totalPaginas;
       private int totalRegistros;
       private int tamanio;
   }

