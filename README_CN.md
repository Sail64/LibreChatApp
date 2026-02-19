# LibreChat Android Wrapper (mTLS Support)

这是一个为 [LibreChat](https://github.com/danny-avila/LibreChat) 定制的 Android 原生壳应用，专为使用 **mTLS (双向 TLS 认证)** 保护的私有部署环境设计。

它通过 Android 原生 WebView 封装了 LibreChat 网页版，完美平衡了 **极致便捷的体验** 与 **企业级的安全性**。

## 🚀 本APP的体验设计

*   **极致流畅的“开箱即用”体验**：
    *   告别繁琐的浏览器证书选择弹窗。
    *   告别每次打开都要重新输入密码的痛苦。
    *   **只需一次配置**（首次选择证书 + 首次登录），此后每次启动 App **直达聊天界面**，秒开秒用。
*   **沉浸式的大屏阅读体验**：
    *   全屏 WebView 设计，最大化利用手机屏幕空间。
    *   完美适配 LibreChat 的响应式布局，无论是代码块还是长对话，阅读体验远超手机浏览器地址栏遮挡的局促感。
*   **不妥协的安全性**：
    *   在享受免密便利的同时，依然由底层 mTLS 证书和 HttpOnly Cookie 守护安全，结合服务端的mTLS双向认证，具有较高的安全性。

## ✨ 主要特性

*   **🔐 mTLS 双向认证支持**：
    *   深度集成 Android KeyChain API。
    *   首次访问自动弹出系统证书选择器。
    *   自动缓存证书别名，后续启动 **零交互自动握手**。
    *   支持证书过期提醒。
    *   遇到 400 错误（证书失效）自动清除缓存并提示重新选择。
*   **⚡ 30 天免密登录**：
    *   通过 WebView Cookie 持久化机制，配合 LibreChat 的 `refreshToken` 实现长效登录。
    *   避免了每次打开 App 都要重新输入密码的繁琐。
*   **🛡️ 安全增强**：
    *   **域名白名单**：仅允许访问配置的主机域名，拦截所有外部链接（如 GitHub、Google）并跳转至系统浏览器，防止钓鱼攻击。
    *   **隐私保护**：默认禁用第三方 Cookie，禁用表单密码保存。
    *   **明文流量禁止**：强制仅允许 HTTPS 连接。
*   **📂 文件上传**：
    *   完整支持 LibreChat 的文件上传功能（对接系统文件选择器/相册）。
*   **🎨 UI 适配**：
    *   自动适配系统深色/浅色模式（状态栏颜色自动切换）。
    *   保留系统状态栏，非沉浸式设计，避免遮挡网页内容。

## 🛠️ 部署与配置

### 1. 环境要求
*   **服务端**：已部署 LibreChat，并配置了 Nginx 反向代理启用 mTLS (`ssl_verify_client on`)。
*   **客户端**：Android 10 (API 29) 及以上版本推荐。
*   **证书**：需在 Android 设备上安装客户端证书（`.p12` 或 `.pfx` 格式），通常在“设置 -> 安全 -> 加密与凭据 -> 安装证书”中安装。

### 2. 修改目标地址
本项目默认配置为示例地址。在编译前，请务必修改 `app/src/main/java/cn/ptdocs/librechatapp/AppConfig.kt` 文件：

```kotlin
object AppConfig {
    // 修改为您自己的 LibreChat 域名
    const val HOST = "your-librechat-domain.com" 
    
    // 修改为您的端口号（如果是 443 标准端口也请填写）
    const val PORT = 443
    
    // 自动生成 Base URL
    const val BASE_URL = "https://$HOST:$PORT"
}
```

### 3. 编译与安装
1. 使用 Android Studio 打开本项目。
2. 等待 Gradle Sync 完成。
3. 连接 Android 设备或启动模拟器。
4. 点击 **Run 'app'** 进行编译安装。

## 📝 使用指南

1. **首次启动**：
   - App 会发起 mTLS 握手请求。
   - Android 系统会弹出“选择证书”对话框。
   - 选择您已安装的客户端证书，点击“允许”。
   - App 将自动记住您的选择。

2. **登录**：
   - 输入 LibreChat 账号密码登录。
   - 勾选“保持登录”或依赖服务端的 Refresh Token 策略。

3. **日常使用**：
   - 下次启动 App 时，将自动完成 mTLS 握手并恢复登录状态，直接进入聊天界面。

## ⚠️ 注意事项

*   **证书管理**：本 App 不直接存储证书私钥，而是通过 Android KeyChain API 引用系统凭据。这是最安全的做法。
*   **外部链接**：为了安全起见，聊天内容中的外部链接（如 AI 生成的参考链接）点击后会跳转到手机默认浏览器打开，不会在 App 内部加载。

## 🤝 贡献

欢迎提交 Issue 或 Pull Request 来改进本项目。

## 📄 许可证

[MIT License](LICENSE)
