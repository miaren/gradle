plugins {
    java
}

val testToolchain = System.getProperty("testToolchain", "knownVendor")

if (testToolchain == "knownVendor") {
// The bodies of the if statements are intentionally not indented to make the user guide page prettier.

// tag::toolchain-known-vendor[]
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
// end::toolchain-known-vendor[]

} else if (testToolchain == "matchingVendor") {
// tag::toolchain-matching-vendor[]
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.matching("customString")
    }
}
// end::toolchain-matching-vendor[]

} else if (testToolchain == "matchingImplementation") {
// tag::toolchain-matching-implementation[]
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.IBM
        implementation = JvmImplementation.J9
    }
}
// end::toolchain-matching-implementation[]
}
