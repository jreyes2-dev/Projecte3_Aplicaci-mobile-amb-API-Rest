package cat.copernic.appvehicles.core.composables

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(tempFile)

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()

        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}