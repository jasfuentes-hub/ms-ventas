package com.ventas.ms_ventas;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    // Inyectamos el VentaService que ahora gestiona la lógica con el repositorio
    private final VentaService ventaService;

    // Usamos inyección de dependencias para obtener el VentaService
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // Método GET para listar todas las ventas (CON HATEOAS)
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> listarTodasLasVentas() {
        List<Venta> ventas = ventaService.listarTodas();

        // 1. Convertir cada Venta a EntityModel. Solo incluimos el enlace 'self' para
        // que el cliente sepa cómo consultar la venta individual.
        List<EntityModel<Venta>> ventaModels = ventas.stream()
                .map(venta -> EntityModel.of(venta,
                        // Enlace 'self' del recurso individual para navegar a /ventas/{id}
                        // NOTA: Para evitar el error de CGLIB/Optional en los tests,
                        // llamamos al método buscarVentaPorId que ahora retorna ResponseEntity.
                        linkTo(methodOn(VentaController.class).buscarVentaPorId(venta.getId())).withSelfRel()
                ))
                .collect(Collectors.toList());

        // 2. Envolver la lista en CollectionModel y añadir enlaces a la colección
        CollectionModel<EntityModel<Venta>> collectionModel = CollectionModel.of(ventaModels,
                // Enlace 'self' a esta lista
                linkTo(methodOn(VentaController.class).listarTodasLasVentas()).withSelfRel(),
                // Enlace para crear una nueva venta
                linkTo(methodOn(VentaController.class).guardarVenta(null)).withRel("crear-venta")
        );

        return ResponseEntity.ok(collectionModel);
    }

    // Método GET para buscar una venta por su ID (REFRACTORIZADO para evitar problemas de CGLIB/Optional con HATEOAS)
    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscarVentaPorId(@PathVariable int id) {
        Optional<Venta> venta = ventaService.buscarPorId(id);
        // Si existe, devuelve 200 OK con el cuerpo, si no, devuelve 404 Not Found.
        return venta.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    // Método POST para guardar una nueva venta (SIMPLE, SIN HATEOAS)
    @PostMapping
    public Venta guardarVenta(@RequestBody Venta venta) {
        return ventaService.guardarVenta(venta);
    }

    // Método PUT para actualizar una venta existente (SIMPLE, SIN HATEOAS)
    @PutMapping("/{id}")
    public Venta actualizarVenta(@PathVariable int id, @RequestBody Venta venta) {
        return ventaService.actualizarVenta(id, venta);
    }

    // Método DELETE para eliminar una venta por su ID (SIMPLE, SIN HATEOAS)
    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable int id) {
        ventaService.eliminarVenta(id);
    }

    // --- MÉTODOS DE CÁLCULO ---
    // Estos métodos retornan un valor primitivo y se mantienen simples.

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
