package com.ventas.ms_ventas;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "DETALLE_VENTA")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id; // Se necesita una clave primaria para esta entidad

    @Column(name = "PRODUCTO", nullable = false)
    private String producto;

    @Column(name = "CANTIDAD", nullable = false)
    private int cantidad;

    @Column(name = "PRECIO", nullable = false)
    private BigDecimal precio;

    @Column(name = "COSTO", nullable = false)
    private BigDecimal costo;
    
    // Mapeo de la relación muchos a uno
    @ManyToOne
    @JoinColumn(name = "ID_VENTA", nullable = false) 
    @JsonIgnore
    private Venta venta;

    // Constructor sin argumentos
    public DetalleVenta() {}

    // Constructor con argumentos para crear un objeto DetalleVenta
    public DetalleVenta(String producto, int cantidad, BigDecimal precio, BigDecimal costo) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.costo = costo;
    }

    public BigDecimal getSubtotal() {
        return this.precio.multiply(new BigDecimal(this.cantidad));
    }

    public BigDecimal getGanancia() {
        BigDecimal gananciaUnitario = this.precio.subtract(this.costo);
        return gananciaUnitario.multiply(new BigDecimal(this.cantidad));
    }
    
    // Getters
    public int getId() { return id; }
    public String getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecio() { return precio; }
    public BigDecimal getCosto() { return costo; }
    public Venta getVenta() { return venta; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProducto(String producto) { this.producto = producto; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
    public void setVenta(Venta venta) { this.venta = venta; }
}