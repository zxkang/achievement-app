# 构建说明

## 方法一：使用Android Studio（推荐）

1. **打开Android Studio**
   - 启动Android Studio

2. **打开项目**
   - File → Open
   - 选择 `D:\Workspace\android\achievement-app` 目录
   - 点击 OK

3. **同步Gradle**
   - Android Studio会自动下载依赖并同步Gradle
   - 等待底部进度条完成（首次可能需要几分钟）

4. **构建项目**
   - Build → Make Project (Ctrl+F9)
   - 或者 Build → Rebuild Project

5. **运行应用**
   - 连接Android设备或启动模拟器
   - 点击运行按钮 (Shift+F10)
   - 选择目标设备

## 方法二：使用命令行

### 前提条件
- 安装JDK 17或更高版本
- 安装Android SDK
- 设置环境变量：
  ```bash
  export ANDROID_HOME=/path/to/android/sdk
  export PATH=$PATH:$ANDROID_HOME/tools/bin
  export PATH=$PATH:$ANDROID_HOME/platform-tools
  ```

### 构建步骤

1. **生成Gradle Wrapper**（如果Android Studio已打开过项目）
   ```bash
   cd D:\Workspace\android\achievement-app
   gradle wrapper
   ```

2. **构建Debug版本**
   ```bash
   ./gradlew assembleDebug
   ```

3. **构建Release版本**
   ```bash
   ./gradlew assembleRelease
   ```

4. **清理构建**
   ```bash
   ./gradlew clean
   ```

## 构建输出

构建成功后，APK文件位于：
- Debug版本: `app/build/outputs/apk/debug/app-debug.apk`
- Release版本: `app/build/outputs/apk/release/app-release.apk`

## 常见问题

### 1. Gradle同步失败
- 检查网络连接
- 确认可以访问 Google Maven仓库
- 尝试使用代理或镜像源

### 2. SDK版本问题
- 确保安装了Android SDK 34
- 在Android Studio中: Tools → SDK Manager

### 3. JDK版本问题
- 确保使用JDK 17
- File → Settings → Build → Gradle → Gradle JDK

### 4. 内存不足
- 编辑 `gradle.properties`
- 增加: `org.gradle.jvmargs=-Xmx4096m`

## 依赖说明

项目使用以下主要依赖：
- AndroidX Core 1.12.0
- Compose BOM 2023.10.01
- Hilt 2.48
- Room 2.6.1
- Retrofit 2.9.0
- Kotlinx Serialization 1.6.0
- Coil 2.5.0

所有依赖会从Maven Central和Google Maven自动下载。
