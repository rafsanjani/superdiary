# Warn when there is a big PR
warn("Big PR") if git.lines_of_code > 500
message("Thank you #{github.pr_author}")

lint_dir = "**/reports/lint-results.xml"
Dir[lint_dir].each do |file_name|
  android_lint.skip_gradle_task = true
  android_lint.filtering = true
  android_lint.report_file = file_name
  android_lint.lint
end

kotlin_detekt.gradle_task = "detektAll"
kotlin_detekt.report_file="build/reports/detekt/detekt.xml"
kotlin_detekt.detekt(inline_mode: true)
