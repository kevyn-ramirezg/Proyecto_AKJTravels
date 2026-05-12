package co.edu.uniquindio.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainApplicationTests {

    @Test
    @DisplayName("La aplicacion debe exponer el metodo main sin fallos de carga de clase")
    void mainMethodExists() {
        assertDoesNotThrow(() -> MainApplication.class.getDeclaredMethod("main", String[].class));
    }
}
