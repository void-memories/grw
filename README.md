# 🚀 grw

> **GRW** • *Gradle Android CLI Tool*

A simple CLI tool for Android projects that abstracts away Gradle's complexity and long commands. Direct access to project models using the Gradle Tooling API - no plugin communication needed!

## ✨ Features

- **🎯 Build Variant Management** - Select and configure build variants with interactive prompts
- **🔨 Project Building** - Clean, build, and clean+build operations
- **📦 Asset Generation** - Generate APK or AAB files for any variant
- **🚀 App Deployment** - Build, install, and launch apps on devices/emulators
- **⚡ Task Execution** - Run any Gradle task with simplified syntax
- **⚙️ Configuration** - Persistent configuration management
- **🎨 Beautiful UI** - Colorful output with spinners and progress indicators

## 📦 Installation

From your Android app root directory, run:

```bash
curl -sSL https://raw.githubusercontent.com/void-memories/grw/main/setup.sh | bash
```

Then verify installation:

```bash
./grw --help
```

## 🛠️ Usage

### Basic Commands

```bash
# Configure build variant
./grw variant

# Build the project
./grw build

# Clean build artifacts
./grw clean

# Clean and build
./grw cleanBuild

# Generate APK
./grw gen apk

# Generate AAB
./grw gen aab

# Build, install and launch app
./grw run

# Execute any Gradle task
./grw task assembleDebug

# View current configuration
./grw config
```

### Interactive Build Variant Selection

```bash
./grw variant
```

Grw will automatically detect your project's flavors and build types, then present an interactive menu to select your desired build variant.

### Quick App Testing

```bash
./grw run
```

This command will:
1. Build your selected variant
2. Install it on the connected device/emulator
3. Launch the app automatically

## 🎯 Requirements

- Java 18+
- Android project with Gradle wrapper
- ADB (for device operations)

## 🏗️ Project Structure

- **Kotlin-based CLI** built with Clikt framework
- **Gradle Tooling API** integration for direct project access
- **Interactive prompts** using KInquirer
- **Colorful terminal output** with custom UI components

## 📝 License

This project is open source. Feel free to contribute! 