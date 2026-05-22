package com.example.ms_carroCompras.services;

import com.example.ms_carroCompras.dto.CarritoDTO;
import com.example.ms_carroCompras.dto.LibroResponseDTO;
import com.example.ms_carroCompras.client.ProductoClient;
import com.example.ms_carroCompras.model.Carrito;
import com.example.ms_carroCompras.repository.carritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public List<Carrito> verCarritoPorCliente(Long idCliente) {
        return repository.findByIdCliente(idCliente);
    }
}