package com.veterinariawoof.services;

import com.veterinariawoof.models.*;
import com.veterinariawoof.models.dto.*;
import com.veterinariawoof.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    private final FacturaCabeceraRepository facturaRepo;
    private final DetalleFacturaRepository detalleRepo;
    private final ClienteRepository clienteRepo;
    private final ProductoRepository productoRepo;
    private final IngresoServicioRepository ingresoRepo;
    private final MedioPagoRepository medioPagoRepo;

    public VentaServiceImpl(FacturaCabeceraRepository facturaRepo, DetalleFacturaRepository detalleRepo,
                            ClienteRepository clienteRepo, ProductoRepository productoRepo,
                            IngresoServicioRepository ingresoRepo, MedioPagoRepository medioPagoRepo) {
        this.facturaRepo = facturaRepo;
        this.detalleRepo = detalleRepo;
        this.clienteRepo = clienteRepo;
        this.productoRepo = productoRepo;
        this.ingresoRepo = ingresoRepo;
        this.medioPagoRepo = medioPagoRepo;
    }

    @Transactional
    @Override
    public void registrarVenta(VentaDTO dto) {
        FacturaCabecera factura = new FacturaCabecera();
        factura.setFechaFactura(dto.getFechaFactura());
        factura.setCliente(clienteRepo.findById(dto.getIdCliente()).orElseThrow());
        factura.setMedioPago(medioPagoRepo.findById(dto.getIdMedioPago()).orElseThrow());
        factura.setObservaciones(dto.getObservaciones());

        List<DetalleFactura> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (DetalleVentaDTO d : dto.getDetalles()) {
            DetalleFactura detalle = new DetalleFactura();
            detalle.setFactura(factura);

            if (d.getIdProducto() != null) {
                Producto p = productoRepo.findById(d.getIdProducto()).orElseThrow();
                detalle.setProducto(p);
            }

            if (d.getIdServicio() != null) {
                IngresoServicio s = ingresoRepo.findById(d.getIdServicio()).orElseThrow();
                detalle.setIngresoServicio(s);
            }

            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            BigDecimal totalItem = d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
            detalle.setTotalItem(totalItem);
            subtotal = subtotal.add(totalItem);

            detalles.add(detalle);
        }

        factura.setDetalles(detalles);
        factura.setSubtotal(subtotal);
        factura.setDescuentos(dto.getDescuentos());
        factura.setImpuestos(subtotal.multiply(BigDecimal.valueOf(0.18))); // IGV 18%
        factura.setTotal(subtotal.subtract(dto.getDescuentos()).add(factura.getImpuestos()));

        facturaRepo.save(factura);
    }
}
