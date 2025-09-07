@Grab('org.yaml:snakeyaml:1.33')

import org.yaml.snakeyaml.Yaml
import groovy.json.JsonBuilder
import java.text.SimpleDateFormat

/**
 * Harness-Compatible Demo Script with File Store Access
 * 
 * This version accesses configuration files from Harness File Store
 * instead of local filesystem
 */

// Define echo function to maintain Jenkins compatibility
def echo(message) {
    println message
}

// Configuration - modify these values for your use case
def team = "frontend"
def suite = "ui-tests" 
def test = "smoke-test"
def customParams = [:]

// You can also use Harness variables like:
// def team = '<+pipeline.variables.team>'
// def suite = '<+pipeline.variables.suite>'
// def test = '<+pipeline.variables.test>'

echo "=" * 60
echo "🚀 HARNESS DEMO SCRIPT EXECUTION (WITH FILE STORE)"
echo "=" * 60
echo "Team: ${team} | Suite: ${suite} | Test: ${test}"

try {
    // 1. Load global configuration from Harness File Store
    echo "\n📚 Step 1: Loading global configuration from Harness File Store..."
    def globalInfo = [:]
    try {
        // Option 1: Using Harness expression to get file content
        // The file content will be resolved by Harness before script execution
        def globalConfigText = '<+fileStore.getAsString("config/global.yml")>'
        
        // Check if Harness resolved the expression (if not, it will still contain '<+')
        if (globalConfigText.startsWith('<+')) {
            echo "⚠️ Harness File Store expression not resolved, trying alternative methods..."
            throw new Exception("File Store expression not resolved")
        }
        
        def yaml = new Yaml()
        def globalConfig = yaml.load(globalConfigText)
        globalInfo = globalConfig.GLOBAL_SETTINGS ?: [:]
        echo "✅ Global configuration loaded from Harness File Store"
        
    } catch (Exception e) {
        echo "⚠️ Warning: Could not load global config from File Store: ${e.message}"
        echo "📝 Using default global configuration..."
        globalInfo = [
            greeting: "Hello from Harness",
            environment: "development", 
            logLevel: "INFO",
            timeout: 30,
            enableNotifications: true,
            emailFrom: "harness@company.com",
            emailSubject: "Harness Build Notification: \${appName}",
            outputFormat: "json",
            includeTimestamp: true,
            buildTool: "maven",
            testCommand: "mvn test"
        ]
    }
    
    // 2. Load team configuration from Harness File Store
    echo "\n👥 Step 2: Loading team configuration for: ${team}"
    def teamConfig = [:]
    try {
        // Access team config from File Store
        def teamConfigText = "<+fileStore.getAsString(\"teams/${team}/markup.yml\")>"
        
        if (teamConfigText.startsWith('<+')) {
            echo "⚠️ Team config File Store expression not resolved"
            throw new Exception("Team File Store expression not resolved")
        }
        
        def yaml = new Yaml()
        teamConfig = yaml.load(teamConfigText)
        echo "✅ Team configuration loaded from Harness File Store for ${team}"
        
    } catch (Exception e) {
        echo "⚠️ Warning: Could not load team config for ${team}: ${e.message}"
        teamConfig = [common: [:]]
    }
    
    // 3. Extract configuration hierarchy
    echo "\n⚙️ Step 3: Building configuration hierarchy..."
    def teamInfo = teamConfig.common ? new HashMap(teamConfig.common) : [:]
    teamInfo.remove("suites")
    
    def suiteInfo = [:]
    if (teamConfig.common?.suites?."${suite}") {
        suiteInfo = new HashMap(teamConfig.common.suites."${suite}")
        suiteInfo.remove("tests")
        echo "✅ Suite configuration found for ${suite}"
    } else {
        echo "⚠️ Suite '${suite}' not found, using defaults"
    }
    
    def testInfo = [:]
    if (teamConfig.common?.suites?."${suite}"?.tests?."${test}") {
        testInfo = teamConfig.common.suites."${suite}".tests."${test}"
        echo "✅ Test configuration found for ${test}"
    } else {
        echo "⚠️ Test '${test}' not found, using defaults"
    }
    
    // 4. Combine configurations (hierarchy: global < team < suite < test < custom)
    echo "\n🔄 Step 4: Merging configuration hierarchy..."
    def config = [:]
    config.putAll(globalInfo)
    config.putAll(teamInfo)
    config.putAll(suiteInfo)
    config.putAll(testInfo)
    config.putAll(customParams)
    
    echo "✅ Configuration hierarchy merged successfully"
    
    // 5. Display final configuration
    echo "\n📋 Step 5: Final Configuration Summary:"
    echo "  • App Name: ${config.appName ?: 'N/A'}"
    echo "  • Environment: ${config.environment ?: 'N/A'}"
    echo "  • Greetingsssssss: ${config.greeting ?: 'N/A'}"
    echo "  • Build Tool: ${config.buildTool ?: 'N/A'}"
    echo "  • Test Command: ${config.testCommand ?: 'N/A'}"
    echo "  • Timeout: ${config.timeout ?: 'N/A'} seconds"
    echo "  • Log Level: ${config.logLevel ?: 'N/A'}"
    
    // 6. Execute the test (simulated)
    echo "\n🧪 Step 6: Executing Test..."
    echo "Greetingsssssss: ${config.greeting}"
    echo "Running: ${config.testCommand ?: 'default test command'}"
    echo "Environment: ${config.environment}"
    
    // Simulate some test execution time
    Thread.sleep(1000)
    
    echo "✅ Test execution completed successfully!"
    
    // 7. Generate output
    echo "\n📊 Step 7: Generating Output..."
    def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    def output = [
        status: "SUCCESS",
        team: team,
        suite: suite,
        test: test,
        appName: config.appName,
        environment: config.environment,
        greeting: config.greeting,
        timestamp: dateFormat.format(new Date())
    ]
    
    if (config.outputFormat == "json") {
        echo "JSON Output:"
        def jsonBuilder = new JsonBuilder(output)
        echo jsonBuilder.toPrettyString()
    }
    
    // 8. Send notifications (simulated)
    if (config.enableNotifications) {
        echo "\n📧 Step 8: Sending Notifications..."
        echo "From: ${config.emailFrom}"
        echo "To: ${config.emailTo ?: 'default@company.com'}"
        def subject = config.emailSubject?.replace('${appName}', config.appName ?: 'Demo App')
        echo "Subject: ${subject}"
        echo "Message: ${config.greeting} - Build completed successfully!"
        echo "✅ Notification sent!"
    }
    
    echo "\n" + "=" * 60
    echo "🎉 DEMO SCRIPT EXECUTION COMPLETED SUCCESSFULLY!"
    echo "=" * 60
    
    // Set environment variables for Harness (optional)
    System.setProperty("DEMO_SCRIPT_STATUS", "SUCCESS")
    System.setProperty("DEMO_SCRIPT_TEAM", team)
    System.setProperty("DEMO_SCRIPT_ENVIRONMENT", config.environment ?: "unknown")
    
} catch (Exception e) {
    echo "\n❌ Error during demo script execution: ${e.message}"
    echo "=" * 60
    System.setProperty("DEMO_SCRIPT_STATUS", "FAILED")
    throw new Exception("Demo script execution failed: ${e.message}")
}
