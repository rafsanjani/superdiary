import javax.xml.parsers.DocumentBuilderFactory

plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.compose.multiplatform).apply(false)
    alias(libs.plugins.buildKonfig).apply(false)
    alias(libs.plugins.testLogger).apply(false)
    alias(libs.plugins.sonar).apply(false)
    alias(libs.plugins.android.test).apply(false)
    alias(libs.plugins.kotlin.parcelize).apply(false)
    alias(libs.plugins.ktlint).apply(false)
    id("com.superdiary.ktlint")
    id("com.superdiary.githooks")
    id("com.superdiary.snapshotdiff")
}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

// TODO: Move this into a plugin
tasks.register("printLineCoverage") {
    group = "verification"
    doLast {
        val report = file("build/reports/kover/report.xml")

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(report)
        val rootNode = doc.firstChild
        var childNode = rootNode.firstChild

        var coveragePercent = 0.0

        while (childNode != null) {
            if (childNode.nodeName == "counter") {
                val typeAttr = childNode.attributes.getNamedItem("type")
                if (typeAttr.textContent == "LINE") {
                    val missedAttr = childNode.attributes.getNamedItem("missed")
                    val coveredAttr = childNode.attributes.getNamedItem("covered")

                    val missed = missedAttr.textContent.toLong()
                    val covered = coveredAttr.textContent.toLong()

                    coveragePercent = (covered * 100.0) / (missed + covered)

                    break
                }
            }
            childNode = childNode.nextSibling
        }

        println("%.1f".format(coveragePercent))
    }
}
