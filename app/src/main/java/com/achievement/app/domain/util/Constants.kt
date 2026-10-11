package com.achievement.app.domain.util

object Constants {
    const val AUTH_BASE_URL = "https://ca.account.sony.com/api/authz/v3/oauth"
    const val PSN_API_BASE_URL = "https://m.np.playstation.com/api"
    
    const val CLIENT_ID = "09515159-7237-4370-9b40-3806e67c0891"
    const val REDIRECT_URI = "com.scee.psxandroid.scecompcall://redirect"
    const val CLIENT_AUTH = "Basic MDk1MTUxNTktNzIzNy00MzcwLTliNDAtMzgwNmU2N2MwODkxOnVjUGprYTV0bnRCMktxc1A="
    
    const val SCOPE = "psn:mobile.v2.core psn:clientapp"
    
    const val PSN_LOGIN_URL = "https://ca.account.sony.com/api/authz/v3/oauth/authorize"
    const val SSO_COOKIE_URL = "https://ca.account.sony.com/api/v1/ssocookie"
    
    const val GAME_LIST_LIMIT = 200
    const val TROPHY_LIST_LIMIT = 200
    
    const val DATASTORE_NAME = "achievement_preferences"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_TOKEN_EXPIRES_AT = "token_expires_at"
    const val KEY_ACCOUNT_ID = "account_id"
    const val KEY_ONLINE_ID = "online_id"
    
    const val TOKEN_REFRESH_THRESHOLD_MS = 5 * 60 * 1000L
}
