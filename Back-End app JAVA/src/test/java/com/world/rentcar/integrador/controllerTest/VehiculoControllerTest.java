package com.world.rentcar.integrador.controllerTest;

import com.world.rentcar.integrador.controller.VehiculoController;
import com.world.rentcar.integrador.exeptions.BadRequest;
import com.world.rentcar.integrador.model.Direccion;
import com.world.rentcar.integrador.model.Sucursal;
import com.world.rentcar.integrador.model.Vehiculo;
import com.world.rentcar.integrador.service.SucursalService;
import com.world.rentcar.integrador.service.VehiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class VehiculoControllerTest {

    @Mock
    private VehiculoService vehiculoService;

    @Mock
    private SucursalService sucursalService;

    @InjectMocks
    private VehiculoController vehiculoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarVehiculo_DebeRegistrarVehiculoCorrectamente() throws BadRequest {
        Sucursal sucursal1 = new Sucursal(1L,"MisiconesCar",new HashSet<>(),new Direccion("Argentina", "123 Main St", 666, "Obera", "Misiones"));
        when(sucursalService.buscarxID(any())).thenReturn( sucursal1);
        when(vehiculoService.guardar(any())).thenReturn(new Vehiculo());

        ResponseEntity<?> response = vehiculoController.registrarVehiculo(new Vehiculo());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void registrarVehiculo_DebeManejarExcepcionBadRequest() throws BadRequest {
        when(sucursalService.buscarxID(any())).thenReturn(new Sucursal());
        when(vehiculoService.guardar(any())).thenThrow(new BadRequest("Mensaje de error"));

        ResponseEntity<?> response = vehiculoController.registrarVehiculo(new Vehiculo());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void buscarVehiculo_DebeRetornarVehiculoExistente() throws BadRequest {
        Long vehiculoId = 1L;
        Vehiculo vehiculo = new Vehiculo();
        when(vehiculoService.buscarxID(vehiculoId)).thenReturn(Optional.of(vehiculo));

        ResponseEntity<Optional<Vehiculo>> response = vehiculoController.buscarVehiculo(vehiculoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(vehiculo, response.getBody().get());
    }

    @Test
    void buscarVehiculo_DebeRetornarNotFoundParaVehiculoNoExistente() throws BadRequest {
        Long vehiculoId = 1L;
        when(vehiculoService.buscarxID(vehiculoId)).thenReturn(Optional.empty());

        ResponseEntity<Optional<Vehiculo>> response = vehiculoController.buscarVehiculo(vehiculoId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void agregarCaracteristica_DebeAgregarCaracteristicaCorrectamente() throws BadRequest {
        Long vehiculoId = 1L;
        Vehiculo vehiculoExistente = new Vehiculo();
        when(vehiculoService.buscarxID(vehiculoId)).thenReturn(Optional.of(vehiculoExistente));

        ResponseEntity<Vehiculo> response = vehiculoController.agregarCaracteristica(new Vehiculo());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, vehiculoExistente.getCaracteristicasExtras().size());
    }

    @Test
    void agregarCaracteristica_DebeRetornarNotFoundParaVehiculoNoExistente() throws BadRequest {
        Long vehiculoId = 1L;
        when(vehiculoService.buscarxID(vehiculoId)).thenReturn(Optional.empty());

        ResponseEntity<Vehiculo> response = vehiculoController.agregarCaracteristica(new Vehiculo());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminarVehiculo_DebeEliminarVehiculoExistente() throws BadRequest {
        Long vehiculoId = 1L;
        when(vehiculoService.buscarxID(vehiculoId)).thenReturn(Optional.of(new Vehiculo()));

        ResponseEntity<?> response = vehiculoController.eliminarVehiculo(vehiculoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarVehiculo_DebeRetornarNotFoundParaVehiculoNoExistente() throws BadRequest {
        Long vehiculoId = 1L;
        when(vehiculoService.buscarxID(vehiculoId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = vehiculoController.eliminarVehiculo(vehiculoId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

