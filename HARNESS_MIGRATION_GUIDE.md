# Jenkins to Harness Migration Guide

## Overview
This guide explains how to migrate your Jenkins shared library script to work in Harness Run Step with minimal changes.

## Key Changes Made

### 1. Jenkins-Specific Function Replacements

| Jenkins Function | Harness Alternative | Purpose |
|-----------------|-------------------|---------|
| `libraryResource('path')` | `new File('path').text` | Read config files from workspace |
| `readYaml text: content` | `new Yaml().load(content)` | Parse YAML content |
| `echo "message"` | `println "message"` | Output messages |
| `error("message")` | `throw new Exception("message")` | Handle errors |
| `writeJSON returnText: true, json: data` | `new JsonBuilder(data).toPrettyString()` | JSON output |

### 2. Dependencies Added
```groovy
@Grab('org.yaml:snakeyaml:1.33')
import org.yaml.snakeyaml.Yaml
```

### 3. File Structure Changes

**Jenkins Structure:**
```
jenkins-shared-library/
├── vars/
│   └── demoScript.groovy
└── resources/
    └── jctr/
        └── global.yml
```

**Harness Structure:**
```
your-repo/
├── harness-migrated-script.groovy
├── config/
│   └── global.yml
└── teams/
    └── [team-name]/
        └── markup.yml
```

## How to Use in Harness

### Option 1: Direct Script Execution
In your Harness Run Step:
```groovy
// Load the script
evaluate(new File('harness-migrated-script.groovy'))

// Execute with parameters
def result = runDemoScript([
    team: 'frontend',
    suite: 'ui-tests', 
    test: 'smoke-test',
    customParams: [greeting: 'Hello from Harness!']
])
```

### Option 2: Inline in Run Step
Copy the entire `harness-migrated-script.groovy` content directly into your Harness Run Step.

### Option 3: Using Harness Variables
Replace hardcoded config with Harness variables:
```groovy
def team = '<+pipeline.variables.team>'
def suite = '<+pipeline.variables.suite>'
def test = '<+pipeline.variables.test>'

def result = runDemoScript([
    team: team,
    suite: suite,
    test: test
])
```

## Configuration Files Setup

### 1. Create Global Config
Create `config/global.yml` in your repository:
```yaml
GLOBAL_SETTINGS:
  greeting: "Hello from Harness"
  environment: "development"
  logLevel: "INFO"
  timeout: 30
  enableNotifications: true
  emailFrom: "harness@company.com"
  emailSubject: "Harness Build Notification: ${appName}"
  outputFormat: "json"
  includeTimestamp: true
```

### 2. Create Team Configs
Create team-specific configs like `teams/frontend/markup.yml`:
```yaml
common:
  appName: "Frontend App"
  buildTool: "npm"
  environment: "staging"
  suites:
    ui-tests:
      testCommand: "npm run test:ui"
      timeout: 60
      tests:
        smoke-test:
          testCommand: "npm run test:smoke"
          environment: "production"
```

## Migration Benefits

1. **Minimal Code Changes**: Core logic remains the same
2. **Same Configuration System**: Hierarchical YAML configs preserved
3. **Harness Native**: Uses standard Groovy without Jenkins dependencies
4. **Flexible Deployment**: Can be used inline or as separate script file

## Testing the Migration

1. Create the config files in your repository
2. Add the migrated script to your Harness pipeline
3. Test with sample parameters:
   ```groovy
   runDemoScript([
       team: 'frontend',
       suite: 'ui-tests',
       test: 'smoke-test'
   ])
   ```

## Troubleshooting

### Common Issues:
1. **YAML parsing errors**: Ensure SnakeYAML dependency is available
2. **File not found**: Check config file paths in your repository
3. **Permission errors**: Ensure Harness has read access to config files

### Solutions:
- Use try-catch blocks with fallback configurations
- Verify file paths relative to your repository root
- Use Harness file store for configuration management
