import java.util.Properties

val buildConfigProps = Properties().apply {
    file("build-source/build-config.properties").inputStream().use { load(it) }
}
extra["buildConfig"] = buildConfigProps

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
