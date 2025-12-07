require 'fileutils'
require 'open3'
require 'uri'

GIT_USER_NAME = "Paparazzi Bot"
GIT_USER_EMAIL = "actions@github.com"
REPO_RAW_URL = "https://github.com/rafsanjani/superdiary/raw"
PULL_REQUEST_NUMBER = ENV['PULL_REQUEST_NUMBER']

module Danger
  class Paparazzi < Plugin

    def verify
      branch_name = "paparazzi-snapshots-#{PULL_REQUEST_NUMBER}"
      output_file = "snapshots.md"

      unless verify_paparazzi
        setup_git
        save_working_directory
        create_orphan_branch(branch_name)
        delta_files = copy_delta_files
        commit_and_push(branch_name, delta_files)
        generate_markdown(delta_files, branch_name, output_file)

        markdown_contents = File.read(output_file)
        restore_stash
        fail markdown_contents
      end
    end

    # Save the working changes, very helpful when running locally to prevent data loss
    def save_working_directory
      system("git stash")
    end

    def restore_stash
      system("git stash pop")
    end

    def verify_paparazzi
      system("./gradlew verifyPaparazziDebug")
    end

    def setup_git
      puts "Setting up commit username and email"
      system("git config --global user.name '#{GIT_USER_NAME}'")
      system("git config --global user.email '#{GIT_USER_EMAIL}'")
    end

    def create_orphan_branch(branch_name)
      puts "Creating an orphan branch with the delta images"
      system("git checkout --orphan #{branch_name}")
      system("git reset")
    end

    def copy_delta_files
      puts "Copy delta files from /build/paparazzi/failures to working directory"
      delta_files = Dir.glob("**/build/paparazzi/failures/delta-*")
      delta_files.each do |file|
        FileUtils.cp_r(file, ".")
      end
      delta_files
    end

    def commit_and_push(branch_name, delta_files)
      return if delta_files.empty?

      puts "Commit and push the delta images to the orphan branch"
      system("git add delta-*")
      system("git commit -m 'Upload paparazzi failures'")
      system("git push --force origin #{branch_name}")
    end

    def generate_markdown(delta_files, branch_name, output_file)
      puts "Generating markdown"
      File.open(output_file, 'w') do |f|
        delta_files.each do |file|
          filename = File.basename(file)
          encoded_filename = URI.encode_www_form_component(filename)
          f.puts "#### #{filename}"
          f.puts "<img alt=\"paparazzi failure\" src=\"#{REPO_RAW_URL}/#{branch_name}/#{encoded_filename}\"/>\n\n"
        end
      end
    end
  end
end
