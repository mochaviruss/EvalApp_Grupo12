package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.logic.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun nombreCorto() {
        assertEquals("Debe tener al menos 3 letras", Validaciones.validarNombre("Al"))
        assertNull(Validaciones.validarNombre("Ana"))
    }

    @Test
    fun correoSinArroba() {
        assertEquals("Correo inválido", Validaciones.validarCorreo("correo.cl"))
        assertNull(Validaciones.validarCorreo("demo@correo.cl"))
    }

    @Test
    fun claveCorta() {
        assertEquals("Debe tener al menos 6 caracteres", Validaciones.validarClave("123"))
        assertNull(Validaciones.validarClave("123456"))
    }

    @Test
    fun claveDeLoginVacia() {
        assertEquals("La contraseña es obligatoria", Validaciones.validarClaveLogin("  "))
        assertNull(Validaciones.validarClaveLogin("abc"))
    }

    @Test
    fun confirmacionDistinta() {
        assertEquals("Las contraseñas no coinciden", Validaciones.validarConfirmacion("123456", "654321"))
        assertNull(Validaciones.validarConfirmacion("123456", "123456"))
    }

    @Test
    fun rolSinElegir() {
        assertEquals("Selecciona un rol", Validaciones.validarRol(""))
        assertNull(Validaciones.validarRol("Docente"))
    }

    @Test
    fun tituloCorto() {
        assertEquals("El título debe tener al menos 3 caracteres", Validaciones.validarTitulo("ab"))
        assertNull(Validaciones.validarTitulo("Guía"))
    }

    @Test
    fun archivoSinElegir() {
        assertEquals("Selecciona un archivo PDF o Word", Validaciones.validarArchivo(""))
        assertNull(Validaciones.validarArchivo("content://documento.pdf"))
    }
}
