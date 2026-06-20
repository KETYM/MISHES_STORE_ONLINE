package com.example.ms_carroCompras.services;

import com.example.ms_carroCompras.dto.CarritoDTO;
import com.example.ms_carroCompras.dto.LibroResponseDTO;
import com.example.ms_carroCompras.client.ProductoClient;
import com.example.ms_carroCompras.model.Carrito;
import com.example.ms_carroCompras.repository.carritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarritoService {

    @Autowired
    private carritoRepository repository;

    @Autowired
    private ProductoClient productoClient;

    public Carrito agregarAlCarrito(CarritoDTO dto){
        try {
            // 🔎 LLAMADA OFICIAL POR FEIGN: Busca el manga en ms-producto
            LibroResponseDTO libro = productoClient.buscarPorId(dto.getIdProducto());

            // Validación estricta
            if (libro == null || libro.getIdLibro() == null){
                throw new RuntimeException("El producto no existe en el catálogo comercial.");
            }

            // Si pasa el filtro de Feign, se guarda de verdad en la BD
            Carrito carrito = new Carrito();
            carrito.setIdCliente(dto.getIdCliente());
            carrito.setIdProducto(dto.getIdProducto());
            carrito.setCantidad(dto.getCantidad());

            return repository.save(carrito);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en el circuito de compras: " + e.getMessage());
        }
    }

    public List<Carrito> listarTodoElCarro() {
        return repository.findAll();
    }

    // 🌟 MÉTODOS EXTRA: OBTENER EL CARRITO TOTALMENTE ENRIQUECIDO CON NOMBRE E ID
    public List<Map<String, Object>> verCarritoEnriquecidoPorCliente(Long idCliente) {
        List<Carrito> itemsBD = repository.findByIdCliente(idCliente);
        List<Map<String, Object>> carritoCompleto = new ArrayList<>();

        for (Carrito item : itemsBD) {
            Map<String, Object> itemMapa = new HashMap<>();
            itemMapa.put("idCarrito", item.getIdCarrito());
            itemMapa.put("idCliente", item.getIdCliente());
            itemMapa.put("idProducto", item.getIdProducto());
            itemMapa.put("cantidad", item.getCantidad());

            try {
                // Interceptamos con Feign el nombre en tiempo real
                LibroResponseDTO libro = productoClient.buscarPorId(item.getIdProducto());
                if (libro != null) {
                    // Mapeamos el nombre y precio que traiga tu LibroResponseDTO
                    itemMapa.put("nombreProducto", libro.getTitulo());
                    itemMapa.put("precioUnitario", libro.getPrecio());
                }
            } catch (Exception e) {
                // Tolerancia a fallos si ms-producto se apaga
                itemMapa.put("nombreProducto", "Manga ID " + item.getIdProducto() + " (Info no disponible)");
                itemMapa.put("precioUnitario", 0);
            }
            carritoCompleto.add(itemMapa);
        }
        return carritoCompleto;
    }

    // 🌟 MÉTODOS EXTRA: EL PUT (Modificar la cantidad de un ítem existente)
    public Carrito actualizarCantidad(Long idCarrito, Integer nuevaCantidad) {
        Carrito item = repository.findById(idCarrito)
                .orElseThrow(() -> new RuntimeException("El ítem con ID " + idCarrito + " no existe en el carro."));

        item.setCantidad(nuevaCantidad); // Usamos el set autogenerado por Lombok para mutar el stock en memoria
        return repository.save(item);     // Persistimos los cambios en la BD
    }

    // 🌟 MÉTODOS EXTRA: EL DELETE (Eliminar un ítem por completo)
    public void eliminarDelCarrito(Long idCarrito) {
        if (!repository.existsById(idCarrito)) {
            throw new RuntimeException("No se puede eliminar. El ítem ID " + idCarrito + " no existe.");
        }
        repository.deleteById(idCarrito);
    }
}