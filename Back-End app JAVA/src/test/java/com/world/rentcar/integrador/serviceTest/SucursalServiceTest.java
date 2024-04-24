package com.world.rentcar.integrador.serviceTest;
import com.world.rentcar.integrador.exeptions.BadRequest;
import com.world.rentcar.integrador.model.Direccion;
import com.world.rentcar.integrador.model.Sucursal;
import com.world.rentcar.integrador.repository.SucursalRepository;
import com.world.rentcar.integrador.service.SucursalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sucursales_DebeRetornarListaDeSucursales() {
        // Configuración del comportamiento simulado del repositorio
        when(sucursalRepository.findAll()).thenReturn(Arrays.asList(
                new Sucursal(1L,"MisiconesCar",new HashSet<>(),new Direccion("Argentina", "123 Main St", 666, "Obera", "Misiones")),
                new Sucursal(2L,"MontevideoCar",new HashSet<>(),new Direccion("Uruguay", "123 Main St", 457, "La Paz", "Montevideo"))
        ));

        // Llamada al método que se está probando
        List<Sucursal> sucursales = sucursalService.sucursales();

        // Verificación de los resultados
        assertEquals(2, sucursales.size());
        assertEquals("MisiconesCar", sucursales.get(0).getNombre());
        assertEquals("MontevideoCar", sucursales.get(1).getNombre());

        // Verificación de que el método del repositorio fue llamado una vez
        verify(sucursalRepository, times(1)).findAll();
    }


    @Test
    void buscarxID_CuandoSucursalExiste_DebeRetornarSucursal() throws BadRequest {
        // Configuración del comportamiento simulado del repositorio
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(new Sucursal(1L, "MisiconesCar", new HashSet<>(), new Direccion("Argentina", "123 Main St", 666, "Obera", "Misiones"))));

        // Llamada al método que se está probando
        Sucursal sucursal = sucursalRepository.findById(1L).get();

        // Verificación de los resultados
        assertEquals("MisiconesCar", sucursal.getNombre());

        // Verificación de que el método del repositorio fue llamado una vez
        verify(sucursalRepository, times(1)).findById(1L);
    }

    @Test
    void buscarxID_CuandoSucursalNoExiste_DebeLanzarExcepcion() {
        // Configuración del comportamiento simulado del repositorio
        when(sucursalRepository.findById(1L)).thenReturn(Optional.empty());

        // Llamada al método que se está probando y verificación de excepción
        assertThrows(BadRequest.class, () -> sucursalService.buscarxID(1L));

        // Verificación de que el método del repositorio fue llamado una vez
        verify(sucursalRepository, times(1)).findById(1L);
    }
}
