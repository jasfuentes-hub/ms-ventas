package com.ventas.ms_ventas;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> buscarPorId(int id) {
        return ventaRepository.findById(id);
    }
    
    public Venta guardarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }
    
    // --- MÉTODOS DE ACTUALIZACIÓN Y ELIMINACIÓN ---
    
    public Venta actualizarVenta(int id, Venta ventaActualizada) {
        // Busca la venta existente por su ID
        Venta ventaExistente = ventaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));

        // Actualiza los campos de la venta principal
        ventaExistente.setCliente(ventaActualizada.getCliente());
        ventaExistente.setFecha(ventaActualizada.getFecha());
        // El total se recalculará automáticamente con el setter de detalles.

        // Maneja la actualización de la lista de detalles de manera segura
        ventaExistente.getDetalles().clear(); // Elimina todos los detalles existentes
        
        // Agrega los nuevos detalles a la lista de la venta existente
        if (ventaActualizada.getDetalles() != null) {
            for (DetalleVenta nuevoDetalle : ventaActualizada.getDetalles()) {
                nuevoDetalle.setVenta(ventaExistente); // Establece la relación bidireccional
                ventaExistente.getDetalles().add(nuevoDetalle);
            }
        }

        // Guarda la venta actualizada. JPA se encargará de los detalles
        return ventaRepository.save(ventaExistente);
    }
    
    public void eliminarVenta(int id) {
        ventaRepository.deleteById(id);
    }
    
    // --- MÉTODOS DE CÁLCULO ---

    public BigDecimal getGananciasDiarias(LocalDate fecha) {
        return ventaRepository.findAll().stream()
                .filter(venta -> venta.getFecha().toLocalDate().isEqual(fecha))
                .map(Venta::getGananciaTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getGananciasMensuales(int mes, int anio) {
        return ventaRepository.findAll().stream()
                .filter(venta -> venta.getFecha().getMonthValue() == mes && venta.getFecha().getYear() == anio)
                .map(Venta::getGananciaTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getGananciasAnuales(int anio) {
        return ventaRepository.findAll().stream()
                .filter(venta -> venta.getFecha().getYear() == anio)
                .map(Venta::getGananciaTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}