package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.AlumnoRepository
import com.example.evalapp_grupo12.data.ClimaRepository
import com.example.evalapp_grupo12.data.EvaluacionRepository
import com.example.evalapp_grupo12.data.RubricaRepository
import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.viewmodel.AlumnoViewModel
import com.example.evalapp_grupo12.viewmodel.ClimaViewModel
import com.example.evalapp_grupo12.viewmodel.EditarUsuarioViewModel
import com.example.evalapp_grupo12.viewmodel.EvaluacionViewModel
import com.example.evalapp_grupo12.viewmodel.EvaluarViewModel
import com.example.evalapp_grupo12.viewmodel.LoginViewModel
import com.example.evalapp_grupo12.viewmodel.RegistroViewModel
import com.example.evalapp_grupo12.viewmodel.RubricaViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FactoryTest {

    private val api = FakeApiService()
    private val usuarios = UsuarioRepository(FakeUsuarioDao())

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun cadaFactoryCreaSuViewModel() {
        assertNotNull(AlumnoViewModel.Factory(AlumnoRepository(api)).create(AlumnoViewModel::class.java))
        assertNotNull(RubricaViewModel.Factory(RubricaRepository(api)).create(RubricaViewModel::class.java))
        assertNotNull(EvaluarViewModel.Factory(EvaluacionRepository(api)).create(EvaluarViewModel::class.java))
        assertNotNull(EvaluacionViewModel.Factory(EvaluacionRepository(api)).create(EvaluacionViewModel::class.java))
        assertNotNull(ClimaViewModel.Factory(ClimaRepository(FakeClimaApiService())).create(ClimaViewModel::class.java))
        assertNotNull(LoginViewModel.Factory(usuarios).create(LoginViewModel::class.java))
        assertNotNull(RegistroViewModel.Factory(usuarios).create(RegistroViewModel::class.java))
        assertNotNull(EditarUsuarioViewModel.Factory(usuarios).create(EditarUsuarioViewModel::class.java))
    }

    @Test
    fun unaFactoryNoCreaOtroViewModel() {
        val otro = SesionViewModel::class.java

        assertThrows(IllegalArgumentException::class.java) { AlumnoViewModel.Factory(AlumnoRepository(api)).create(otro) }
        assertThrows(IllegalArgumentException::class.java) { RubricaViewModel.Factory(RubricaRepository(api)).create(otro) }
        assertThrows(IllegalArgumentException::class.java) { EvaluarViewModel.Factory(EvaluacionRepository(api)).create(otro) }
        assertThrows(IllegalArgumentException::class.java) { EvaluacionViewModel.Factory(EvaluacionRepository(api)).create(otro) }
        assertThrows(IllegalArgumentException::class.java) { ClimaViewModel.Factory(ClimaRepository(FakeClimaApiService())).create(otro) }
        assertThrows(IllegalArgumentException::class.java) { LoginViewModel.Factory(usuarios).create(otro) }
        assertThrows(IllegalArgumentException::class.java) { RegistroViewModel.Factory(usuarios).create(otro) }
        assertThrows(IllegalArgumentException::class.java) { EditarUsuarioViewModel.Factory(usuarios).create(otro) }
    }
}
