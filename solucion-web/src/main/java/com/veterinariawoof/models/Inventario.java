package com.veterinariawoof.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
       
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "stock_actual", nullable = false)
    private int stockActual;

    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;

    @Column(name = "fecha_actualizacion")
    private LocalDate fechaActualizacion;

    private String lote;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    
    // Getters y Setters separados por 1 salto de linea por atributo
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public Producto getProducto() {return producto;}
    public void setProducto(Producto producto) {this.producto = producto;}

    public int getStockActual() {return stockActual;}
    public void setStockActual(int stockActual) {this.stockActual = stockActual;}

    public int getStockMinimo() {return stockMinimo;}
    public void setStockMinimo(int stockMinimo) {this.stockMinimo = stockMinimo;}

    public LocalDate getFechaActualizacion() {return fechaActualizacion;}
    public void setFechaActualizacion(LocalDate fechaActualizacion) {this.fechaActualizacion = fechaActualizacion;}

    public String getLote() {return lote;}
    public void setLote(String lote) {this.lote = lote;}

    public LocalDate getFechaVencimiento() {return fechaVencimiento;}
    public void setFechaVencimiento(LocalDate fechaVencimiento) {this.fechaVencimiento = fechaVencimiento;}
}