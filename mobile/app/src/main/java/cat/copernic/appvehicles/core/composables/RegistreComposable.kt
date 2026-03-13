package cat.copernic.appvehicles.core.composables

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ReusableTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    modifier: Modifier = Modifier, isPassword: Boolean = false, placeholder: String? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } }, modifier = modifier.fillMaxWidth(),
        singleLine = true, visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = MaterialTheme.shapes.medium
    )
}

/**
 * Función auxiliar (Custom Hook) que esconde toda la lógica compleja.
 * Devuelve un ImageBitmap listo para pintar, procesado en segundo plano.
 */
@Composable
fun rememberBase64Bitmap(imageUri: String?): ImageBitmap? {
    var bitmap by remember(imageUri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUri) {
        if (imageUri?.startsWith("data:image") == true) {
            withContext(Dispatchers.IO) {
                try {
                    val cleanBase64 = imageUri.substringAfter(",").replace("\\s+".toRegex(), "")
                    val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                } catch (e: Exception) {
                    bitmap = null
                }
            }
        } else {
            bitmap = null
        }
    }
    return bitmap
}

@Composable
fun ImageUploadOrPreview(
    label: String, imageUri: String?,
    onUploadClick: () -> Unit, onDeleteClick: () -> Unit
) {
    // Usamos nuestra función mágica para limpiar el código
    val base64Bitmap = rememberBase64Bitmap(imageUri)

    if (imageUri == null) {
        // NO HAY FOTO: Botón de subir
        Surface(
            modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onUploadClick() },
            color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        // SÍ HAY FOTO
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {

            // 1. Si es Base64 y ya se ha decodificado -> Pintamos Imagen Nativa
            if (base64Bitmap != null) {
                Image(
                    bitmap = base64Bitmap, contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            // 2. Si NO es Base64 (es URI local de la galería) -> Usamos Coil
            else if (!imageUri.startsWith("data:image")) {
                AsyncImage(
                    model = imageUri, contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            // 3. Mientras el Base64 se carga -> Ruedecita de carga opcional
            else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Papelera
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), CircleShape).size(36.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}