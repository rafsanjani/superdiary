package codequality

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import java.util.Locale

class GitHooksPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureGitHooksPlugin()
        }
    }
}

private fun Project.configureGitHooksPlugin() {
    tasks.register("copyGitHooks", Copy::class.java) {
        description = "Copies the git hooks from /git-hooks to the .git folder"
        from("${rootDir}/git-hooks/") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            include("**/*.sh")
            rename("(.*).sh", "$1")
        }
        into("${rootDir}/.git/hooks")
        onlyIf {
            isLinuxOrMacOs()
        }
    }

    tasks.register("installGitHooks", Exec::class.java) {
        description = "Installs the pre-commit git hooks from /git-hooks."
        group = "git hooks"
        workingDir = rootDir
        commandLine = listOf("chmod")
        args("-R", "+x", ".git/hooks/")
        dependsOn("copyGitHooks")
        onlyIf {
            isLinuxOrMacOs()
        }
        doLast {
            logger.info("Git hook installed succesfully.")
        }
    }
}

private fun isLinuxOrMacOs(): Boolean {
    val osName = System.getProperty("os.name").lowercase(Locale.ROOT)
    return osName.contains("linux") || osName.contains("mac os") || osName.contains("macos")
}