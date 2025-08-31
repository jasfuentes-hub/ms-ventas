package com.ventas.ms_ventas;

import java.math.BigDecimal;

public class DetalleVenta {
    private String producto;
    private int cantidad;
    private BigDecimal precio;
    private BigDecimal costo;

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
    
    public String getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecio() { return precio; }
    public BigDecimal getCosto() { return costo; }
}