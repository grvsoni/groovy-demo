#!/usr/bin/env groovy

/**
 * Simple Groovy script to perform basic math operations on numbers 2 and 3
 * Can be executed independently with: groovy MathOperations.groovy
 */

// Function to add two string
def concat(firstname, lastname) {
    return firstname + lastname
}

// Main execution
def call() {
    def firstname = config.firstname
    def lastname = config.lastname
    
    println "=== Concat Operations on ${firstname} and ${lastname} ==="
    println ""
    
    // Perform all operations
    def result = concat(firstname, lastname)
    
    println "${result}"
}

// Execute main function
call()
