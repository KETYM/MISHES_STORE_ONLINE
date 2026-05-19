package com.example.ms_carroCompras.services;

import com.example.ms_carroCompras.dto.CarritoDTO;
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
            Object producto = productoClient.buscarPorId(dto.getIdProducto());

            if (producto == null){
                throw new RuntimeException("El producto no existe en el catálogo.");
            }

            Carrito carrito = new Carrito();
            carrito.setIdCliente(dto.getIdCliente());
            carrito.setIdProducto(dto.getIdProducto());
            carrito.setCantidad(dto.getCantidad());

            return repository.save(carrito);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con el microservicio de productos usando Feign.");
        }
    }

    public List<Carrito> verCarritoPorCliente(Long idCliente) {
        return repository.findByIdCliente(idCliente);
    }

    // 💡 ¡EL NUEVO MÉTODO!: Busca absolutamente todas las filas de la tabla
    public List<Carrito> listarTodoElCarro() {
        return repository.findAll();
    }
}