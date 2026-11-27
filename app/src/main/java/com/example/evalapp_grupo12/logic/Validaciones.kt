package com.example.evalapp_grupo12.logic

object Validaciones {

    fun validarNombre(nombre: String): String? =
        if (nombre.length < 3) "Debe tener al menos 3 letras" else null

    fun validarCorreo(correo: String): String? =
        if (!correo.contains("@")) "Correo inválido" else null

    fun validarClave(clave: String): String? =
        if (clave.length < 6) "Debe tener al menos 6 caracteres" else null

    fun validarClaveLogin(clave: String): String? =
        if (clave.isBlank()) "La contraseña es obligatoria" else null

    fun validarConfirmacion(clave: String, confirmacion: String): String? =
        if (confirmacion != clave) "Las contraseñas no coinciden" else null

    fun validarRol(rol: String): String? =
        if (rol.isEmpty()) "Selecciona un rol" else null

    fun validarTitulo(titulo: String): String? =
        if (titulo.length < 3) "El título debe tener al menos 3 caracteres" else null

    fun validarArchivo(uri: String): String? =
        if (uri.isEmpty()) "Selecciona un archivo PDF o Word" else null
}
