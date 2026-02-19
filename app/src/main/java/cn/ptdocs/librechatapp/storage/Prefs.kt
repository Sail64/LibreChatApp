package cn.ptdocs.librechatapp.storage

import android.content.Context
import java.net.URI

object Prefs {
    private const val FILE = "mtls_webview"
    private const val KEY_ALIAS = "keychain_alias"
    private const val KEY_BASE_URL = "base_url"

    fun getAlias(ctx: Context): String? =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_ALIAS, null)

    fun setAlias(ctx: Context, alias: String) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ALIAS, alias)
            .apply()
    }

    fun clearAlias(ctx: Context) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_ALIAS)
            .apply()
    }

    fun getBaseUrl(ctx: Context): String? {
        return ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY_BASE_URL, null)
    }

    fun setBaseUrl(ctx: Context, url: String) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BASE_URL, url)
            .apply()
    }

    fun getHost(ctx: Context): String? {
        val url = getBaseUrl(ctx) ?: return null
        return try {
            URI(url).host
        } catch (e: Exception) {
            null
        }
    }
}
