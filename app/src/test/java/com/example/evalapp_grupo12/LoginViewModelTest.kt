package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.model.Usuario
import com.example.evalapp_grupo12.viewmodel.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {

    private val dao = FakeUsuarioDao()
    private val viewModel = LoginViewModel(UsuarioRepository(dao))

    private val docente = Usuario(1, "Docente Demo", "docente@demo.cl", "demo123", "Docente")

    @Test
    fun correoSinArrobaMuestraError() {
        viewModel.onCorreoChange("docente")
        viewModel.onClaveChange("demo123")

        assertFalse(viewModel.validarFormulario())
        assertEquals("Correo inválido", viewModel.estado.value.errores.correo)
    }

    @Test
    fun claveVaciaMuestraError() {
        viewModel.onCorreoChange("docente@demo.cl")

        assertFalse(viewModel.validarFormulario())
        assertEquals("La contraseña es obligatoria", viewModel.estado.value.errores.clave)
    }

    @Test
    fun escribirDeNuevoBorraElError() {
        viewModel.validarFormulario()

        viewModel.onCorreoChange("docente@demo.cl")
        viewModel.onClaveChange("demo123")

        assertNull(viewModel.estado.value.errores.correo)
        assertNull(viewModel.estado.value.errores.clave)
    }

    @Test
    fun iniciarSesionConDatosCorrectos() {
        dao.usuarios.add(docente)
        viewModel.onCorreoChange("docente@demo.cl")
        viewModel.onClaveChange("demo123")

        val usuario = viewModel.iniciarSesion()

        assertEquals("Docente Demo", usuario?.nombre)
        assertEquals("", viewModel.estado.value.correo)
    }

    @Test
    fun iniciarSesionConClaveIncorrecta() {
        dao.usuarios.add(docente)
        viewModel.onCorreoChange("docente@demo.cl")
        viewModel.onClaveChange("otra")

        assertNull(viewModel.iniciarSesion())
        assertEquals("Correo o contraseña incorrectos", viewModel.estado.value.errores.clave)
    }

    @Test
    fun iniciarSesionConFormularioInvalidoNoBuscaElUsuario() {
        dao.usuarios.add(docente)

        assertNull(viewModel.iniciarSesion())
        assertTrue(viewModel.estado.value.errores.correo != null)
    }
}
