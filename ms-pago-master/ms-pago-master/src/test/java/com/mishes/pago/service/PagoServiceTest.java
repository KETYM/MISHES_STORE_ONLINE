package com.mishes.pago.service;
import com.mishes.pago.client.CarroClient;
import com.mishes.pago.client.ClienteClient;
import com.mishes.pago.client.PedidoClient;
import com.mishes.pago.dto.ClienteResponseDTO;
import com.mishes.pago.dto.PagoRequestDTO;
import com.mishes.pago.dto.PagoResponseDTO;
import com.mishes.pago.model.MetodoPago;
import com.mishes.pago.model.Pago;
import com.mishes.pago.repository.MetodoPagoRepository;
import com.mishes.pago.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MetodoPagoRepository metodoPagoRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private CarroClient carroClient;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private PagoService pagoService;

    private Pago pagoFalso;
    private PagoRequestDTO requestDTO;
    private MetodoPago metodoDebito;
    private MetodoPago metodoEfectivo;

    @BeforeEach
    void setUp() {
        // 1. Preparamos Métodos de Pago
        metodoDebito = new MetodoPago();
        metodoDebito.setIdMetodoPago((short) 2);
        metodoDebito.setNombreMetodoPago("Debito");

        metodoEfectivo = new MetodoPago();
        metodoEfectivo.setIdMetodoPago((short) 1);
        metodoEfectivo.setNombreMetodoPago("Efectivo");

        // 2. Preparamos el Request
        requestDTO = new PagoRequestDTO();
        requestDTO.setIdCli(1L);
        requestDTO.setIdPed(100L);
        requestDTO.setFechaPago(LocalDateTime.now());
        requestDTO.setMontoTotal(17980);
        requestDTO.setIdMetodoPago((short) 2); // Usaremos Débito por defecto para que pase

        // 3. Preparamos la Entidad Pago
        pagoFalso = new Pago();
        pagoFalso.setIdPag(50L);
        pagoFalso.setIdCli(1L);
        pagoFalso.setIdPed(100L);
        pagoFalso.setFechaPago(LocalDateTime.now());
        pagoFalso.setMontoTotal(17980);
        pagoFalso.setIdMetodoPago((short) 2);
    }

    // ==========================================
    // TESTS PARA GUARDAR PAGO (REGLAS DE NEGOCIO)
    // ==========================================

    @Test
    void guardar_ExitoConCarritoLlenoYMetodoPermitido() {
        // --- GIVEN ---
        // 1. Simulamos que el cliente SÍ tiene cosas en el carrito (Feign)
        when(carroClient.verCarritoPorCliente(1L)).thenReturn(Arrays.asList(new Object()));
        // 2. Simulamos que el método de pago existe y es Débito
        when(metodoPagoRepository.findById((short) 2)).thenReturn(Optional.of(metodoDebito));
        // 3. Simulamos guardado
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoFalso);

        // Simulamos que el Feign de Cliente falla para probar tu mapeo de "Janit Profesional"
        when(clienteClient.obtenerPorId(1L)).thenThrow(new RuntimeException("Error 403 simulado"));

        // --- WHEN ---
        PagoResponseDTO resultado = pagoService.guardar(requestDTO);

        // --- THEN ---
        assertNotNull(resultado);
        assertEquals(17980, resultado.getMontoTotal());
        assertEquals("Janit Profesional", resultado.getNombreCliente()); // Tu regla de mapeo
        verify(carroClient, times(1)).verCarritoPorCliente(1L);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void guardar_FallaPorCarritoVacio() {
        // --- GIVEN ---
        // Simulamos respuesta vacía del Feign de Carrito
        when(carroClient.verCarritoPorCliente(1L)).thenReturn(Collections.emptyList());

        // --- WHEN & THEN ---
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pagoService.guardar(requestDTO);
        });

        assertEquals("No se puede procesar el pago: El carrito del cliente está vacío.", excepcion.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void guardar_FallaPorMetodoEfectivo() {
        // --- GIVEN ---
        requestDTO.setIdMetodoPago((short) 1); // Cambiamos a Efectivo
        when(carroClient.verCarritoPorCliente(1L)).thenReturn(Arrays.asList(new Object()));
        when(metodoPagoRepository.findById((short) 1)).thenReturn(Optional.of(metodoEfectivo));

        // --- WHEN & THEN ---
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pagoService.guardar(requestDTO);
        });

        assertEquals("Error: El pago en Efectivo no está permitido en Mishes Store online.", excepcion.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    // ==========================================
    // TESTS PARA BUSCAR PAGO
    // ==========================================

    @Test
    void obtenerPorId_Exito() {
        when(pagoRepository.findById(50L)).thenReturn(Optional.of(pagoFalso));
        // Simulamos cliente OK
        ClienteResponseDTO clienteFalso = new ClienteResponseDTO();
        clienteFalso.setNombreCli("Cliente Test");
        when(clienteClient.obtenerPorId(1L)).thenReturn(clienteFalso);

        when(metodoPagoRepository.findById((short) 2)).thenReturn(Optional.of(metodoDebito));

        Optional<PagoResponseDTO> resultado = pagoService.obtenerPorId(50L);

        assertTrue(resultado.isPresent());
        assertEquals("Cliente Test", resultado.get().getNombreCliente());
        assertEquals("Debito", resultado.get().getMetodoPago());
    }
}