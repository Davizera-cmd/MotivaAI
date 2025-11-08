// Define os plugins que estamos usando
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp") // Plugin para o Room
}

android {
    namespace = "com.example.motivaai"
    compileSdk = 34 // Pode ser 34 ou mais recente

    defaultConfig {
        applicationId = "com.example.motivaai"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "META-INF/native-image/**"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Vai ser jetpack composer!
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Bloco de dependências
dependencies {

    // --- Bibliotecas Padrão do AndroidX ---
    implementation("androidx.core:core-ktx:1.13.1") // Pode usar a versão que o Studio criou
    implementation("androidx.appcompat:appcompat:1.6.1") // Pode usar a versão que o Studio criou
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // --- IA Generativa (Google AI - Gemini) ---
    // (RF04: Personalização da IA Motivacional)
    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")

    // --- Banco de Dados Local (Room) ---
    // (RNF02: Armazenamento Local, RF01, RF05)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion") // Suporte a Coroutines
    // Para usar o processador de anotações KSP (mais rápido)
    ksp("androidx.room:room-compiler:$roomVersion")

    // --- Agendamento de Tarefas (WorkManager) ---
    // (RF02: Agendamento de Notificações, RNF05: Otimização)
    val workVersion = "2.9.0"
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    // --- Componentes de Ciclo de Vida (ViewModel e LiveData) ---
    val lifecycleVersion = "2.8.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // --- Geração de PDF (iText7 - versão para Android) ---
    // (RF07: Exportação do diário para PDF)
    // Usamos a versão "core" que é compatível com Android
    implementation("com.itextpdf:itext7-core:8.0.4")

    // --- Jetpack Compose (UI Moderna) ---
    val composeBomVersion = "2024.05.00" // Bill of Materials (BOM)
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3") // Componentes do Material Design 3

    // Integração do Compose com Atividades e ViewModels
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    // Ferramentas de debug (previews do Compose)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}