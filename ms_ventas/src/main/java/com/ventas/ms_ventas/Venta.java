package com.ventas.ms_ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Venta {
    private int id;
    private String cliente;
    private List<DetalleVenta> detalles;
    private double total;
    private LocalDateTime fecha;

    public Venta(int id, String cliente, List<DetalleVenta> detalles, LocalDateTime fecha) {
        this.id = id;
        this.cliente = cliente;
        this.detalles = detalles;
        this.fecha = fecha;
        this.total = calcularTotal();
    }

    // Método para calcular el total
    private double calcularTotal() {
        return detalles.stream().mapToDouble(detalle -> detalle.getSubtotal().doubleValue()).sum();
    }
    

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public double getTotal() { return total; }
    public LocalDateTime getFecha() { return fecha; }

    public BigDecimal getGananciaTotal() {
        return detalles.stream()
                .map(detalle -> detalle.getGanancia())
                .reduce(BigDecimal.ZERO, (totalAcumulado, ganancia) -> totalAcumulado.add(ganancia));
    }
}