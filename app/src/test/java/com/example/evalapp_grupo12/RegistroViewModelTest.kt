package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.model.Usuario
import com.example.evalapp_grupo12.viewmodel.RegistroViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistroViewModelTest {

    private val dao = FakeUsuarioDao()
    private val viewModel = RegistroViewModel(UsuarioRepository(dao))

    private fun llenarFormulario() {
        viewModel.onNombreChange("Ana Torres")
        viewModel.onCorreoChange("ana@demo.cl")
        viewModel.onClaveChange("clave123")
        viewModel.onConfirmarClaveChange("clave123")
        viewModel.onRolChange("Docente")
    }

    @Test
    fun formularioVacioMuestraTodosLosErrores() {
        assertFalse(viewModel.validarFormulario())

        val errores = viewModel.estado.value.errores
        assertEquals("Debe tener al menos 3 letras", errores.nombre)
        assertEquals("Correo inválido", errores.correo)
        assertEquals("Debe tener al menos 6 caracteres", errores.clave)
        assertNull(errores.confirmarClave)
        assertEquals("Selecciona un rol", errores.rol)
    }

    @Test
    fun clavesDistintasMuestraError() {
        llenarFormulario()
        viewModel.onConfirmarClaveChange("otra123")

        assertFalse(viewModel.validarFormulario())
        assertEquals("Las contraseñas no coinciden", viewModel.estado.value.errores.confirmarClave)
    }

    @Test
    fun correoRepetidoMuestraError() {
        dao.usuarios.add(Usuario(1, "Ana", "ana@demo.cl", "clave123", "Docente"))
        llenarFormulario()

        assertFalse(viewModel.validarFormulario())
        assertEquals("Este correo ya está registrado", viewModel.estado.value.errores.correo)
    }

    @Test
    fun registrarConDatosCorrectos() {
        llenarFormulario()

        assertTrue(viewModel.registrar())
        assertEquals(1, dao.usuarios.size)
        assertEquals("Docente", dao.usuarios[0].rol)
        assertEquals("", viewModel.estado.value.nombre)
    }

    @Test
    fun registrarConErroresNoGuarda() {
        viewModel.onNombreChange("Ana Torres")

        assertFalse(viewModel.registrar())
        assertTrue(dao.usuarios.isEmpty())
    }

    @Test
    fun escribirDeNuevoBorraLosErrores() {
        viewModel.validarFormulario()

        llenarFormulario()

        val errores = viewModel.estado.value.errores
        assertNull(errores.nombre)
        assertNull(errores.correo)
        assertNull(errores.clave)
        assertNull(errores.rol)
    }
}
