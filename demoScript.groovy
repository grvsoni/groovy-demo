@Grab('org.yaml:snakeyaml:1.33')

import org.yaml.snakeyaml.Yaml
import java.text.SimpleDateFormat

// Global variables
def config = [:]
def replaces = [:]

/**
 * Combines multiple maps with later maps overriding earlier ones
 */
def combineMaps(Map... maps) {
    def result = [:]
    maps.each { map ->
        if (map != null) {
            result.putAll(map)
        }
    }
    return result
}

/**
 * Flattens nested maps for variable replacement
 */
def flattenMap(Map map, String prefix = '') {
    def result = [:]
    map.each { key, value ->
        def fullKey = prefix ? "${prefix}.${key}" : key
        if (value instanceof Map) {
            result.putAll(flattenMap(value, fullKey))
        } else {
            result[fullKey] = value?.toString()
        }
    }
    return result
}

/**
 * Parses variables in text using ${variable} syntax
 */
def parseVars(String text, Map variables) {
    if (!text) return text
    
    def result = text
    variables.each { key, value ->
        result = result.replaceAll(/\$\{${key}\}/, value?.toString() ?: '')
    }
    return result
}

/**
 * Loads YAML configuration from file (for local files)
 */
def loadYamlConfig(String filePath) {
    try {
        def yaml = new Yaml()
        def file = new File(filePath)
        if (file.exists()) {
            return yaml.load(file.text)
        } else {
            println "Warning: Configuration file not found: ${filePath}"
            return [:]
        }
    } catch (Exception e) {
        println "Error loading YAML config from ${filePath}: ${e.message}"
        return [:]
    }
}

/**
 * Loads YAML configuration from Jenkins shared library resource
 */
def loadLibraryResource(String resourcePath) {
    try {
        // In a real Jenkins environment, this would use libraryResource
        // For demo purposes, we'll simulate it by reading from our mock shared library
        def yaml = new Yaml()
        def mockLibraryPath = "jenkins-shared-library/resources/${resourcePath}"
        def file = new File(mockLibraryPath)
        if (file.exists()) {
            println "📚 Loading from shared library resource: ${resourcePath}"
            return yaml.load(file.text)
        } else {
            println "⚠️  Shared library resource not found: ${resourcePath}, falling back to local config"
            // Fallback to local config for demo
            return loadYamlConfig("config/global.yml")
        }
    } catch (Exception e) {
        println "Error loading shared library resource ${resourcePath}: ${e.message}"
        println "Falling back to local config..."
        return loadYamlConfig("config/global.yml")
    }
}

/**
 * Sends notification (simulated)
 */
def sendNotification() {
    if (config.enableNotifications) {
        println "\n=== NOTIFICATION ==="
        println "From: ${config.emailFrom}"
        println "To: ${config.emailTo}"
        println "Subject: ${parseVars(config.emailSubject, replaces)}"
        println "Message: ${parseVars(config.greeting, replaces)} - Build completed successfully!"
        println "===================="
    }
}

/**
 * Executes the main test logic
 */
def executeTest() {
    println "\n=== EXECUTING TEST ==="
    println "Greeting: ${parseVars(config.greeting, replaces)}"
    println "App Name: ${config.appName}"
    println "Environment: ${config.environment}"
    println "Build Tool: ${config.buildTool}"
    println "Test Command: ${config.testCommand}"
    
    if (config.testFiles) {
        println "Test Files: ${config.testFiles}"
    }
    
    if (config.timeout) {
        println "Timeout: ${config.timeout} seconds"
    }
    
    // Simulate test execution
    println "Executing: ${config.testCommand}"
    println "✅ Tests completed successfully!"
    println "======================"
}

/**
 * Generates output in specified format
 */
def generateOutput() {
    def timestamp = config.includeTimestamp ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) : null
    
    println "\n=== BUILD OUTPUT ==="
    if (config.outputFormat == "json") {
        def output = [
            status: "SUCCESS",
            appName: config.appName,
            environment: config.environment,
            greeting: parseVars(config.greeting, replaces)
        ]
        if (timestamp) {
            output.timestamp = timestamp
        }
        def jsonBuilder = new groovy.json.JsonBuilder(output)
        println jsonBuilder.toPrettyString()
    } else {
        println "Status: SUCCESS"
        println "App: ${config.appName}"
        println "Environment: ${config.environment}"
        println "Greeting: ${parseVars(config.greeting, replaces)}"
        if (timestamp) {
            println "Timestamp: ${timestamp}"
        }
    }
    println "===================="
}

/**
 * Main execution function
 */
def execute(String team, String suite, String test, Map customParams = [:]) {
    println "=".repeat(60)
    println "DEMO GROOVY SCRIPT WITH YAML CONFIGURATION"
    println "=".repeat(60)
    
    // 1. Load global configuration from Jenkins shared library (like runTest.groovy)
    println "\n1. Loading global configuration from shared library..."
    def globalConfig = loadLibraryResource('jctr/global.yml')
    def globalInfo = globalConfig.GLOBAL_SETTINGS ?: [:]
    println "✅ Global config loaded from shared library"
    
    // 2. Load team configuration
    println "\n2. Loading team configuration for: ${team}"
    def teamConfig = loadYamlConfig("teams/${team}/markup.yml")
    def teamInfo = teamConfig.common ?: [:]
    teamInfo.remove("suites")
    println "✅ Team config loaded"
    
    // 3. Load suite configuration
    println "\n3. Loading suite configuration for: ${suite}"
    def suiteInfo = [:]
    if (teamConfig.common?.suites?."${suite}") {
        suiteInfo = teamConfig.common.suites."${suite}"
        suiteInfo.remove("tests")
        println "✅ Suite config loaded"
    } else {
        println "⚠️  Suite '${suite}' not found, using defaults"
    }
    
    // 4. Load test configuration
    println "\n4. Loading test configuration for: ${test}"
    def testInfo = [:]
    if (teamConfig.common?.suites?."${suite}"?.tests?."${test}") {
        testInfo = teamConfig.common.suites."${suite}".tests."${test}"
        println "✅ Test config loaded"
    } else {
        println "⚠️  Test '${test}' not found, using defaults"
    }
    
    // 5. Combine all configurations (hierarchy: global < team < suite < test < custom)
    println "\n5. Merging configurations..."
    config = combineMaps(globalInfo, teamInfo, suiteInfo, testInfo, customParams)
    println "✅ Configuration merged"
    
    // 6. Setup variable replacements
    println "\n6. Setting up variable replacements..."
    replaces = flattenMap(config)
    replaces["timestamp"] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
    replaces["team"] = team
    replaces["suite"] = suite
    replaces["test"] = test
    println "✅ Variables prepared"
    
    // 7. Display final configuration
    println "\n7. Final Configuration:"
    println "   Team: ${team}"
    println "   Suite: ${suite}"
    println "   Test: ${test}"
    println "   App Name: ${config.appName}"
    println "   Environment: ${config.environment}"
    println "   Greeting: ${config.greeting}"
    println "   Log Level: ${config.logLevel}"
    
    // 8. Execute the test
    executeTest()
    
    // 9. Generate output
    generateOutput()
    
    // 10. Send notification
    sendNotification()
    
    println "\n" + "=".repeat(60)
    println "DEMO COMPLETED SUCCESSFULLY!"
    println "=".repeat(60)
}

// Example usage - can be called with different parameters
if (args.length >= 3) {
    // Command line usage: groovy demoScript.groovy <team> <suite> <test>
    def team = args[0]
    def suite = args[1] 
    def test = args[2]
    def customParams = [:]
    
    // Parse additional custom parameters
    if (args.length > 3) {
        for (int i = 3; i < args.length; i++) {
            def param = args[i]
            if (param.contains("=")) {
                def parts = param.split("=", 2)
                customParams[parts[0]] = parts[1]
            }
        }
    }
    
    execute(team, suite, test, customParams)
} else {
    // Default demo execution
    println "Running default demo scenarios...\n"
    
    // Scenario 1: Frontend smoke test
    println "SCENARIO 1: Frontend Smoke Test"
    execute("frontend", "ui-tests", "smoke-test")
    
    println "\n" + "-".repeat(80) + "\n"
    
    // Scenario 2: Backend unit test with custom parameters
    println "SCENARIO 2: Backend Unit Test with Custom Parameters"
    execute("backend", "unit-tests", "service-test", [
        greeting: "Custom Hello from Jenkins Job!",
        environment: "custom-env",
        logLevel: "DEBUG"
    ])
    
    println "\n" + "-".repeat(80) + "\n"
    
    // Scenario 3: Frontend regression test
    println "SCENARIO 3: Frontend Regression Test"
    execute("frontend", "ui-tests", "regression-test", [
        emailTo: "custom-team@company.com"
    ])
}
