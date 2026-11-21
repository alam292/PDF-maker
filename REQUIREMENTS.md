# PDF Maker: Architecture, Requirements & Governance Specification

## 1. Executive Summary & Project Overview
**PDF Maker** is a modern, high-performance Android application built entirely using **Kotlin** and **Jetpack Compose**. It allows users to create, view, organize, and manage PDF documents directly on their devices with full offline privacy and safety.

- **Platform Target**: Android (minSdk 24+, targetSdk 34+)
- **UI Toolkit**: Jetpack Compose (Material 3 Design System)
- **Primary Paradigms**: Unidirectional Data Flow (UDF), Clean Architecture, Kotlin Coroutines & Flow, Dependency Injection (Hilt).

---

## 2. Requirements & Feature Specifications

### 2.1 Core Functional Requirements
1. **Image to PDF Conversion**:
   - Multi-image selection via Android Photo Picker.
   - Reordering, preview, and page customization (page margins, orientation, fit/fill ratio).
   - High-fidelity conversion preserving source aspect ratio and applying optional compression.
2. **Text to PDF Conversion**:
   - Plain text and structured text document generation.
   - Configurable typography, line spacing, margins, and automatic multi-page wrapping.
3. **Built-in PDF Viewer**:
   - In-app rendering and inspection of generated PDF documents without external viewer dependencies.
   - Zoom, scroll, page count indication, and share intent handling.
4. **Document History & File Management**:
   - Listing of created PDF files with metadata (file size, page count, last modified date).
   - Renaming, sharing, and deleting operations with proper OS-level file safety.

### 2.2 Feature Restrictions & Anti-Requirements
- **No Third-Party Bloated Native Libraries**: Rely on Android native `android.graphics.pdf.PdfDocument` and native platform rendering engines wherever applicable, minimizing APK size and preventing binary bloat.
- **Strictly Offline-First**: No remote telemetry, analytics, or remote file processing without explicit permission. User documents must never leave the device.
- **Scoped Storage Compliance**: No legacy storage permissions (`READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE` for Android 10+); strictly follow Scoped Storage and MediaStore / App-specific directories.
- **No Direct Domain Dependencies on Android Framework**: Domain models, use cases, and repository interfaces must remain pure Kotlin.

---

## 3. Architecture Standards

### 3.1 Clean Architecture Package Structure
All code generation and modifications strictly abide by the structure defined in `.antigravityrules`:

```
app/src/main/java/com/examplealam292/pdfmaker/
│
├── MyApplication.kt
├── MainActivity.kt
│
├── core/
│   ├── common/             # Dispatchers, Base Result types, AppError, Extensions
│   ├── designsystem/       # Material3 Theme, Typography, Shapes, Common Components
│   ├── navigation/         # NavGraph, Route definitions, Screen destinations
│   ├── network/            # Network clients / interceptors (if applicable)
│   ├── database/           # Room DB instance & migrations
│   ├── datastore/          # Preferences & Proto DataStore
│   ├── security/           # Crypto & Keystore security helpers
│   └── utils/              # Image & File utilities, formatting helpers
│
├── data/
│   ├── local/              # Room DAOs, Entities, Local Data Sources
│   ├── remote/             # Remote API definitions & DTOs
│   ├── mapper/             # Entity <-> Domain Model mappers
│   └── repository/         # Repository implementations (data orchestration)
│
├── domain/
│   ├── model/              # Pure Kotlin business models
│   ├── repository/         # Repository interfaces (contracts)
│   └── usecase/            # Granular business use cases
│
├── feature/
│   ├── home/               # Dashboard, recent PDF list, quick actions
│   ├── imagetopdf/         # Image selection, reordering, conversion settings
│   ├── texttopdf/          # Text editor, typography settings, conversion
│   ├── viewer/             # PDF viewer screen & rendering controls
│   └── [feature_name]/     # Isolated feature modules
│
├── di/                     # Hilt DI modules (SingletonComponent)
└── worker/                 # WorkManager background tasks
```

### 3.2 Layer Isolation & Responsibilities
- **Domain Layer**:
  - The core of the application; completely decoupled from UI, Room, or Android SDK dependencies (except Android Parcelable where strictly required for navigation models).
  - Business rules encapsulated inside focused UseCases (`CreatePdfFromImagesUseCase`, `CreatePdfFromTextUseCase`, etc.).
- **Data Layer**:
  - Single Source of Truth pattern.
  - Exposes domain models to repositories through dedicated mappers.
- **UI / Feature Layer**:
  - Unidirectional Data Flow (UDF): ViewModels expose a single immutable `StateFlow<UiState>` and receive user `UiEvent`s.
  - Stateless composable functions for maximum previewability and reusability.

---

## 4. Development Workflow & Engineering Standards

### 4.1 Development Protocol
1. **Interface-Driven Contract Definition**: Define Domain models and Repository interfaces prior to implementing concrete data sources or UI.
2. **Isolated Feature Slices**: Changes in one feature (e.g., `feature/imagetopdf`) must not introduce regressions or direct coupled dependencies to another feature (e.g., `feature/texttopdf`).
3. **Resource Externalization**:
   - No hardcoded strings in Composable files; use `res/values/strings.xml`.
   - All color codes mapped through `core/designsystem/theme/Color.kt` and `Theme.kt`.

### 4.2 Quality Assurance & Build Verification
- Every feature increment must compile cleanly with `./gradlew assembleDebug` or `./gradlew test`.
- All background threads must use explicit `CoroutineDispatchers` (`Dispatchers.IO`, `Dispatchers.Default`) injected via DI.

---

## 5. Approval Permissions & Change Control

| Action / Scope | Required Approval | Policy & Guidelines |
|---|---|---|
| **Adding New Third-Party Dependencies** | **Explicit User Approval Required** | Third-party libraries (especially non-official or binary engines) must be explicitly vetted and approved to prevent APK bloat and security hazards. |
| **Architectural / Layer Boundary Refactoring** | **Explicit User Approval Required** | Changes altering package structure, moving core classes, or replacing Hilt / Room / Compose architectures. |
| **File Deletion / Destructive Changes** | **Explicit User Approval Required** | Deleting existing features, user data files, or critical source files. |
| **New Feature Implementation (within architecture)** | **Pre-Approved (Standard Workflow)** | Implementing features adhering to existing Clean Architecture conventions, creating use cases, ViewModels, and Compose UI. |
| **Bug Fixes, Optimizations & Refinements** | **Pre-Approved (Standard Workflow)** | Performance improvements, coroutine handling, UI polishing, and resolving compiler warnings. |
