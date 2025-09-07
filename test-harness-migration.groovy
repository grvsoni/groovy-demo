#!/usr/bin/env groovy

/**
 * Test script to demonstrate the migrated Harness script
 * This shows how to use the migrated script in Harness Run Step
 */

// Load and execute the migrated script
println "🧪 Testing Harness Migration Script..."
println "=" * 50

// Load the migrated script
def scriptBinding = new Binding()
def shell = new GroovyShell(scriptBinding)
def script = shell.parse(new File('harness-migrated-script.groovy'))
script.run()

// Get the function from the script
def runDemoScript = script.runDemoScript

// Test Case 1: Basic execution
println "\n📋 Test Case 1: Basic Frontend UI Test"
try {
    def result1 = runDemoScript([
        team: 'frontend',
        suite: 'ui-tests',
        test: 'smoke-test'
    ])
    println "✅ Test 1 Status: ${result1.status}"
} catch (Exception e) {
    println "❌ Test 1 Failed: ${e.message}"
}

// Test Case 2: With custom parameters
println "\n📋 Test Case 2: With Custom Parameters"
try {
    def result2 = runDemoScript([
        team: 'frontend',
        suite: 'ui-tests',
        test: 'smoke-test',
        customParams: [
            greeting: 'Hello from Custom Harness!',
            environment: 'production',
            timeout: 120
        ]
    ])
    println "✅ Test 2 Status: ${result2.status}"
} catch (Exception e) {
    println "❌ Test 2 Failed: ${e.message}"
}

// Test Case 3: Backend team test
println "\n📋 Test Case 3: Backend Team Test"
try {
    def result3 = runDemoScript([
        team: 'backend',
        suite: 'api-tests',
        test: 'integration-test'
    ])
    println "✅ Test 3 Status: ${result3.status}"
} catch (Exception e) {
    println "❌ Test 3 Failed: ${e.message}"
}

println "\n" + "=" * 50
println "🎉 Migration Test Completed!"
