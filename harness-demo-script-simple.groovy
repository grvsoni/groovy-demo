@Grab('org.yaml:snakeyaml:1.33')

import org.yaml.snakeyaml.Yaml
import groovy.json.JsonBuilder
import java.text.SimpleDateFormat

/**
 * Simple Harness-Compatible Demo Script
 * 
 * This is a standalone version that can be directly used in Harness Run Step
 * Just copy this entire script into your Harness Run Step and modify the parameters at the bottom
 */

// Configuration - modify these values for your use case
def team = "frontend"
def suite = "ui-tests" 
def test = "smoke-test"
def customParams = [:]

// You can also use Harness variables like:
// def team = '<+pipeline.variables.team>'
// def suite = '<+pipeline.variables.suite>'
// def test = '<+pipeline.variables.test>'

println "=" * 60
println "🚀 HARNESS DEMO SCRIPT EXECUTION"
println "=" * 60
println "Team: ${team} | Suite: ${suite} | Test: ${test}"

try {
    // 1. Load global configuration
    println "\n📚 Step 1: Loading global configuration..."
    def globalInfo = [:]
    try {
        def globalConfigFile = new File("config/global.yml")
        if (globalConfigFile.exists()) {
            def yaml = new Yaml()
            def globalConfig = yaml.load(globalConfigFile.text)
            globalInfo = globalConfig.GLOBAL_SETTINGS ?: [:]
            println "✅ Global configuration loaded from file"
        } else {
            throw new Exception("Global config file not found")
        }
    } catch (Exception e) {
        println "⚠️ Warning: Could not load global config: ${e.message}"
        println "📝 Using default global configuration..."
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
    
    // 2. Load team configuration
    println "\n👥 Step 2: Loading team configuration for: ${team}"
    def teamConfig = [:]
    try {
        def teamConfigFile = new File("teams/${team}/markup.yml")
        if (teamConfigFile.exists()) {
            def yaml = new Yaml()
            teamConfig = yaml.load(teamConfigFile.text)
            println "✅ Team configuration loaded for ${team}"
        } else {
            throw new Exception("Team config file not found")
        }
    } catch (Exception e) {
        println "⚠️ Warning: Could not load team config for ${team}: ${e.message}"
        teamConfig = [common: [:]]
    }
    
    // 3. Extract configuration hierarchy
    println "\n⚙️ Step 3: Building configuration hierarchy..."
    def teamInfo = teamConfig.common ? new HashMap(teamConfig.common) : [:]
    teamInfo.remove("suites")
    
    def suiteInfo = [:]
    if (teamConfig.common?.suites?."${suite}") {
        suiteInfo = new HashMap(teamConfig.common.suites."${suite}")
        suiteInfo.remove("tests")
        println "✅ Suite configuration found for ${suite}"
    } else {
        println "⚠️ Suite '${suite}' not found, using defaults"
    }
    
    def testInfo = [:]
    if (teamConfig.common?.suites?."${suite}"?.tests?."${test}") {
        testInfo = teamConfig.common.suites."${suite}".tests."${test}"
        println "✅ Test configuration found for ${test}"
    } else {
        println "⚠️ Test '${test}' not found, using defaults"
    }
    
    // 4. Combine configurations (hierarchy: global < team < suite < test < custom)
    println "\n🔄 Step 4: Merging configuration hierarchy..."
    def config = [:]
    config.putAll(globalInfo)
    config.putAll(teamInfo)
    config.putAll(suiteInfo)
    config.putAll(testInfo)
    config.putAll(customParams)
    
    println "✅ Configuration hierarchy merged successfully"
    
    // 5. Display final configuration
    println "\n📋 Step 5: Final Configuration Summary:"
    println "  • App Name: ${config.appName ?: 'N/A'}"
    println "  • Environment: ${config.environment ?: 'N/A'}"
    println "  • Greeting: ${config.greeting ?: 'N/A'}"
    println "  • Build Tool: ${config.buildTool ?: 'N/A'}"
    println "  • Test Command: ${config.testCommand ?: 'N/A'}"
    println "  • Timeout: ${config.timeout ?: 'N/A'} seconds"
    println "  • Log Level: ${config.logLevel ?: 'N/A'}"
    
    // 6. Execute the test (simulated)
    println "\n🧪 Step 6: Executing Test..."
    println "Greeting: ${config.greeting}"
    println "Running: ${config.testCommand ?: 'default test command'}"
    println "Environment: ${config.environment}"
    
    // Simulate some test execution time
    Thread.sleep(1000)
    
    println "✅ Test execution completed successfully!"
    
    // 7. Generate output
    println "\n📊 Step 7: Generating Output..."
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
        println "JSON Output:"
        def jsonBuilder = new JsonBuilder(output)
        println jsonBuilder.toPrettyString()
    }
    
    // 8. Send notifications (simulated)
    if (config.enableNotifications) {
        println "\n📧 Step 8: Sending Notifications..."
        println "From: ${config.emailFrom}"
        println "To: ${config.emailTo ?: 'default@company.com'}"
        def subject = config.emailSubject?.replace('${appName}', config.appName ?: 'Demo App')
        println "Subject: ${subject}"
        println "Message: ${config.greeting} - Build completed successfully!"
        println "✅ Notification sent!"
    }
    
    println "\n" + "=" * 60
    println "🎉 DEMO SCRIPT EXECUTION COMPLETED SUCCESSFULLY!"
    println "=" * 60
    
    // Set environment variables for Harness (optional)
    System.setProperty("DEMO_SCRIPT_STATUS", "SUCCESS")
    System.setProperty("DEMO_SCRIPT_TEAM", team)
    System.setProperty("DEMO_SCRIPT_ENVIRONMENT", config.environment ?: "unknown")
    
} catch (Exception e) {
    println "\n❌ Error during demo script execution: ${e.message}"
    println "=" * 60
    System.setProperty("DEMO_SCRIPT_STATUS", "FAILED")
    throw new Exception("Demo script execution failed: ${e.message}")
}
