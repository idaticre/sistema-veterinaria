package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ProductoRequestDTO;
import com.vet.manadawoof.dtos.response.ProductoResponseDTO;
import com.vet.manadawoof.service.ProductoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------------------------------
    // GET /productos
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public List<ProductoResponseDTO> listar() {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_productos_listar");
        sp.execute();
        return mapResultList(sp.getResultList());
    }

    // ----------------------------------------------------------------
    // GET /productos?proveedor={proveedorId}
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public List<ProductoResponseDTO> listarPorProveedor(Long proveedorId) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_productos_listar_por_proveedor");
        sp.registerStoredProcedureParameter("p_proveedor_id", Long.class, ParameterMode.IN);
        sp.setParameter("p_proveedor_id", proveedorId);
        sp.execute();
        return mapResultList(sp.getResultList());
    }

    // ----------------------------------------------------------------
    // POST /productos
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public ProductoResponseDTO registrar(ProductoRequestDTO request) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_productos_registrar");
        sp.registerStoredProcedureParameter("p_nombre",       String.class,     ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_descripcion",  String.class,     ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_marca",        String.class,     ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_precio",       BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_stock",        Integer.class,    ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_proveedor_id", Long.class,       ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_foto",         String.class,     ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id",           Long.class,       ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo",       String.class,     ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje",      String.class,     ParameterMode.OUT);

        sp.setParameter("p_nombre",       request.getNombre());
        sp.setParameter("p_descripcion",  request.getDescripcion());
        sp.setParameter("p_marca",        request.getMarca());
        sp.setParameter("p_precio",       request.getPrecio());
        sp.setParameter("p_stock",        request.getStock());
        sp.setParameter("p_proveedor_id", request.getProveedorId());
        sp.setParameter("p_foto",         request.getFoto());

        sp.execute();

        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if (mensaje != null && mensaje.startsWith("ERROR")) {
            return ProductoResponseDTO.builder().mensaje(mensaje).build();
        }

        Long nuevoId   = (Long)   sp.getOutputParameterValue("p_id");
        String codigo  = (String) sp.getOutputParameterValue("p_codigo");

        return ProductoResponseDTO.builder()
                .id(nuevoId)
                .codigo(codigo)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .marca(request.getMarca())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .proveedorId(request.getProveedorId())
                .foto(request.getFoto())
                .activo(true)
                .mensaje(mensaje)
                .build();
    }

    // ----------------------------------------------------------------
    // PUT /productos/{id}
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request) {
        em.createNativeQuery(
        "CALL sp_productos_actualizar(:p_id, :p_nombre, :p_descripcion, :p_marca, " +
        ":p_precio, :p_stock, :p_proveedor_id, :p_foto, :p_activo, @p_mensaje)")
        .setParameter("p_id",           id)
        .setParameter("p_nombre",       request.getNombre())
        .setParameter("p_descripcion",  request.getDescripcion())
        .setParameter("p_marca",        request.getMarca())
        .setParameter("p_precio",       request.getPrecio())
        .setParameter("p_stock",        request.getStock())
        .setParameter("p_proveedor_id", request.getProveedorId())
        .setParameter("p_foto",         request.getFoto())
        .setParameter("p_activo",       request.getActivo() != null ? request.getActivo() : true)
        .executeUpdate();

        String mensaje = (String) em.createNativeQuery("SELECT @p_mensaje").getSingleResult();

        if (mensaje != null && mensaje.startsWith("ERROR")) {
            return ProductoResponseDTO.builder().mensaje(mensaje).build();
        }

        return ProductoResponseDTO.builder()
                .id(id)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .marca(request.getMarca())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .proveedorId(request.getProveedorId())
                .foto(request.getFoto())
                .activo(request.getActivo())
                .mensaje(mensaje)
                .build();
    }

    // ----------------------------------------------------------------
    // DELETE /productos/{id} (lógico)
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public ProductoResponseDTO eliminar(Long id) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_productos_eliminar");
        sp.registerStoredProcedureParameter("p_id",      Long.class,   ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        sp.setParameter("p_id", id);
        sp.execute();

        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        return ProductoResponseDTO.builder()
                .id(id)
                .activo(false)
                .mensaje(mensaje)
                .build();
    }

    // ----------------------------------------------------------------
    // Mapea el Object[] de los SPs SELECT a ProductoResponseDTO
    // Orden: id, codigo, nombre, descripcion, marca, precio, stock,
    //        proveedor_id, codigo_proveedor, nombre_proveedor, foto, activo
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private List<ProductoResponseDTO> mapResultList(List<Object[]> rows) {
        return rows.stream().map(row -> ProductoResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .codigo((String) row[1])
                .nombre((String) row[2])
                .descripcion((String) row[3])
                .marca((String) row[4])
                .precio(row[5] != null ? new BigDecimal(row[5].toString()) : null)
                .stock(row[6] != null ? ((Number) row[6]).intValue() : 0)
                .proveedorId(((Number) row[7]).longValue())
                .nombreProveedor((String) row[9])
                .foto((String) row[10])
                .activo(row[11] != null ? (Boolean) row[11] : true)
                .build()
        ).toList();
    }
}
