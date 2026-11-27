package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.model.Usuario
import com.example.evalapp_grupo12.viewmodel.SesionViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SesionViewModelTest {

    private val usuario = Usuario(1, "Instructor Demo", "demo@correo.cl", "123456", "Docente")

    @Test
    fun alInicioNoHayUsuario() {
        assertNull(SesionViewModel().usuario.value)
    }

    @Test
    fun iniciarSesionGuardaElUsuario() {
        val viewModel = SesionViewModel()

        viewModel.iniciarSesion(usuario)

        assertEquals("Instructor Demo", viewModel.usuario.value?.nombre)
    }

    @Test
    fun actualizarUsuarioCambiaLosDatos() {
        val viewModel = SesionViewModel()
        viewModel.iniciarSesion(usuario)

        viewModel.actualizarUsuario(usuario.copy(nombre = "Nombre nuevo"))

        assertEquals("Nombre nuevo", viewModel.usuario.value?.nombre)
    }

    @Test
    fun cerrarSesionBorraElUsuario() {
        val viewModel = SesionViewModel()
        viewModel.iniciarSesion(usuario)

        viewModel.cerrarSesion()

        assertNull(viewModel.usuario.value)
    }
}
