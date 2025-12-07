require 'json'

COMPOSE_RULES_VERSION = '0.4.26'
KTLINT_CLI_VERSION = '1.7.1'

module Danger
  class Ktlint < Plugin
    class UnexpectedLimitTypeError < StandardError; end

    class UnsupportedServiceError < StandardError
      def initialize(message = 'Unsupported service! Currently supported services are GitHub, GitLab and BitBucket server.')
        super(message)
      end
    end

    AVAILABLE_SERVICES = [:github, :gitlab, :bitbucket_server]

    attr_accessor :filtering, :skip_lint, :report_file, :report_files_pattern
    attr_reader :limit

    def limit=(value)
      unless value.nil? || value.is_a?(Integer)
        raise UnexpectedLimitTypeError, "Limit must be an integer"
      end
      @limit = value
    end

    # Main lint method
    def lint(inline_mode: false)
      raise UnsupportedServiceError unless supported_service?

      targets = target_files(changed_files)
      return if targets.empty?

      results = ktlint_results(targets)
      return if results.nil? || results.empty?

      if inline_mode
        send_inline_comments(results, targets)
      else
        send_markdown_comments(results, targets)
      end
    end

    private

    # --- Git / File helpers ---

    def changed_files
      git.added_files + git.modified_files
    end

    def target_files(files)
      files.select { |f| f.end_with?('.kt') }
    end

    def relative_file_path(file)
      file.gsub(/^#{pwd}\//, '')
    end

    def file_html_link(file_path, line_number)
      if danger.scm_provider == :github
        "#{file_path}#L#{line_number}"
      else
        file_path
      end.tap { |file| scm_provider_klass.html_link(file) }
    end

    def pwd
      @pwd ||= `pwd`.chomp
    end

    # --- Comments / Reporting ---

    def send_markdown_comments(results, targets)
      process_results(results, targets) do |file_path, error|
        fail("#{file_html_link(file_path, error['line'])}: #{error['message']}")
      end
    end

    def send_inline_comments(results, targets)
      process_results(results, targets) do |file_path, error|
        fail(error['message'], file: file_path, line: error['line'])
      end
    end

    def process_results(results, targets)
      count = 0
      catch(:limit_reached) do
        results.each do |result_batch|
          result_batch.each do |result|
            next unless targets.include?(relative_file_path(result['file']))

            result['errors'].each do |error|
              yield result['file'], error
              next if limit.nil?

              count += 1
              throw(:limit_reached) if count >= limit
            end
          end
        end
      end
    end

    # --- Ktlint / Dependencies ---

    def ktlint_results(targets)
      return load_existing_results if skip_lint

      ensure_dependencies_present

      return if targets.empty?

      command = "./ktlint --ruleset=compose-rules.jar #{targets.join(' ')} --reporter=json --relative --log-level=none"
      [JSON.parse(`#{command}`)]
    end

    def load_existing_results
      files = ktlint_result_files
      files.map { |file| JSON.parse(File.read(file)) }
    end

    def ktlint_result_files
      if report_file && File.exist?(report_file)
        [report_file]
      elsif report_files_pattern
        Dir.glob(report_files_pattern)
      else
        fail("Couldn't find ktlint result JSON file. Set `ktlint.report_file` or `ktlint.report_files_pattern` in your Dangerfile.")
      end
    end

    def ensure_dependencies_present
      download_ktlint unless ktlint_exists?
      download_compose_rules unless File.exist?('compose-rules.jar')
    end

    def ktlint_exists?
      system('which ktlint > /dev/null 2>&1')
    end

    def download_ktlint
      puts "Downloading ktlint CLI..."
      `curl -sSLO https://github.com/pinterest/ktlint/releases/download/#{KTLINT_CLI_VERSION}/ktlint && chmod +x ktlint`
    end

    def download_compose_rules
      puts "Downloading Compose rules..."
      `curl -L https://github.com/mrmans0n/compose-rules/releases/download/v#{COMPOSE_RULES_VERSION}/ktlint-compose-#{COMPOSE_RULES_VERSION}-all.jar -o compose-rules.jar`
    end

    # --- SCM helpers ---

    def scm_provider_klass
      @scm_provider_klass ||= eval(danger.scm_provider.to_s)
    end

    def supported_service?
      AVAILABLE_SERVICES.include?(danger.scm_provider.to_sym)
    end
  end
end
