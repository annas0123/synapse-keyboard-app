# 🚀 RUN APP STEERING FILE

**Purpose:** This file contains the automated command to run the Synapse Keyboard app.

**Trigger:** When user says "run app" or "run the app" or "launch app"

---

## 📋 RUN COMMAND

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"; cd "e:\flutter\2 Ai Keyboard\SynapseApp"; .\gradlew.bat installDebug
```

## 📱 LAUNCH COMMAND

```powershell
C:\Users\ANNAS\AppData\Local\Android\Sdk\platform-tools\adb.exe shell am start -n com.smafty.synapsekeyboard/.MainActivity
```

---

## 🔄 AUTOMATED WORKFLOW

When user says "run app":

1. **Build & Install:**
   - Set JAVA_HOME
   - Navigate to SynapseApp directory
   - Run `gradlew.bat installDebug`
   - Wait for successful installation

2. **Launch App:**
   - Use full ADB path to launch MainActivity
   - App should open on connected device

---

## ⚠️ NOTES

- ADB path is hardcoded: `C:\Users\ANNAS\AppData\Local\Android\Sdk\platform-tools\adb.exe`
- JAVA_HOME: `C:\Program Files\Java\jdk-17`
- Package name: `com.smafty.synapsekeyboard`
- Main activity: `.MainActivity`
