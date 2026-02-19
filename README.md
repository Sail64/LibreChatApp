# LibreChat Android Wrapper (mTLS Support)

[中文文档](README_CN.md)

A custom Android native wrapper for [LibreChat](https://github.com/danny-avila/LibreChat), designed specifically for private deployments protected by **mTLS (Mutual TLS Authentication)**.

It encapsulates the LibreChat web interface within an Android native WebView, perfectly balancing **seamless user experience** with **enterprise-grade security**.

## 🚀 Experience Design

*   **Seamless "Out-of-the-Box" Experience**:
    *   Say goodbye to annoying browser certificate selection prompts.
    *   Say goodbye to re-entering your password every time you open the app.
    *   **One-time Setup** (select certificate + login once), and every subsequent launch takes you **straight to the chat interface**. Instant access.
*   **Immersive Large-Screen Experience**:
    *   Full-screen WebView design maximizes screen real estate.
    *   Perfectly adapted to LibreChat's responsive layout. Whether reading code blocks or long conversations, the experience far surpasses mobile browsers cluttered with address bars.
*   **Uncompromised Security**:
    *   Enjoy the convenience of password-free access while remaining protected by underlying mTLS certificates and HttpOnly Cookies. Combined with server-side mTLS mutual authentication, it provides a high level of security.

## ✨ Key Features

*   **🔐 mTLS Mutual Authentication**:
    *   Deep integration with Android KeyChain API.
    *   Automatically prompts the system certificate selector on first launch.
    *   Caches the certificate alias for **zero-interaction automatic handshakes** on subsequent launches.
    *   Certificate expiry reminders.
    *   Auto-clears cache and prompts for re-selection on 400 errors (certificate invalid).
*   **⚡ 30-Day Persistent Login**:
    *   Leverages WebView Cookie persistence combined with LibreChat's `refreshToken` for long-term sessions.
*   **🛡️ Security Hardening**:
    *   **Domain Whitelist**: Only allows loading the configured host domain. All external links (e.g., GitHub, Google) are intercepted and opened in the system browser to prevent phishing.
    *   **Privacy Protection**: Third-party cookies and form password saving are disabled by default.
    *   **No Cleartext Traffic**: Enforces HTTPS connections only.
*   **📂 File Upload**:
    *   Full support for LibreChat's file upload feature (integrates with system file picker/gallery).
*   **🎨 UI Adaptation**:
    *   Automatically adapts to system Dark/Light mode (status bar color switches automatically).
    *   Retains system status bar (non-immersive) to avoid obscuring web content.

## 🛠️ Configuration & Build

### 1. Requirements
*   **Server**: LibreChat deployed with Nginx reverse proxy enabling mTLS (`ssl_verify_client on`).
*   **Client**: Android 10 (API 29) or higher recommended.
*   **Certificate**: Client certificate (`.p12` or `.pfx`) must be installed on the Android device (Settings -> Security -> Encryption & credentials -> Install a certificate).

### 2. Configure Target URL
This project is configured with a sample address by default. Before building, you **MUST** modify `app/src/main/java/cn/ptdocs/librechatapp/AppConfig.kt`:

```kotlin
object AppConfig {
    // Change to your LibreChat domain
    const val HOST = "your-librechat-domain.com" 
    
    // Change to your port (use 443 for standard HTTPS)
    const val PORT = 443
    
    // Base URL is generated automatically
    const val BASE_URL = "https://$HOST:$PORT"
}
```

### 3. Build & Install
1. Open the project in Android Studio.
2. Wait for Gradle Sync to complete.
3. Connect your Android device or start an emulator.
4. Click **Run 'app'** to build and install.

## 📝 Usage Guide

1. **First Launch**:
   - The app initiates an mTLS handshake.
   - Android system pops up a "Select Certificate" dialog.
   - Choose your installed client certificate and click "Allow".
   - The app remembers your choice automatically.

2. **Login**:
   - Enter your LibreChat email and password.
   - Check "Keep me logged in" or rely on the server's Refresh Token policy.

3. **Daily Use**:
   - Next time you open the app, it automatically completes the mTLS handshake and restores your login session, taking you directly to the chat.

## ⚠️ Notes

*   **Certificate Management**: This app does NOT store your certificate private key directly. It uses the Android KeyChain API to reference system credentials, which is the most secure method.
*   **External Links**: For security, external links in chat content (e.g., AI-generated references) open in your default mobile browser, not inside the app.

## 🤝 Contributing

Issues and Pull Requests are welcome!

## 📄 License

[MIT License](LICENSE)
