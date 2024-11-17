lint_dir = "**/reports/lint-results.xml"
Dir[lint_dir].each do |file_name|
  android_lint.skip_gradle_task = true
  android_lint.filtering = true
  android_lint.report_file = file_name
  android_lint.lint
end

ktlint.lint(inline_mode: true)
