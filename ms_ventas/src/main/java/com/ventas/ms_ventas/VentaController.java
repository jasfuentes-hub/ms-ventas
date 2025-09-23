package com.ventas.ms_ventas;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    // Inyectamos el VentaService que ahora gestiona la lógica con el repositorio
    private final VentaService ventaService;

    // Usamos inyección de dependencias para obtener el VentaService
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // Método GET para listar todas las ventas
    // Ahora llama al servicio, que a su vez usa el repositorio para buscar en la BDD
    @GetMapping
    public List<Venta> listarTodasLasVentas() {
        return ventaService.listarTodas();
    }

    // Método GET para buscar una venta por su ID
    @GetMapping("/{id}")
    public Optional<Venta> buscarVentaPorId(@PathVariable int id) {
        return ventaService.buscarPorId(id);
    }
    
    // Método POST para guardar una nueva venta
    // JPA se encargará de guardar la Venta y sus DetalleVenta asociados
    @PostMapping
    public Venta guardarVenta(@RequestBody Venta venta) {
        return ventaService.guardarVenta(venta);
    }

        // --- NUEVOS MÉTODOS ---

    // Método PUT para actualizar una venta existente
    @PutMapping("/{id}")
    public Venta actualizarVenta(@PathVariable int id, @RequestBody Venta venta) {
        return ventaService.actualizarVenta(id, venta);
    }

    // Método DELETE para eliminar una venta por su ID
    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable int id) {
        ventaService.eliminarVenta(id);
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