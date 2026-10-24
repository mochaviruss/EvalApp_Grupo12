package com.example.evalapp_grupo12.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.evalapp_grupo12.ui.screens.BienvenidaScreen
import com.example.evalapp_grupo12.ui.screens.CargarScreen
import com.example.evalapp_grupo12.ui.screens.CerrarSesionScreen
import com.example.evalapp_grupo12.ui.screens.DocumentosScreen
import com.example.evalapp_grupo12.ui.screens.EditarUsuarioScreen
import com.example.evalapp_grupo12.ui.screens.InicioScreen
import com.example.evalapp_grupo12.ui.screens.LoginScreen
import com.example.evalapp_grupo12.ui.screens.RegistroScreen
import com.example.evalapp_grupo12.ui.screens.VisorScreen
import com.example.evalapp_grupo12.viewmodel.DocumentoViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel
import com.example.evalapp_grupo12.viewmodel.VisorViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val sesionViewModel: SesionViewModel = viewModel()
    val documentoViewModel: DocumentoViewModel = viewModel()
    val visorViewModel: VisorViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ) {
        composable("bienvenida") {
            BienvenidaScreen(navController)
        }
        composable("login") {
            LoginScreen(navController, sesionViewModel)
        }
        composable("registro") {
            RegistroScreen(navController)
        }
        composable("inicio") {
            InicioScreen(navController, sesionViewModel)
        }
        composable("editar_usuario") {
            EditarUsuarioScreen(navController, sesionViewModel)
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
