package com.example.evalapp_grupo12.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.evalapp_grupo12.data.AlumnoRepository
import com.example.evalapp_grupo12.data.EvaluacionRepository
import com.example.evalapp_grupo12.data.RetrofitClient
import com.example.evalapp_grupo12.data.RubricaRepository
import com.example.evalapp_grupo12.data.UsuarioDaoSQLite
import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.ui.screens.AlumnosScreen
import com.example.evalapp_grupo12.ui.screens.AplicarRubricaScreen
import com.example.evalapp_grupo12.ui.screens.BienvenidaScreen
import com.example.evalapp_grupo12.ui.screens.CargarScreen
import com.example.evalapp_grupo12.ui.screens.CerrarSesionScreen
import com.example.evalapp_grupo12.ui.screens.DocumentosScreen
import com.example.evalapp_grupo12.ui.screens.EditarRubricaScreen
import com.example.evalapp_grupo12.ui.screens.EditarUsuarioScreen
import com.example.evalapp_grupo12.ui.screens.EvaluarScreen
import com.example.evalapp_grupo12.ui.screens.InicioScreen
import com.example.evalapp_grupo12.ui.screens.LoginScreen
import com.example.evalapp_grupo12.ui.screens.RegistroScreen
import com.example.evalapp_grupo12.ui.screens.RubricasScreen
import com.example.evalapp_grupo12.ui.screens.VisorScreen
import com.example.evalapp_grupo12.viewmodel.AlumnoViewModel
import com.example.evalapp_grupo12.viewmodel.DocumentoViewModel
import com.example.evalapp_grupo12.viewmodel.EditarUsuarioViewModel
import com.example.evalapp_grupo12.viewmodel.EvaluarViewModel
import com.example.evalapp_grupo12.viewmodel.LoginViewModel
import com.example.evalapp_grupo12.viewmodel.RegistroViewModel
import com.example.evalapp_grupo12.viewmodel.RubricaViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel
import com.example.evalapp_grupo12.viewmodel.VisorViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val contexto = LocalContext.current.applicationContext

    val sesionViewModel: SesionViewModel = viewModel()
    val documentoViewModel: DocumentoViewModel = viewModel()
    val visorViewModel: VisorViewModel = viewModel()

    val usuarioRepository = UsuarioRepository(UsuarioDaoSQLite(contexto))

    val alumnoViewModel: AlumnoViewModel = viewModel(
        factory = AlumnoViewModel.Factory(AlumnoRepository(RetrofitClient.api))
    )
    val rubricaViewModel: RubricaViewModel = viewModel(
        factory = RubricaViewModel.Factory(RubricaRepository(RetrofitClient.api))
    )

    val evaluacionRepository = EvaluacionRepository(RetrofitClient.api)
    val evaluarViewModel: EvaluarViewModel = viewModel(
        factory = EvaluarViewModel.Factory(evaluacionRepository)
    )

    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ) {
        composable("bienvenida") {
            BienvenidaScreen(navController)
        }
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory(usuarioRepository))
            LoginScreen(navController, sesionViewModel, loginViewModel)
        }
        composable("registro") {
            val registroViewModel: RegistroViewModel = viewModel(factory = RegistroViewModel.Factory(usuarioRepository))
            RegistroScreen(navController, registroViewModel)
        }
        composable("inicio") {
            InicioScreen(navController, sesionViewModel)
        }
        composable("evaluar") {
            EvaluarScreen(navController, sesionViewModel, evaluarViewModel, alumnoViewModel, rubricaViewModel)
        }
        composable("aplicar_rubrica") {
            AplicarRubricaScreen(navController, sesionViewModel, evaluarViewModel)
        }
        composable("rubricas") {
            RubricasScreen(navController, sesionViewModel, rubricaViewModel)
        }
        composable("editar_rubrica") {
            EditarRubricaScreen(navController, sesionViewModel, rubricaViewModel)
        }
        composable("alumnos") {
            AlumnosScreen(navController, sesionViewModel, alumnoViewModel)
        }
        composable("editar_usuario") {
            val editarUsuarioViewModel: EditarUsuarioViewModel = viewModel(
                factory = EditarUsuarioViewModel.Factory(usuarioRepository)
            )
            EditarUsuarioScreen(navController, sesionViewModel, editarUsuarioViewModel)
        }
        composable("documentos") {
            DocumentosScreen(navController, sesionViewModel, documentoViewModel, visorViewModel)
        }
        composable("cargar") {
            CargarScreen(navController, sesionViewModel, documentoViewModel)
        }
        composable("visor") {
            VisorScreen(navController, visorViewModel)
        }
        composable("cerrar_sesion") {
            CerrarSesionScreen(navController, sesionViewModel)
        }
    }
}
