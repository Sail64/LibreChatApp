#!/bin/bash

echo "请输入 Keystore/Key 密码 (输入时不显示):"
read -s PASSWORD

# 验证 Store Password
if ! keytool -list -keystore app/release.keystore -storepass "$PASSWORD" > /dev/null 2>&1; then
    echo ""
    echo "错误：密码不正确。"
    exit 1
fi

# 验证 Key Password (使用相同的密码)
if ! keytool -list -v -keystore app/release.keystore -alias release_key -storepass "$PASSWORD" -keypass "$PASSWORD" > /dev/null 2>&1; then
    echo ""
    echo "错误：Key 密码不正确。"
    exit 1
fi

echo ""
echo "密码验证通过，开始构建 Release 包..."
./gradlew assembleRelease -PSTORE_PASSWORD="$PASSWORD" -PKEY_PASSWORD="$PASSWORD"

if [ $? -eq 0 ]; then
    echo ""
    echo "构建成功！"
    cp app/build/outputs/apk/release/app-release.apk ./release.apk
    echo "APK 已复制到当前目录：release.apk"
else
    echo ""
    echo "构建失败。"
    exit 1
fi
