import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.artifact.SingleArtifact
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.plugin)
    alias(libs.plugins.firebase.appdistribution.plugin)
}

android {
    namespace = "com.aldiprahasta.tmdb"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aldiprahasta.tmdb"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.10"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val tmpFilePath = System.getProperty("user.home") + "/work/_temp/keystore"
            val allFilesFromDir = File(tmpFilePath).listFiles()

            if (allFilesFromDir != null) {
                val keystoreFile = allFilesFromDir.first()
                keystoreFile.renameTo(File("keystore/tmdb_keystore.jks"))
            }

            val localProperties = Properties()
            localProperties.load(FileInputStream(rootProject.file("local.properties")))

            storeFile = file("keystore/tmdb_keystore.jks")
            storePassword = localProperties["STORE_PASSWORD"].toString()
            keyAlias = localProperties["KEY_ALIAS"].toString()
            keyPassword = localProperties["KEY_PASSWORD"].toString()
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            optIn.add("kotlin.RequiresOptIn")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
    ndkVersion = "28.2.13676358"
}

abstract class RenameApk : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val apkName: Property<String>

    @get:Internal
    abstract val transformation: Property<ArtifactTransformationRequest<RenameApk>>

    @TaskAction
    fun renameApk() {
        val output = outputDirectory.file(apkName).get().asFile
        transformation.get().submit(this) { artifact ->
            File(artifact.outputFile).copyTo(output, overwrite = true)
        }
    }
}

androidComponents.onVariants { variant ->
    val variantName = variant.name.replaceFirstChar { it.uppercaseChar() }
    val apkTask = tasks.register<RenameApk>("rename${variantName}Apk") {
        apkName.set(if (variant.buildType == "debug") "TMDb-debug.apk" else "TMDb.apk")
    }
    val transformation = variant.artifacts.use(apkTask)
        .wiredWithDirectories(RenameApk::inputDirectory, RenameApk::outputDirectory)
        .toTransformMany(SingleArtifact.APK)
    apkTask.configure {
        this.transformation.set(transformation)
    }
}

dependencies {
    implementation(platform(libs.compose.bom))

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.compose.base)

    implementation(libs.bundles.networking)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.bundles.utilities)

    implementation(libs.bundles.paging)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit4)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(libs.bundles.compose.test)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
}
