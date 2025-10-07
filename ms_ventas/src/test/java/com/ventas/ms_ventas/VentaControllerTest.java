package com.ventas.ms_ventas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para la capa del controlador (VentaController).
 * Se utiliza @WebMvcTest para aislar la capa web y @MockBean para simular el VentaService.
 */
@WebMvcTest(VentaController.class)
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula las peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Convierte objetos Java a JSON y viceversa

    @MockBean
    private VentaService ventaService; // Simulamos el servicio para no depender de la lógica real ni de la BD

    // ---  CREAR DATOS MOCK ---

    /** Crea un objeto DetalleVenta con todos los campos obligatorios. */
    private DetalleVenta createDetalle(int id, String producto, int cantidad, String precio, String costo) {
        DetalleVenta detalle = new DetalleVenta(producto, cantidad, new BigDecimal(precio), new BigDecimal(costo));
        detalle.setId(id);
        return detalle;
    }

    /**
     * Crea un objeto Venta completamente inicializado para el mocking.
     * El setter de detalles calculará automáticamente el total y establecerá la fecha.
     */
    private Venta createMockVenta(Integer id, String cliente, List<DetalleVenta> detalles) {
        Venta venta = new Venta();
        if (id != null) venta.setId(id);
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.of(2025, 10, 5, 10, 0, 0)); // Fecha fija para pruebas
        venta.setDetalles(detalles); // Esto también calcula el total.
        return venta;
    }

    // --- PRUEBAS CRUD BÁSICAS ---

    /** Prueba 1: GET /ventas - Listar todas las ventas exitosamente. */
    @Test
    void testListarTodasLasVentasExitoso() throws Exception {
        // ARRANGE
        DetalleVenta d1 = createDetalle(1, "Prod A", 10, "10.00", "5.00");
        Venta v1 = createMockVenta(1, "Client A", List.of(d1));
        List<Venta> ventas = List.of(v1);

        // MOCKING: Cuando se llama a listarTodas(), devuelve la lista simulada.
        when(ventaService.listarTodas()).thenReturn(ventas);

        // ACT & ASSERT
        mockMvc.perform(get("/ventas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK
                // Aserción CORREGIDA 1: El contenido debe ser HATEOAS (hal+json)
                .andExpect(content().contentType("application/hal+json"))
                // Aserción CORREGIDA 2: La lista de ventas está bajo la ruta '._embedded.ventaList'
                .andExpect(jsonPath("$._embedded.ventaList.length()").value(1))
                // Aserción CORREGIDA 3: Acceder al cliente a través de la ruta HATEOAS
                .andExpect(jsonPath("$._embedded.ventaList[0].cliente").value("Client A"));
        
        verify(ventaService, times(1)).listarTodas();
    }

    /** Prueba 2: GET /ventas/{id} - Buscar una venta por ID existente. */
    @Test
    void testBuscarVentaPorIdExistente() throws Exception {
        // ARRANGE
        int id = 5;
        Venta v1 = createMockVenta(id, "Client B", new ArrayList<>());

        // MOCKING: Cuando se busca el ID, devuelve la venta.
        when(ventaService.buscarPorId(id)).thenReturn(Optional.of(v1));

        // ACT & ASSERT
        mockMvc.perform(get("/ventas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.cliente").value("Client B"));

        verify(ventaService, times(1)).buscarPorId(id);
    }

    /** Prueba 3: POST /ventas - Guardar una nueva venta exitosamente. */
    @Test
    void testGuardarVentaExitoso() throws Exception {
        // ARRANGE: Venta que se envía (sin ID)
        DetalleVenta d_new = createDetalle(0, "New Prod", 5, "5.00", "2.00");
        Venta newVenta = createMockVenta(null, "New Client", List.of(d_new)); 
        
        // ARRANGE: Venta que se retorna (con ID asignado)
        Venta savedVenta = createMockVenta(10, "New Client", List.of(d_new)); 

        // MOCKING: Cuando se llama a guardarVenta, devuelve la venta con ID 10.
        when(ventaService.guardarVenta(any(Venta.class))).thenReturn(savedVenta);

        // ACT & ASSERT
        mockMvc.perform(post("/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newVenta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.total").value(25.0)); // Total calculado: 5 * 5.00 = 25.0

        verify(ventaService, times(1)).guardarVenta(any(Venta.class));
    }
    
    /** Prueba 4: PUT /ventas/{id} - Actualizar una venta existente. */
    @Test
    void testActualizarVentaExitoso() throws Exception {
        // ARRANGE: Datos que se envían para la actualización
        Venta updatedData = createMockVenta(null, "Client Updated", new ArrayList<>()); 
        
        // ARRANGE: Venta que se retorna después de la actualización
        Venta returnedVenta = createMockVenta(5, "Client Updated", new ArrayList<>());

        // MOCKING: Cuando se llama a actualizarVenta, devuelve el objeto actualizado.
        when(ventaService.actualizarVenta(eq(5), any(Venta.class))).thenReturn(returnedVenta);

        // ACT & ASSERT
        mockMvc.perform(put("/ventas/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente").value("Client Updated"));
        
        verify(ventaService, times(1)).actualizarVenta(eq(5), any(Venta.class));
    }

    /** Prueba 5: DELETE /ventas/{id} - Eliminar una venta. */
    @Test
    void testEliminarVentaExitoso() throws Exception {
        // ARRANGE: Configuramos el mock para no hacer nada (void method)
        doNothing().when(ventaService).eliminarVenta(3);
        
        // ACT & ASSERT
        mockMvc.perform(delete("/ventas/3"))
                .andExpect(status().isOk()); // El controller retorna void (por defecto 200, podría ser 204)
        
        // VERIFICACIÓN: Aseguramos que el método del servicio fue llamado.
        verify(ventaService, times(1)).eliminarVenta(3);
    }

    // --- PRUEBAS DE CÁLCULO DE GANANCIAS ---

    /** Prueba 6: GET /ventas/ganancias/diarias - Obtener ganancias diarias. */
    @Test
    void testGetGananciasDiarias() throws Exception {
        // ARRANGE
        BigDecimal expectedProfit = new BigDecimal("150.75");
        LocalDate testDate = LocalDate.of(2025, 10, 5);

        // MOCKING: Cuando se llama al servicio con la fecha, devuelve la ganancia.
        when(ventaService.getGananciasDiarias(testDate)).thenReturn(expectedProfit);
        
        // ACT & ASSERT
        mockMvc.perform(get("/ventas/ganancias/diarias")
                .param("fecha", "2025-10-05")) // Formato yyyy-MM-dd
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("150.75"));
        
        verify(ventaService, times(1)).getGananciasDiarias(testDate);
    }

    /** Prueba 7: GET /ventas/ganancias/mensuales - Obtener ganancias mensuales. */
    @Test
    void testGetGananciasMensuales() throws Exception {
        // ARRANGE
        BigDecimal expectedProfit = new BigDecimal("5000.50");
        int mes = 10;
        int anio = 2025;

        // MOCKING: Cuando se llama al servicio con mes y año, devuelve la ganancia.
        when(ventaService.getGananciasMensuales(mes, anio)).thenReturn(expectedProfit);
        
        // ACT & ASSERT
        mockMvc.perform(get("/ventas/ganancias/mensuales")
                .param("mes", "10")
                .param("anio", "2025"))
                .andExpect(status().isOk())
                .andExpect(content().string("5000.50"));
        
        verify(ventaService, times(1)).getGananciasMensuales(mes, anio);
    }

    /** Prueba 8: GET /ventas/ganancias/anuales - Obtener ganancias anuales. */
    @Test
    void testGetGananciasAnuales() throws Exception {
        // ARRANGE
        BigDecimal expectedProfit = new BigDecimal("80000.00");
        int anio = 2025;

        // MOCKING: Cuando se llama al servicio con el año, devuelve la ganancia.
        when(ventaService.getGananciasAnuales(anio)).thenReturn(expectedProfit);
        
        // ACT & ASSERT
        mockMvc.perform(get("/ventas/ganancias/anuales")
                .param("anio", "2025"))
                .andExpect(status().isOk())
                .andExpect(content().string("80000.00"));
        
        verify(ventaService, times(1)).getGananciasAnuales(anio);
    }
}