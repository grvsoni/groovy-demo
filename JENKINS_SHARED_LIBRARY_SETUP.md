# Jenkins Shared Library Setup Guide

This guide shows you how to set up and use the **Jenkins Shared Library** approach for your YAML configuration system.

## 🎯 Overview

The Jenkins Shared Library approach provides:
- ✅ **Professional enterprise solution**
- ✅ **Centralized configuration management**
- ✅ **Reusable across multiple pipelines**
- ✅ **Version controlled and maintainable**
- ✅ **Full YAML configuration hierarchy support**

## 📋 Prerequisites

- Jenkins instance with admin access
- Your GitHub repository: `https://github.com/grvsoni/groovy-demo.git`
- Jenkins plugins: `Pipeline: Groovy`, `Git`, `Pipeline: Stage View`

## 🔧 Step 1: Configure Jenkins Shared Library

### 1.1 Access Jenkins Configuration
1. Go to **"Manage Jenkins"** → **"Configure System"**
2. Scroll down to **"Global Pipeline Libraries"**

### 1.2 Add Your Shared Library
Click **"Add"** and configure:

| Field | Value |
|-------|-------|
| **Name** | `groovy-demo-shared-library` |
| **Default version** | `main` |
| **Load implicitly** | ☐ (unchecked) |
| **Allow default version to be overridden** | ☑ (checked) |
| **Include @Library changes in job recent changes** | ☑ (checked) |

### 1.3 Configure Source Code Management
Select **"Modern SCM"** → **"Git"**:

| Field | Value |
|-------|-------|
| **Project Repository** | `https://github.com/grvsoni/groovy-demo.git` |
| **Credentials** | (Select appropriate credentials or leave blank for public repo) |

### 1.4 Save Configuration
Click **"Save"** to apply the changes.

## 🚀 Step 2: Create Your Pipeline Job

### 2.1 Create New Pipeline Job
1. Click **"New Item"**
2. Enter name: `demo-script-pipeline`
3. Select **"Pipeline"**
4. Click **"OK"**

### 2.2 Configure Pipeline Parameters
In the job configuration, check **"This project is parameterized"** and add:

#### Choice Parameter: TEAM
- **Name**: `TEAM`
- **Choices**: 
  ```
  frontend
  backend
  ```
- **Description**: `Select team`

#### Choice Parameter: SUITE
- **Name**: `SUITE`
- **Choices**:
  ```
  ui-tests
  api-tests
  unit-tests
  integration-tests
  ```
- **Description**: `Select test suite`

#### Choice Parameter: TEST
- **Name**: `TEST`
- **Choices**:
  ```
  smoke-test
  regression-test
  service-test
  controller-test
  database-test
  integration-test
  ```
- **Description**: `Select test`

#### String Parameter: CUSTOM_GREETING
- **Name**: `CUSTOM_GREETING`
- **Default Value**: (leave empty)
- **Description**: `Custom greeting (optional)`

#### String Parameter: CUSTOM_ENVIRONMENT
- **Name**: `CUSTOM_ENVIRONMENT`
- **Default Value**: (leave empty)
- **Description**: `Custom environment (optional)`

### 2.3 Configure Pipeline Script
In the **Pipeline** section, select **"Pipeline script"** and paste:

```groovy
@Library('groovy-demo-shared-library@main') _

pipeline {
    agent any
    
    stages {
        stage('Execute Demo Script') {
            steps {
                script {
                    // Build custom parameters map
                    def customParams = [:]
                    if (params.CUSTOM_GREETING) {
                        customParams.greeting = params.CUSTOM_GREETING
                    }
                    if (params.CUSTOM_ENVIRONMENT) {
                        customParams.environment = params.CUSTOM_ENVIRONMENT
                    }
                    
                    // Call the shared library step with error handling
                    try {
                        def result = demoScript(
                            team: params.TEAM ?: 'frontend',
                            suite: params.SUITE ?: 'ui-tests',
                            test: params.TEST ?: 'smoke-test',
                            customParams: customParams
                        )
                        
                        if (result && result.status) {
                            echo "✅ Demo script completed with status: ${result.status}"
                        } else {
                            echo "✅ Demo script completed successfully"
                        }
                    } catch (Exception e) {
                        echo "❌ Demo script execution failed: ${e.message}"
                        throw e
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo "Demo script execution completed!"
        }
        success {
            echo "🎉 Demo script executed successfully!"
        }
        failure {
            echo "❌ Demo script execution failed!"
        }
    }
}
```

### 2.4 Save Pipeline Configuration
Click **"Save"**

## 🎯 Step 3: Test Your Setup

### 3.1 Run the Pipeline
1. Go to your pipeline job
2. Click **"Build with Parameters"**
3. Select your desired parameters:
   - **TEAM**: `frontend`
   - **SUITE**: `ui-tests`
   - **TEST**: `smoke-test`
   - **CUSTOM_GREETING**: `Hello from Jenkins!`
4. Click **"Build"**

### 3.2 Expected Output
You should see output like:
```
============================================================
🚀 JENKINS SHARED LIBRARY: DEMO SCRIPT EXECUTION
============================================================
Team: frontend | Suite: ui-tests | Test: smoke-test

📚 Step 1: Loading global configuration from shared library...
✅ Global configuration loaded successfully

👥 Step 2: Loading team configuration for: frontend
✅ Team configuration loaded for frontend

⚙️ Step 3: Building configuration hierarchy...
✅ Suite configuration found for ui-tests
✅ Test configuration found for smoke-test

🔄 Step 4: Merging configuration hierarchy...
✅ Configuration hierarchy merged successfully

📋 Step 5: Final Configuration Summary:
  • App Name: WebApp
  • Environment: staging
  • Greeting: Hello from Jenkins!
  • Build Tool: npm
  • Test Command: npm test
  • Timeout: 60 seconds
  • Log Level: INFO

🧪 Step 6: Executing Test...
Greeting: Hello from Jenkins!
Running: npm test
Environment: staging
✅ Test execution completed successfully!

📊 Step 7: Generating Output...
JSON Output: {...}

📧 Step 8: Sending Notifications...
From: jenkins@company.com
To: frontend-team@company.com
Subject: Frontend Build: WebApp - staging
Message: Hello from Jenkins! - Build completed successfully!
✅ Notification sent!

============================================================
🎉 DEMO SCRIPT EXECUTION COMPLETED SUCCESSFULLY!
============================================================
```

## 🔄 Step 4: Using in Other Pipelines

Once configured, you can use the shared library in any pipeline:

```groovy
@Library('groovy-demo-shared-library@main') _

pipeline {
    agent any
    stages {
        stage('My Test') {
            steps {
                script {
                    // Simple usage
                    demoScript team: 'backend', suite: 'unit-tests', test: 'service-test'
                    
                    // With custom parameters
                    demoScript(
                        team: 'frontend',
                        suite: 'ui-tests', 
                        test: 'regression-test',
                        customParams: [
                            greeting: 'Production Build!',
                            environment: 'production',
                            logLevel: 'DEBUG'
                        ]
                    )
                }
            }
        }
    }
}
```

## 🎛️ Advanced Configuration

### Version Management
You can specify different versions of your shared library:

```groovy
@Library('groovy-demo-shared-library@v1.0.0') _  // Specific version
@Library('groovy-demo-shared-library@develop') _ // Different branch
@Library('groovy-demo-shared-library@main') _    // Latest main
```

### Multiple Libraries
You can use multiple shared libraries:

```groovy
@Library(['groovy-demo-shared-library@main', 'other-library@v2.0']) _
```

## 🔍 Troubleshooting

### Common Issues

1. **Library not found**
   - Check the library name matches exactly
   - Verify the GitHub repository URL is correct
   - Ensure the repository is accessible

2. **Jenkins loading old version of shared library (MOST COMMON)**
   - **Symptoms**: Old output format, missing features, `NullPointerException`
   - **Solution**: Clear Jenkins shared library cache:
     1. Go to "Manage Jenkins" → "Script Console"
     2. Run this script:
     ```groovy
     // Clear shared library cache
     System.setProperty("org.jenkinsci.plugins.workflow.libs.LibraryAdder.DISABLE_CACHE", "true")
     Jenkins.instance.reload()
     println "Shared library cache cleared"
     ```
     3. Or restart Jenkins completely
     4. Try your build again

3. **Parameters coming through as null**
   - **Symptoms**: `suite:null` in logs, parameter errors
   - **Solution**: Use the updated Jenkinsfile with default values and error handling
   - **Check**: Ensure all pipeline parameters are correctly configured

4. **Configuration not loading**
   - Check that `jenkins-shared-library/resources/jctr/global.yml` exists
   - Verify YAML syntax is correct
   - Check Jenkins logs for detailed errors

5. **Team configuration not found**
   - Ensure `teams/{team}/markup.yml` files exist in your repository
   - Check that the team name parameter matches the directory name

### Debug Mode
Add this to your pipeline for more detailed logging:

```groovy
pipeline {
    agent any
    options {
        timestamps()
        ansiColor('xterm')
    }
    // ... rest of pipeline
}
```

## 🎉 Benefits of This Approach

1. **Centralized Management**: All configuration logic in one place
2. **Version Control**: Track changes to your configuration system
3. **Reusability**: Use across multiple projects and teams
4. **Consistency**: Same configuration system everywhere
5. **Professional**: Enterprise-grade solution
6. **Maintainability**: Easy to update and extend

Your Jenkins Shared Library is now ready for production use! 🚀
