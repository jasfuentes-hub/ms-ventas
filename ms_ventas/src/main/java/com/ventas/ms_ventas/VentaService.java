package com.ventas.ms_ventas;

import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VentaService {

    private final List<Venta> listaVentas;

    public VentaService(List<Venta> listaVentas) {
        this.listaVentas = listaVentas;
    }

        // Método para listar todas las ventas
    public List<Venta> listarTodas() {
        return listaVentas;
    }

    // Método para buscar una venta por ID
    public Optional<Venta> buscarPorId(int id) {
        return listaVentas.stream()
                .filter(venta -> venta.getId() == id)
                .findFirst();
    }

    public BigDecimal getGananciasDiarias(LocalDate fecha) {
        return listaVentas.stream()
                .filter(venta -> venta.getFecha().toLocalDate().isEqual(fecha))
                .map(venta -> venta.getGananciaTotal())
                .reduce(BigDecimal.ZERO, (total, ganancia) -> total.add(ganancia));
    }

    public BigDecimal getGananciasMensuales(int mes, int anio) {
        return listaVentas.stream()
                .filter(venta -> venta.getFecha().getMonthValue() == mes && venta.getFecha().getYear() == anio)
                .map(venta -> venta.getGananciaTotal())
                .reduce(BigDecimal.ZERO, (total, ganancia) -> total.add(ganancia));
    }

    public BigDecimal getGananciasAnuales(int anio) {
        return listaVentas.stream()
                .filter(venta -> venta.getFecha().getYear() == anio)
                .map(venta -> venta.getGananciaTotal())
                .reduce(BigDecimal.ZERO, (total, ganancia) -> total.add(ganancia));
    }
}