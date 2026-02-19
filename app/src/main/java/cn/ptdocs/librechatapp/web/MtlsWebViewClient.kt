package cn.ptdocs.librechatapp.web

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.security.KeyChain
import android.util.Log
import android.webkit.ClientCertRequest
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.ptdocs.librechatapp.storage.Prefs
import java.security.cert.X509Certificate
import java.util.Date
import java.util.concurrent.TimeUnit

import android.widget.Toast

class MtlsWebViewClient(
    private val activity: Activity,
    private val onSettingsVisibilityChange: (Boolean) -> Unit
) : WebViewClient() {

    companion object {
        private const val TAG = "MtlsWebViewClient"
        private const val CERT_CLEAR_COOLDOWN_MS = 5000L
    }

    private var hasShownExpiryWarning = false
    private var lastCertClearTime = 0L

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url
        val currentHost = Prefs.getHost(activity)
        if (currentHost != null && url.host == currentHost) {
            return false
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, url)
            activity.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open external URL: $url", e)
        }
        return true
    }

    private fun checkCertificateExpiry(cert: X509Certificate) {
        if (hasShownExpiryWarning) return

        val expiryDate = cert.notAfter
        val now = Date()
        val diff = expiryDate.time - now.time
        
        // 如果证书已经过期，diff 会是负数，这里我们只关心即将过期的（比如还有 30 天）
        // 如果已经过期，通常 TLS 握手会失败，或者浏览器会提示证书错误，但这里我们也提示一下
        val daysLeft = TimeUnit.MILLISECONDS.toDays(diff)

        Log.d(TAG, "Certificate expires in $daysLeft days")

        if (daysLeft < 30) {
            hasShownExpiryWarning = true
            activity.runOnUiThread {
                val message = if (daysLeft < 0) {
                    "您的证书已过期，请立即更新。"
                } else {
                    "您的客户端证书将在 $daysLeft 天后过期，请及时联系管理员更新。"
                }
                
                AlertDialog.Builder(activity)
                    .setTitle("证书提醒")
                    .setMessage(message)
                    .setPositiveButton("确定", null)
                    .show()
            }
        }
    }

    override fun onReceivedClientCertRequest(view: WebView, request: ClientCertRequest) {
        val savedAlias = Prefs.getAlias(activity)

        Log.d(TAG, "Client cert request: host=${request.host} port=${request.port} savedAlias=${savedAlias != null}")

        lateinit var proceedWithAlias: (String) -> Unit

        fun chooseAlias() {
            Log.d(TAG, "Prompting for client cert alias")
            KeyChain.choosePrivateKeyAlias(
                activity,
                { alias ->
                    Log.d(TAG, "Alias selection result: ${alias ?: "null"}")
                    if (alias != null) {
                        Prefs.setAlias(activity, alias)
                        proceedWithAlias(alias)
                    } else {
                        request.ignore()
                    }
                },
                request.keyTypes,
                request.principals,
                request.host,
                request.port,
                null
            )
        }

        proceedWithAlias = { alias ->
            Thread {
                try {
                    val privateKey = KeyChain.getPrivateKey(activity, alias)
                    val chain = KeyChain.getCertificateChain(activity, alias)
                    if (privateKey != null && chain != null) {
                        Log.d(TAG, "Proceeding with alias: $alias, chainLen=${chain.size}")

                        if (chain.isNotEmpty()) {
                            checkCertificateExpiry(chain[0])
                        }

                        request.proceed(privateKey, chain)
                    } else {
                        Log.w(TAG, "Missing key/chain for alias: $alias")
                        activity.runOnUiThread { chooseAlias() }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load key/chain for alias: $alias", e)
                    activity.runOnUiThread { chooseAlias() }
                }
            }.start()
        }

        if (savedAlias != null) {
            Log.d(TAG, "Using cached alias")
            proceedWithAlias(savedAlias)
            return
        }

        chooseAlias()
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        Log.e(TAG, "onReceivedError: errorCode=${error.errorCode}, description=${error.description}, url=${request.url}")
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse
    ) {
        Log.e(TAG, "onReceivedHttpError: statusCode=${errorResponse.statusCode}, reasonPhrase=${errorResponse.reasonPhrase}, url=${request.url}")

        if (request.isForMainFrame && errorResponse.statusCode == 400) {
            val savedAlias = Prefs.getAlias(activity)
            val currentTime = System.currentTimeMillis()

            if (savedAlias != null && (currentTime - lastCertClearTime > CERT_CLEAR_COOLDOWN_MS)) {
                Log.d(TAG, "Clearing client cert preferences due to 400 error on main frame")
                lastCertClearTime = currentTime

                // Clear alias
                Prefs.clearAlias(activity)

                // Clear WebView cache
                WebView.clearClientCertPreferences {
                    Log.d(TAG, "Client cert preferences cleared")
                    activity.runOnUiThread {
                        Toast.makeText(activity, "客户端证书已失效，请重新选择", Toast.LENGTH_LONG).show()
                        view.reload()
                    }
                }
            }
        }

        super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        CookieManager.getInstance().flush()
        Log.d(TAG, "Page finished, cookies flushed: $url")

        val cookies = CookieManager.getInstance().getCookie(url)
        val isLoginPage = url.contains("/login")
        val hasCookies = !cookies.isNullOrEmpty()
        
        // Show settings if on login page OR no cookies
        val shouldShowSettings = isLoginPage || !hasCookies
        
        activity.runOnUiThread {
            onSettingsVisibilityChange(shouldShowSettings)
        }
    }
}
