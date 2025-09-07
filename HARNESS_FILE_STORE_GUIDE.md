# Harness File Store Access Guide

## Overview
When you store files in Harness File Store, you need to use Harness-specific expressions to access them in your Groovy scripts.

## Methods to Access Harness File Store

### Method 1: Using Harness Expression (Recommended)
```groovy
// Access file content directly using Harness expression
def globalConfigText = '<+fileStore.getAsString("config/global.yml")>'

// Parse the YAML content
def yaml = new Yaml()
def globalConfig = yaml.load(globalConfigText)
```

### Method 2: Using File Store with Base64 Encoding
```groovy
// For binary files or when you need base64 encoding
def fileContent = '<+fileStore.getAsBase64("config/global.yml")>'
def decodedContent = new String(Base64.getDecoder().decode(fileContent))
```

### Method 3: Using Pipeline Variables
```groovy
// Set up a pipeline variable that references the file store
// In Harness Pipeline: Create variable 'globalConfig' with value '<+fileStore.getAsString("config/global.yml")>'
def globalConfigText = '<+pipeline.variables.globalConfig>'
```

### Method 4: Using Environment Variables
```groovy
// Access via environment variable if set in Harness
def globalConfigText = System.getenv('HARNESS_GLOBAL_CONFIG')
```

## File Store Path Structure

When you upload files to Harness File Store, use the correct path structure:

```
Harness File Store Structure:
├── config/
│   └── global.yml
├── teams/
│   ├── frontend/
│   │   └── markup.yml
│   └── backend/
│       └── markup.yml
└── templates/
    └── notification.txt
```

## Updated Script Example

Here's how to modify your script to use Harness File Store:

```groovy
// Original (local file system)
def globalConfigFile = new File("config/global.yml")
if (globalConfigFile.exists()) {
    def yaml = new Yaml()
    def globalConfig = yaml.load(globalConfigFile.text)
    globalInfo = globalConfig.GLOBAL_SETTINGS ?: [:]
}

// Updated (Harness File Store)
try {
    def globalConfigText = '<+fileStore.getAsString("config/global.yml")>'
    
    // Check if Harness resolved the expression
    if (!globalConfigText.startsWith('<+')) {
        def yaml = new Yaml()
        def globalConfig = yaml.load(globalConfigText)
        globalInfo = globalConfig.GLOBAL_SETTINGS ?: [:]
        echo "✅ Global configuration loaded from Harness File Store"
    } else {
        throw new Exception("File Store expression not resolved")
    }
} catch (Exception e) {
    echo "⚠️ Warning: Could not load global config: ${e.message}"
    // Use fallback configuration
}
```

## Dynamic File Paths

For dynamic paths (like team-specific configs):

```groovy
def team = "frontend"
def teamConfigText = "<+fileStore.getAsString(\"teams/${team}/markup.yml\")>"
```

## Best Practices

### 1. Always Use Try-Catch
```groovy
try {
    def configText = '<+fileStore.getAsString("config/global.yml")>'
    // Process config
} catch (Exception e) {
    echo "⚠️ Fallback to default config: ${e.message}"
    // Use default configuration
}
```

### 2. Validate Expression Resolution
```groovy
def configText = '<+fileStore.getAsString("config/global.yml")>'
if (configText.startsWith('<+')) {
    echo "⚠️ Harness expression not resolved"
    // Handle unresolved expression
}
```

### 3. Use Descriptive File Paths
```groovy
// Good
'<+fileStore.getAsString("configs/teams/frontend/environment-staging.yml")>'

// Avoid
'<+fileStore.getAsString("config.yml")>'
```

## Troubleshooting

### Common Issues:

1. **Expression Not Resolved**: `<+fileStore.getAsString(...)>` appears as literal text
   - **Solution**: Check file path, ensure file exists in File Store
   - **Solution**: Verify Harness has permission to access the file

2. **File Not Found**: Error accessing file from File Store
   - **Solution**: Verify the exact file path in Harness File Store
   - **Solution**: Check file upload was successful

3. **YAML Parsing Error**: Invalid YAML content
   - **Solution**: Validate YAML syntax in your uploaded file
   - **Solution**: Check for special characters or encoding issues

### Debug Tips:

```groovy
// Debug: Print the raw content to see what Harness resolved
def configText = '<+fileStore.getAsString("config/global.yml")>'
echo "Raw config content: ${configText}"

// Debug: Check if expression was resolved
if (configText.startsWith('<+')) {
    echo "❌ Expression NOT resolved by Harness"
} else {
    echo "✅ Expression resolved by Harness"
}
```

## Migration Steps

1. **Upload your config files to Harness File Store**
   - Go to Project Settings → File Store
   - Upload `global.yml` to path `config/global.yml`
   - Upload team configs to `teams/{team-name}/markup.yml`

2. **Update your script to use File Store expressions**
   - Replace `new File("path")` with `'<+fileStore.getAsString("path")>'`
   - Add proper error handling and fallbacks

3. **Test the updated script**
   - Run in Harness pipeline to verify file access works
   - Check logs to ensure expressions are resolved correctly
