package com.example.evalapp_grupo12.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.evalapp_grupo12.model.Usuario
import kotlinx.coroutines.launch

data class Seccion(
    val titulo: String,
    val ruta: String,
    val icono: ImageVector
)

val secciones = listOf(
    Seccion("Inicio", "inicio", Icons.Filled.Home),
    Seccion("Evaluar", "evaluar", Icons.Filled.EditNote),
    Seccion("Rúbricas", "rubricas", Icons.Filled.Checklist),
    Seccion("Alumnos", "alumnos", Icons.Filled.Groups)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuApp(
    navController: NavController,
    usuario: Usuario,
    titulo: String,
    contenido: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun irA(ruta: String) {
        scope.launch { drawerState.close() }
        navController.navigate(ruta) {
            popUpTo("inicio")
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                Text(
                    text = "${usuario.nombre}\n${usuario.correo}\n${usuario.rol}",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                for (seccion in secciones) {
                    NavigationDrawerItem(
                        label = { Text(seccion.titulo) },
                        icon = { Icon(seccion.icono, contentDescription = null) },
                        selected = titulo == seccion.titulo,
                        onClick = { irA(seccion.ruta) }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Mis documentos") },
                    icon = { Icon(Icons.Filled.Folder, contentDescription = null) },
                    selected = titulo == "Mis documentos",
                    onClick = { irA("documentos") }
                )
                NavigationDrawerItem(
                    label = { Text("Cargar documento") },
                    icon = { Icon(Icons.Filled.UploadFile, contentDescription = null) },
                    selected = false,
                    onClick = { irA("cargar") }
                )
                NavigationDrawerItem(
                    label = { Text("Editar mis datos") },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    selected = false,
                    onClick = { irA("editar_usuario") }
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("cerrar_sesion")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    title = { Text(titulo) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("editar_usuario") }) {
                            Icon(Icons.Filled.Person, contentDescription = "Editar mis datos")
                        }
                        IconButton(onClick = { navController.navigate("cerrar_sesion") }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    for (seccion in secciones) {
                        NavigationBarItem(
                            selected = titulo == seccion.titulo,
                            onClick = { irA(seccion.ruta) },
                            icon = { Icon(seccion.icono, contentDescription = null) },
                            label = { Text(seccion.titulo) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                contenido()
            }
        }
    }
}
