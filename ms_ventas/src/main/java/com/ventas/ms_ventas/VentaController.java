package com.ventas.ms_ventas;
import org.springframework.format.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final List<Venta> listaVentas = new ArrayList<>();

    public VentaController() {

        listaVentas.add(new Venta(1, "Cliente Pérez", List.of(
            new DetalleVenta("Taste of the Wild 13.6kg", 1, new BigDecimal("45990"), new BigDecimal("32000"))
        ), LocalDateTime.of(2025, 8, 28, 10, 30)));

  
        listaVentas.add(new Venta(2, "Cliente García", List.of(
            new DetalleVenta("Pack de 3 pelotitas", 1, new BigDecimal("3990"), new BigDecimal("1800")),
            new DetalleVenta("Túnel de juegos para gato", 1, new BigDecimal("15990"), new BigDecimal("9000"))
        ), LocalDateTime.of(2025, 8, 28, 12, 0)));


        listaVentas.add(new Venta(3, "Cliente López", List.of(
            new DetalleVenta("Shampoo de avena 500ml", 1, new BigDecimal("7500"), new BigDecimal("4000")),
            new DetalleVenta("Huesos dentales (pack)", 3, new BigDecimal("4990"), new BigDecimal("2500"))
        ), LocalDateTime.of(2025, 8, 29, 14, 15)));


        listaVentas.add(new Venta(4, "Cliente Morales", List.of(
            new DetalleVenta("Arena sanitaria aglomerante 10kg", 2, new BigDecimal("9990"), new BigDecimal("6000")),
            new DetalleVenta("Bebedero automático", 1, new BigDecimal("22500"), new BigDecimal("15000"))
        ), LocalDateTime.of(2025, 8, 29, 16, 20)));


        listaVentas.add(new Venta(5, "Cliente Rojas", List.of(
            new DetalleVenta("Cepillo de cerdas para perro", 1, new BigDecimal("4990"), new BigDecimal("2500"))
        ), LocalDateTime.of(2025, 8, 30, 9, 0)));
        

        listaVentas.add(new Venta(6, "Cliente Silva", List.of(
            new DetalleVenta("Juguete de cuerda", 5, new BigDecimal("2990"), new BigDecimal("1500"))
        ), LocalDateTime.of(2025, 8, 30, 11, 30)));

        listaVentas.add(new Venta(7, "Cliente Muñoz", List.of(
            new DetalleVenta("Cama para perro grande", 1, new BigDecimal("38000"), new BigDecimal("25000"))
        ), LocalDateTime.of(2025, 9, 1, 10, 0)));
        

        listaVentas.add(new Venta(8, "Cliente Tapia", List.of(
            new DetalleVenta("Viruta de madera para jaula", 2, new BigDecimal("2500"), new BigDecimal("1000")),
            new DetalleVenta("Snacks para roedores", 3, new BigDecimal("1990"), new BigDecimal("1000"))
        ), LocalDateTime.of(2025, 9, 1, 13, 0)));
        

        listaVentas.add(new Venta(9, "Cliente Herrera", List.of(
            new DetalleVenta("Antipulgas NexGard", 1, new BigDecimal("16990"), new BigDecimal("11000"))
        ), LocalDateTime.of(2025, 9, 2, 18, 0)));

        listaVentas.add(new Venta(10, "Cliente Soto", List.of(
            new DetalleVenta("Alimento seco gatos 7.5kg", 1, new BigDecimal("32990"), new BigDecimal("22000")),
            new DetalleVenta("Paté para gatos", 5, new BigDecimal("1200"), new BigDecimal("700")),
            new DetalleVenta("Ratoncito de juguete", 2, new BigDecimal("990"), new BigDecimal("500"))
        ), LocalDateTime.of(2025, 9, 2, 20, 30)));
        
        this.ventaService = new VentaService(listaVentas);
    }
      
    // Método GET para listar todas las ventas
    @GetMapping
    public List<Venta> listarTodasLasVentas() {
        return ventaService.listarTodas();
    }
    
    // Método GET para buscar una venta por su ID
    @GetMapping("/{id}")
    public Optional<Venta> buscarVentaPorId(@PathVariable int id) {
        return ventaService.buscarPorId(id);
    }
    // Método GET para obtener las ganancias diarias
    @GetMapping("/ganancias/diarias")
    public BigDecimal getGananciasDiarias(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ventaService.getGananciasDiarias(fecha);
    }
    // Método GET para obtener las ganancias mensuales
    @GetMapping("/ganancias/mensuales")
    public BigDecimal getGananciasMensuales(@RequestParam int mes, @RequestParam int anio) {
        return ventaService.getGananciasMensuales(mes, anio);
    }
    // Método GET para obtener las ganancias anuales
    @GetMapping("/ganancias/anuales")
    public BigDecimal getGananciasAnuales(@RequestParam int anio) {
        return ventaService.getGananciasAnuales(anio);
    }
}
