plugins {
    java
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java"))
        }
    }

    test {
        java {
            setSrcDirs(listOf("src/test/java"))
        }
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.0")
}

application {
    mainClassName = "io.funfun.redbook.Application"
}
