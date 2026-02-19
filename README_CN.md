# LibreChatApp

> **非官方客户端声明**
>
> 本项目是 **LibreChat** 的一个 **非官方 Android 客户端**，仅提供全屏 WebView 封装能力，
> 方便在 Android 设备上访问你的 LibreChat 服务端。它不隶属于 LibreChat 官方团队。

## 截图

> 截图展示的是 **LibreChat 的网页界面**，本应用仅做全屏 WebView 封装和免输密码认证。

![Screenshot 1](screenshots/Screenshot_20260219_182457_cn.ptdocs.librechatapp.jpg)
![Screenshot 2](screenshots/Screenshot_20260219_182528_cn.ptdocs.librechatapp.jpg)

---

## 简介

我想要说明一下我做这个项目的背景：
我经常要在手机上使用，而官方没有APP客户端，在手机上使用有以下几点使用不便：
1、要输入密码，哪怕是浏览器记录密码，仍然需要有一次登录过程。
2、LibreChat的网页非常优雅美观，但是放在手机浏览器里，浏览器的地址栏和菜单会占用过多屏幕空间，同时会
对LibreChat的网页产生干扰。
3、我把LibreChat部署在我的内网，我部署了一个nginx反向代理服务器，采用mTLS认证的方式，以便加强安全性，
这就要求手机浏览器能够做到出示客户端证书和缓存证书（别名），但目前只有Chrome/firefox等大型浏览器能够实现
这一点，但是他们太重了。

基于这些背景我决定开发一款自己的轻量级APP，LibreChatApp 就是这样一款轻量的 Android WebView 客户端，
访问自部署的 LibreChat 服务端。它支持配置服务器地址、mTLS 客户端证书选择与缓存、证书过期提醒，以及文件
的上传和下载。

---

## 功能特性

- **服务器地址配置**（首次启动设置，可随时更改）
- **mTLS 客户端证书支持**（选择证书、缓存 alias、证书过期提醒）
- **证书失效自动清理**（证书到期前一个月有更换证书提示，同时支持服务端吊销证书的处理）
- **WebView 安全配置**（禁止混合内容、关闭第三方 Cookie）
- **文件上传、下载支持**（HTTP、data URL、blob URL，保存到系统下载目录）

---

## 使用说明

### 1. 设置服务器地址
首次启动会弹窗要求设置服务器地址。之后可以在登录页或无 Cookie 时点击底部“设置服务器地址”进行修改。

### 2. mTLS 客户端证书
如果服务端启用了双向 TLS，应用会弹出系统证书选择器。选择后会缓存证书 alias，且会在证书即将过期时提示。

### 3. 文件上传和下载
支持对话时上传文件，支持导出记录时的文件下载。

---

## 构建与发布

### 开发构建
```bash
./gradlew assembleDebug
```

### 生成签名文件
```bash
./generate_keystore.sh
```

### 构建 Release
```bash
./build_release.sh
```
构建成功后会生成 `release.apk`。

---

## 项目结构

```
app/src/main/java/cn/ptdocs/librechatapp/
├── MainActivity.kt               # WebView 容器与设置入口
├── storage/Prefs.kt              # SharedPreferences（服务器地址、证书 alias）
└── web/
    ├── AppWebChromeClient.kt     # 文件选择回调
    ├── DownloadHandler.kt        # 下载处理（HTTP / data / blob）
    ├── MtlsWebViewClient.kt      # mTLS 证书逻辑、外链处理、证书过期提示
    └── WebViewConfigurator.kt    # WebView 安全配置
```

---

## 权限

- `android.permission.INTERNET`

---

## License

MIT License. 详见 [LICENSE](LICENSE)。
