package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.model.Usuario
import com.example.evalapp_grupo12.viewmodel.EditarUsuarioViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EditarUsuarioViewModelTest {

    private val dao = FakeUsuarioDao()
    private val viewModel = EditarUsuarioViewModel(UsuarioRepository(dao))

    private val docente = Usuario(1, "Docente Demo", "docente@demo.cl", "demo123", "Docente")

    @Test
    fun cargarDatosLlenaElFormulario() {
        viewModel.cargarDatos(docente)

        assertEquals("Docente Demo", viewModel.estado.value.nombre)
        assertEquals("demo123", viewModel.estado.value.clave)
        assertEquals("Docente", viewModel.estado.value.rol)
    }

    @Test
    fun guardarConNombreCortoMuestraError() {
        viewModel.cargarDatos(docente)
        viewModel.onNombreChange("Do")

        assertNull(viewModel.guardarCambios(docente))
        assertEquals("Debe tener al menos 3 letras", viewModel.estado.value.errores.nombre)
    }

    @Test
    fun guardarConClaveCortaMuestraError() {
        viewModel.cargarDatos(docente)
        viewModel.onClaveChange("123")

        assertNull(viewModel.guardarCambios(docente))
        assertEquals("Debe tener al menos 6 caracteres", viewModel.estado.value.errores.clave)
    }

    @Test
    fun guardarCambiosActualizaElUsuario() {
        dao.usuarios.add(docente)
        viewModel.cargarDatos(docente)
        viewModel.onNombreChange("Docente Nuevo")
        viewModel.onClaveChange("nueva123")
        viewModel.onRolChange("Instructor")

        val editado = viewModel.guardarCambios(docente)

        assertEquals("Docente Nuevo", editado?.nombre)
        assertEquals("Instructor", dao.usuarios[0].rol)
        assertEquals("nueva123", dao.usuarios[0].clave)
        assertEquals("docente@demo.cl", dao.usuarios[0].correo)
    }

    @Test
    fun escribirDeNuevoBorraElError() {
        viewModel.validarFormulario()

        viewModel.onNombreChange("Docente Demo")
        viewModel.onClaveChange("demo123")

        assertNull(viewModel.estado.value.errores.nombre)
        assertNull(viewModel.estado.value.errores.clave)
    }
}
