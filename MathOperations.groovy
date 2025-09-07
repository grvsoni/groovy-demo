#!/usr/bin/env groovy

/**
 * Simple Groovy script to perform basic math operations on numbers 2 and 3
 * Can be executed independently with: groovy MathOperations.groovy
 */

// Function to add two numbers
def add(a, b) {
    return a + b
}

// Function to subtract two numbers
def subtract(a, b) {
    return a - b
}

// Function to multiply two numbers
def multiply(a, b) {
    return a * b
}

// Function to divide two numbers
def divide(a, b) {
    if (b == 0) {
        return "Error: Division by zero!"
    }
    return a / b
}

// Main execution
def main() {
    def num1 = 2
    def num2 = 3
    
    println "=== Math Operations on ${num1} and ${num2} ==="
    println ""
    
    // Perform all operations
    def addResult = add(num1, num2)
    def subtractResult = subtract(num1, num2)
    def multiplyResult = multiply(num1, num2)
    def divideResult = divide(num1, num2)
    
    // Display results
    println "Addition: ${num1} + ${num2} = ${addResult}"
    println "Subtraction: ${num1} - ${num2} = ${subtractResult}"
    println "Multiplication: ${num1} * ${num2} = ${multiplyResult}"
    println "Division: ${num1} / ${num2} = ${divideResult}"
    
    println ""
    println "=== Additional Operations ==="
    
    // Show reverse operations for completeness
    println "Reverse subtraction: ${num2} - ${num1} = ${subtract(num2, num1)}"
    println "Reverse division: ${num2} / ${num1} = ${divide(num2, num1)}"
}

// Execute main function
main()
