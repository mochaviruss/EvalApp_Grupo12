package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UsuarioRepositoryTest {

    private val dao = FakeUsuarioDao()
    private val repository = UsuarioRepository(dao)

    private val docente = Usuario(1, "Docente Demo", "docente@demo.cl", "demo123", "Docente")

    @Test
    fun registrarGuardaElCorreoEnMinusculasYSinEspacios() {
        repository.registrar(docente.copy(nombre = "  Ana Torres ", correo = " Ana@Demo.CL "))

        assertEquals("ana@demo.cl", dao.usuarios[0].correo)
        assertEquals("Ana Torres", dao.usuarios[0].nombre)
    }

    @Test
    fun existeCorreoNoDistingueMayusculas() {
        dao.usuarios.add(docente)

        assertTrue(repository.existeCorreo("DOCENTE@demo.cl"))
        assertFalse(repository.existeCorreo("otro@demo.cl"))
    }

    @Test
    fun buscarUsuarioConDatosCorrectos() {
        dao.usuarios.add(docente)

        assertNotNull(repository.buscarUsuario(" Docente@Demo.cl ", "demo123"))
    }

    @Test
    fun buscarUsuarioConClaveIncorrectaDevuelveNull() {
        dao.usuarios.add(docente)

        assertNull(repository.buscarUsuario("docente@demo.cl", "otra"))
    }

    @Test
    fun actualizarCambiaLosDatos() {
        dao.usuarios.add(docente)

        repository.actualizar(docente.copy(nombre = " Docente Nuevo ", rol = "Instructor"))

        assertEquals("Docente Nuevo", dao.usuarios[0].nombre)
        assertEquals("Instructor", dao.usuarios[0].rol)
    }
}
