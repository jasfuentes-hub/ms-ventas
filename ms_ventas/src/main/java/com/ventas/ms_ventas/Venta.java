package com.ventas.ms_ventas;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "VENTA")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "CLIENTE", nullable = false)
    private String cliente;

    // Mapeo de la relación uno a muchos
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @Column(name = "TOTAL", nullable = false)
    private double total;

    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;

    // Constructor sin argumentos, necesario para JPA
    public Venta() {}

    // Constructor con argumentos para crear un objeto Venta
    public Venta(String cliente, List<DetalleVenta> detalles, LocalDateTime fecha) {
        this.cliente = cliente;
        this.detalles = detalles;
        this.fecha = fecha;
        // La lógica del total se manejará en el setter
    }
    
    // Método para calcular el total
    public double calcularTotal() {
        if (this.detalles == null) {
            return 0.0;
        }
        return this.detalles.stream()
            .mapToDouble(detalle -> detalle.getSubtotal().doubleValue())
            .sum();
    }

    public BigDecimal getGananciaTotal() {
        return detalles.stream()
            .map(DetalleVenta::getGanancia)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters
    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public double getTotal() { return total; }
    public LocalDateTime getFecha() { return fecha; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    // Este es el método que necesitas modificar.
    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
        // Agregamos la lógica para asegurar la bidireccionalidad.
        if (this.detalles != null) {
            for (DetalleVenta detalle : this.detalles) {
                detalle.setVenta(this);
            }
        }
        // Llamamos a calcularTotal() aquí para que el total se establezca
        // justo después de que la lista de detalles haya sido poblada.
        this.total = calcularTotal();
    }
    
    public void setTotal(double total) { this.total = total; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}