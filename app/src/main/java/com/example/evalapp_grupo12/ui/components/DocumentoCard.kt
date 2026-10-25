package com.example.evalapp_grupo12.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.evalapp_grupo12.model.Documento
import com.example.evalapp_grupo12.ui.theme.ColorPdf
import com.example.evalapp_grupo12.ui.theme.ColorWord

@Composable
fun DocumentoCard(
    documento: Documento,
    onVer: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (documento.tipo == "PDF") {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = "PDF", tint = ColorPdf, modifier = Modifier.size(40.dp))
            } else {
                Icon(Icons.Filled.Description, contentDescription = "Word", tint = ColorWord, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = documento.titulo, fontWeight = FontWeight.Bold)
                Text(text = documento.tipo)
            }

            IconButton(onClick = onVer) {
                Icon(Icons.Filled.Visibility, contentDescription = "Ver documento")
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
