package cat.copernic.appvehicles.core.auth

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos la instancia del DataStore (solo existirá una)
private val Context.dataStore by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {

    // Definimos las claves para guardar los datos
    companion object {
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_TOKEN = stringPreferencesKey("user_token")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    /**
     * Guarda el estado de la sesión en el dispositivo móvil.
     * NUNCA GUARDAMOS LA CONTRASEÑA, cumpliendo con las especificaciones.
     */
    suspend fun saveSession(email: String, name: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
            preferences[USER_TOKEN] = token
            preferences[IS_LOGGED_IN] = true
        }
    }

    /**
     * Cierra la sesión eliminando los datos guardados.
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Devuelve un Flow (flujo reactivo) que nos dirá en tiempo real si el usuario está logueado.
     * Ideal para decidir qué pantalla mostrar en Compose.
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    /**
     * Devuelve un Flow con el email del usuario logueado.
     */
    val userEmailFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }
}