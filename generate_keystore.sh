#!/bin/bash
echo "正在生成签名文件 app/release.keystore ..."
echo "请按照提示输入密钥库口令（密码）和证书信息。"
echo "注意：输入密码时，屏幕上不会显示字符，输入完毕后按回车即可。"
keytool -genkeypair -v -keystore app/release.keystore -alias release_key -keyalg RSA -keysize 2048 -validity 10000
echo "签名文件生成完毕。"
