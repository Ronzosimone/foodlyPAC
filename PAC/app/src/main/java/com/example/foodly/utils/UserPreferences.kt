package com.example.foodly.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences private constructor(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "foodly_user_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_DIET_VEGAN = "diet_vegan"           // 0 or 1
    private const val KEY_DIET_VEGETARIAN = "diet_vegetarian" // 0 or 1
    private const val KEY_DIET_GLUTEN_FREE = "diet_gluten_free" // 0 or 1
        
        @Volatile
        private var INSTANCE: UserPreferences? = null
        
        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Salva l'ID dell'utente dopo login/registrazione
     */
    fun saveUserId(userId: Int) {
        sharedPreferences.edit {
            putInt(KEY_USER_ID, userId)
                .putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }
    
    /**
     * Recupera l'ID dell'utente salvato
     * @return L'ID dell'utente o null se non è loggato
     */
    fun getUserId(): Int? {
        return if (isLoggedIn()) {
            val userId = sharedPreferences.getInt(KEY_USER_ID, -1)
            if (userId != -1) userId else null
        } else {
            null
        }
    }
    
    /**
     * Verifica se l'utente è loggato
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Logout - pulisce i dati dell'utente
     */
    fun logout() {
        sharedPreferences.edit()
            .remove(KEY_USER_ID)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
    
    /**
     * Pulisce tutti i dati salvati
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    // ---- Diet Preferences (0 disabled, 1 enabled) ----
    fun setVegan(enabled: Boolean) {
        sharedPreferences.edit { putInt(KEY_DIET_VEGAN, if (enabled) 1 else 0) }
    }

    fun isVegan(): Boolean = sharedPreferences.getInt(KEY_DIET_VEGAN, 0) == 1
    fun getVeganInt(): Int = sharedPreferences.getInt(KEY_DIET_VEGAN, 0)

    fun setVegetarian(enabled: Boolean) {
        sharedPreferences.edit { putInt(KEY_DIET_VEGETARIAN, if (enabled) 1 else 0) }
    }

    fun isVegetarian(): Boolean = sharedPreferences.getInt(KEY_DIET_VEGETARIAN, 0) == 1
    fun getVegetarianInt(): Int = sharedPreferences.getInt(KEY_DIET_VEGETARIAN, 0)

    fun setGlutenFree(enabled: Boolean) {
        sharedPreferences.edit { putInt(KEY_DIET_GLUTEN_FREE, if (enabled) 1 else 0) }
    }

    fun isGlutenFree(): Boolean = sharedPreferences.getInt(KEY_DIET_GLUTEN_FREE, 0) == 1
    fun getGlutenFreeInt(): Int = sharedPreferences.getInt(KEY_DIET_GLUTEN_FREE, 0)
}
