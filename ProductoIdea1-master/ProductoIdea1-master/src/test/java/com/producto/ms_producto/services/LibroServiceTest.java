package com.producto.ms_producto.services;

import com.producto.ms_producto.dto.LibroDTO;
import com.producto.ms_producto.exception.RecursoDuplicadoException;
import com.producto.ms_producto.model.Libro;
import com.producto.ms_producto.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    private Libro libroFalso;
    private LibroDTO libroDTO;

    @BeforeEach
    void setUp() {
        // Inicializamos los datos de prueba
        libroFalso = new Libro();
        libroFalso.setIdLibro(1L);
        libroFalso.setIsbn("978-0-123456-47-2");
        libroFalso.setTitulo("Backend con Spring Boot");
        libroFalso.setAutor("Janit");
        libroFalso.setPrecio(15000.0);
        libroFalso.setStock(50);

        libroDTO = new LibroDTO();
        libroDTO.setIsbn("978-0-123456-47-2");
        libroDTO.setTitulo("Backend con Spring Boot");
        libroDTO.setAutor("Janit");
        libroDTO.setPrecio(15000.0);
        libroDTO.setStock(50);
    }

    // ==========================================
    // TESTS PARA GUARDAR LIBRO
    // ==========================================
    @Test
    void guardar_Exito() {
        when(libroRepository.findByIsbn(libroDTO.getIsbn())).thenReturn(Optional.empty());
        when(libroRepository.save(any(Libro.class))).thenReturn(libroFalso);

        Libro resultado = libroService.guardar(libroDTO);

        assertNotNull(resultado);
        assertEquals("Backend con Spring Boot", resultado.getTitulo());
        assertEquals("Janit", resultado.getAutor());
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    void guardar_FallaPorIsbnDuplicado() {
        when(libroRepository.findByIsbn(libroDTO.getIsbn())).thenReturn(Optional.of(libroFalso));

        RecursoDuplicadoException excepcion = assertThrows(RecursoDuplicadoException.class, () -> {
            libroService.guardar(libroDTO);
        });

        assertEquals("El libro con ISBN 978-0-123456-47-2 ya existe.", excepcion.getMessage());
        verify(libroRepository, never()).save(any(Libro.class));
    }

    // ==========================================
    // TESTS PARA BUSCAR POR ISBN
    // ==========================================
    @Test
    void buscarPorIsbn_Exito() {
        when(libroRepository.findByIsbn("978-0-123456-47-2")).thenReturn(Optional.of(libroFalso));

        Libro resultado = libroService.buscarPorIsbn("978-0-123456-47-2");

        assertNotNull(resultado);
        assertEquals("Janit", resultado.getAutor());
    }

    @Test
    void buscarPorIsbn_FallaNoEncontrado() {
        when(libroRepository.findByIsbn("000-0-000000-00-0")).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            libroService.buscarPorIsbn("000-0-000000-00-0");
        });

        assertEquals("No encontramos el libro con ISBN: 000-0-000000-00-0", excepcion.getMessage());
    }

    // ==========================================
    // TESTS PARA LISTAR TODOS
    // ==========================================
    @Test
    void listarTodos_Exito() {
        when(libroRepository.findAll()).thenReturn(Arrays.asList(libroFalso));

        List<Libro> resultado = libroService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(libroRepository, times(1)).findAll();
    }

    // ==========================================
    // TESTS PARA ACTUALIZAR
    // ==========================================
    @Test
    void actualizarPorId_Exito() {
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libroFalso));
        when(libroRepository.save(any(Libro.class))).thenReturn(libroFalso);

        Optional<Libro> resultado = libroService.actualizarPorId(1L, libroFalso);

        assertTrue(resultado.isPresent());
        assertEquals("Backend con Spring Boot", resultado.get().getTitulo());
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    // ==========================================
    // TESTS PARA ELIMINAR
    // ==========================================
    @Test
    void eliminarPorId_Exito() {
        when(libroRepository.existsById(1L)).thenReturn(true);

        libroService.eliminarPorId(1L);

        verify(libroRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarPorId_FallaNoEncontrado() {
        when(libroRepository.existsById(1L)).thenReturn(false);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            libroService.eliminarPorId(1L);
        });

        assertEquals("No se puede eliminar. El libro con ID 1 no existe.", excepcion.getMessage());
        verify(libroRepository, never()).deleteById(anyLong());
    }
}