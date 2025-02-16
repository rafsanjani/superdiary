package com.superdiary.gradle.snapshots

import java.io.File
import java.io.FileOutputStream
import org.gradle.api.Plugin
import org.gradle.api.Project

/** This plugin should be applied to the root project */
class SnapshotsDiffPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        target.subprojects.forEach(::registerReportCommentTask)
    }

    private fun registerReportCommentTask(project: Project) {
        project.plugins.withId("app.cash.paparazzi") {
            with(project) {
                tasks.register("createPaparazziReportComment") {
                    doLast {
                        val reportDirectory = layout.buildDirectory.dir("paparazzi/failures").get().toString()
                        val deltaFiles = File(reportDirectory)
                            .listFiles()
                            ?.filter { file ->
                                file.name.startsWith("delta")
                            }

                        val pullRequestNumber = System.getenv("BUILD_NUMBER")

                        val outputFilePath = "snapshots.md"
                        val outputFile = FileOutputStream(outputFilePath)
                        deltaFiles?.forEach { image ->
                            val filePath = image
                                .name
                                .toString()
                                .replace("[", "%5B")
                                .replace("]", "%5D")

                            val header = "#### ${image.name}\n"
                            val data =
                                "<img alt=\"paparazzi failure\" src=\"https://github.com/rafsanjani/superdiary/raw/paparazzi-snapshots-$pullRequestNumber/$filePath\"/>\n\n"

                            outputFile.write(header.toByteArray())
                            outputFile.write(data.toByteArray())
                        }
                        outputFile.close()
                    }
                }
            }
        }
//        project.afterEvaluate {
//            if (!project.pluginManager.hasPlugin("app.cash.paparazzi")) {
//                return@afterEvaluate
//            }
//
//            with(project) {
//                tasks.register("createPaparazziReportComment") {
//                    doLast {
//                        val reportDirectory = layout.buildDirectory.dir("paparazzi/failures").get().toString()
//                        val deltaFiles = File(reportDirectory)
//                            .listFiles()
//                            ?.filter { file ->
//                                file.name.startsWith("delta")
//                            }
//
//                        val pullRequestNumber = System.getenv("BUILD_NUMBER")
//
//                        val outputFilePath = "snapshots.md"
//                        val outputFile = FileOutputStream(outputFilePath)
//                        deltaFiles?.forEach { image ->
//                            val filePath = image
//                                .name
//                                .toString()
//                                .replace("[", "%5B")
//                                .replace("]", "%5D")
//
//                            val header = "#### ${image.name}\n"
//                            val data =
//                                "<img alt=\"paparazzi failure\" src=\"https://github.com/rafsanjani/superdiary/raw/paparazzi-snapshots-$pullRequestNumber/$filePath\"/>\n\n"
//
//                            outputFile.write(header.toByteArray())
//                            outputFile.write(data.toByteArray())
//                        }
//                        outputFile.close()
//                    }
//                }
//            }
//        }
    }
}
