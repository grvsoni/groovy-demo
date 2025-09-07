# Jenkins Shared Library for Demo Script

This directory simulates a Jenkins Shared Library structure for the YAML configuration demo.

## Structure

```
jenkins-shared-library/
├── vars/
│   └── demoScript.groovy     # Pipeline step definition
├── resources/
│   └── jctr/
│       └── global.yml        # Global configuration (loaded via libraryResource)
└── README.md                 # This file
```

## Usage in Jenkins

### 1. Configure Shared Library in Jenkins
- Go to "Manage Jenkins" → "Configure System" → "Global Pipeline Libraries"
- Add library with name: `demo-shared-library`
- Set Git repository URL pointing to this directory
- Set default version: `main`

### 2. Use in Jenkinsfile
```groovy
@Library('demo-shared-library') _

pipeline {
    agent any
    stages {
        stage('Demo') {
            steps {
                demoScript team: 'frontend', suite: 'ui-tests', test: 'smoke-test'
            }
        }
    }
}
```

### 3. Global Configuration Loading
The `resources/jctr/global.yml` file is loaded using:
```groovy
def globalDataFile = libraryResource 'jctr/global.yml'
def globalJson = readYaml text: globalDataFile
def globalInfo = [:] << globalJson.GLOBAL_SETTINGS
```

This matches the pattern used in the original `runTest.groovy` script.
