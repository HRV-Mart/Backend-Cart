import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
	id("io.gitlab.arturbosch.detekt") version "1.22.0"
	id("jacoco")// This is to use Jacoco for coverage testing
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	// Detekt-formatting
	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	// To run Jacoco Test Coverage Verification
	finalizedBy("jacocoTestCoverageVerification")
}
/*
* detekt configs*/
detekt {
	toolVersion = "1.22.0"
	config = files("config/detekt/detekt.yml")
}
/*
* Jacoco configs*/
tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			excludes = listOf(
				"com.hrv.mart.user.repository.UserRepository.kt.*"
			)
			limit {
				minimum = "0.9".toBigDecimal()
			}
		}
	}
}
tasks.jacocoTestReport{
	reports {
		html.required.set(true)
		generate()
	}
}