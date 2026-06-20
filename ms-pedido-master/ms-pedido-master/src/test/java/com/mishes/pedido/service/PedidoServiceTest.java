package com.mishes.pedido.service;

import com.mishes.pedido.dto.PedidoRequestDTO;
import com.mishes.pedido.dto.PedidoResponseDTO;
import com.mishes.pedido.model.Estado;
import com.mishes.pedido.model.Pedido;
import com.mishes.pedido.model.PedidoDetalle;
import com.mishes.pedido.repository.EstadoRepository;
import com.mishes.pedido.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoFalso;
    private PedidoRequestDTO requestDTO;
    private Estado estadoFalso;

    @BeforeEach
    void setUp() {
        // 1. Preparamos un Estado Falso (Ajustado a Short)
        estadoFalso = new Estado();
        estadoFalso.setIdEstado((short) 1); // <-- CORREGIDO AQUÍ
        estadoFalso.setNombreEstado("PENDIENTE");

        // 2. Preparamos un DTO de Request Falso (Ajustado a Short)
        requestDTO = new PedidoRequestDTO();
        requestDTO.setIdCli(100L);
        requestDTO.setIdProc(50L);
        requestDTO.setIdEstado((short) 1); // <-- YA ESTABA CORREGIDO
        requestDTO.setCantidad(2);
        requestDTO.setPrecioUnitario(10000.0);
        requestDTO.setNombreProducto("Manga Naruto Vol 1");
        requestDTO.setFechaPedido(LocalDateTime.now());

        // 3. Preparamos el Pedido y su Detalle Falso (para simular la BD)
        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setIdPedDetalle(1L);
        detalle.setIdProc(50L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(10000.0);

        pedidoFalso = new Pedido();
        pedidoFalso.setIdPed(1L);
        pedidoFalso.setIdCli(100L);
        pedidoFalso.setIdEstado(estadoFalso);
        pedidoFalso.setFechaPedido(LocalDateTime.now());
        pedidoFalso.setTotalPagar(20000.0);
        pedidoFalso.setDetalles(Arrays.asList(detalle));
    }

    // ==========================================
    // TESTS PARA GUARDAR PEDIDO
    // ==========================================

    @Test
    void guardar_Exito() {
        // --- GIVEN ---
        when(estadoRepository.findById(requestDTO.getIdEstado())).thenReturn(Optional.of(estadoFalso));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoFalso);

        // --- WHEN ---
        PedidoResponseDTO resultado = pedidoService.guardar(requestDTO);

        // --- THEN ---
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPed());
        assertEquals(20000.0, resultado.getPrecioTotal());
        assertEquals("PENDIENTE", resultado.getNombreEstado());

        // Verificamos usando Short en lugar de 1L
        verify(estadoRepository, times(1)).findById((short) 1); // <-- CORREGIDO AQUÍ
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void guardar_FallaEstadoNoEncontrado() {
        // --- GIVEN ---
        when(estadoRepository.findById(requestDTO.getIdEstado())).thenReturn(Optional.empty());

        // --- WHEN & THEN ---
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pedidoService.guardar(requestDTO);
        });

        assertEquals("Estado no encontrado en la base de datos", excepcion.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    // ==========================================
    // TESTS PARA BUSCAR POR ID
    // ==========================================

    @Test
    void obtenerPorId_Exito() {
        // --- GIVEN ---
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoFalso));

        // --- WHEN ---
        Optional<PedidoResponseDTO> resultado = pedidoService.obtenerPorId(1L);

        // --- THEN ---
        assertTrue(resultado.isPresent());
        assertEquals(100L, resultado.get().getIdCli());
    }

    // ==========================================
    // TESTS PARA LISTAR TODOS
    // ==========================================

    @Test
    void obtenerTodas_Exito() {
        // --- GIVEN ---
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoFalso));

        // --- WHEN ---
        List<PedidoResponseDTO> resultado = pedidoService.obtenerTodas();

        // --- THEN ---
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    // ==========================================
    // TESTS PARA ACTUALIZAR
    // ==========================================

    @Test
    void actualizarPorId_Exito() {
        // --- GIVEN ---
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoFalso));
        // Usamos Short para buscar el estado
        when(estadoRepository.findById((short) 1)).thenReturn(Optional.of(estadoFalso)); // <-- CORREGIDO AQUÍ
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoFalso);

        // --- WHEN ---
        Optional<PedidoResponseDTO> resultado = pedidoService.actualizarPorId(1L, requestDTO);

        // --- THEN ---
        assertTrue(resultado.isPresent());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    // ==========================================
    // TESTS PARA ELIMINAR
    // ==========================================

    @Test
    void eliminarPorId_Exito() {
        // --- WHEN ---
        pedidoService.eliminarPorId(1L);

        // --- THEN ---
        verify(pedidoRepository, times(1)).deleteById(1L);
    }
}