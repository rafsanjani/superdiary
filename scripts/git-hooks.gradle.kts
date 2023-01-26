import java.util.*
// https://blog.sebastiano.dev/ooga-chaka-git-hooks-to-enforce-code-quality/

fun isLinuxOrMacOs(): Boolean {
    val osName = System.getProperty("os.name").toLowerCase(Locale.ROOT)
    return osName.contains("linux") || osName.contains("mac os") || osName.contains("macos")
}

tasks.register(name = "copyGitHooks", type = Copy::class) {
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

tasks.register(name = "installGitHooks", type = Exec::class) {
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
