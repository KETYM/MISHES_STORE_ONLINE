package com.example.ms_carroCompras.services;

import com.example.ms_carroCompras.client.ProductoClient;
import com.example.ms_carroCompras.dto.CarritoDTO;
import com.example.ms_carroCompras.dto.LibroResponseDTO;
import com.example.ms_carroCompras.model.Carrito;
import com.example.ms_carroCompras.repository.carritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private carritoRepository repository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private CarritoService carritoService;

    private Carrito carritoFalso;
    private CarritoDTO carritoDTO;
    private LibroResponseDTO libroFalso;

    @BeforeEach
    void setUp() {
        // Datos para Carrito
        carritoFalso = new Carrito();
        carritoFalso.setIdCarrito(1L);
        carritoFalso.setIdCliente(10L);
        carritoFalso.setIdProducto(5L);
        carritoFalso.setCantidad(2);

        carritoDTO = new CarritoDTO();
        carritoDTO.setIdCliente(10L);
        carritoDTO.setIdProducto(5L);
        carritoDTO.setCantidad(2);

        // Datos para Libro (Feign)
        libroFalso = new LibroResponseDTO();
        libroFalso.setIdLibro(5L);
        libroFalso.setTitulo("Manga Test");
        libroFalso.setPrecio(5000.0);
    }

    @Test
    void agregarAlCarrito_Exito() {
        when(productoClient.buscarPorId(5L)).thenReturn(libroFalso);
        when(repository.save(any(Carrito.class))).thenReturn(carritoFalso);

        Carrito resultado = carritoService.agregarAlCarrito(carritoDTO);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getIdProducto());
        verify(repository, times(1)).save(any(Carrito.class));
    }

    @Test
    void verCarritoEnriquecidoPorCliente_Exito() {
        when(repository.findByIdCliente(10L)).thenReturn(Arrays.asList(carritoFalso));
        when(productoClient.buscarPorId(5L)).thenReturn(libroFalso);

        List<Map<String, Object>> resultado = carritoService.verCarritoEnriquecidoPorCliente(10L);

        assertFalse(resultado.isEmpty());
        assertEquals("Manga Test", resultado.get(0).get("nombreProducto"));
        assertEquals(5000.0, resultado.get(0).get("precioUnitario"));
    }

    @Test
    void actualizarCantidad_Exito() {
        when(repository.findById(1L)).thenReturn(Optional.of(carritoFalso));
        when(repository.save(any(Carrito.class))).thenReturn(carritoFalso);

        Carrito resultado = carritoService.actualizarCantidad(1L, 5);

        assertEquals(5, resultado.getCantidad());
        verify(repository, times(1)).save(any(Carrito.class));
    }

    @Test
    void eliminarDelCarrito_Exito() {
        when(repository.existsById(1L)).thenReturn(true);

        carritoService.eliminarDelCarrito(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}