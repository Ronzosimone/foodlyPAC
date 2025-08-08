package com.example.foodly.utils

/**
 * Funzioni di utilità per la gestione della sessione utente
 */
object SessionUtils {
    
    /**
     * Verifica se l'utente dovrebbe essere reindirizzato al login o alla home
     * @param context Context dell'applicazione
     * @return true se l'utente è già loggato, false se deve andare al login
     */
    fun shouldNavigateToHome(context: android.content.Context): Boolean {
        val userPrefs = UserPreferences.getInstance(context)
        return userPrefs.isLoggedIn() && userPrefs.getUserId() != null
    }
    
    /**
     * Ottiene l'ID dell'utente corrente se è loggato
     * @param context Context dell'applicazione
     * @return L'ID dell'utente o null se non è loggato
     */
    fun getCurrentUserId(context: android.content.Context): Int? {
        return UserPreferences.getInstance(context).getUserId()
    }
    
    /**
     * Effettua il logout completo dell'utente
     * @param context Context dell'applicazione
     */
    fun performLogout(context: android.content.Context) {
        UserPreferences.getInstance(context).logout()
    }
}
