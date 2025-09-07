# Groovy Demo Script with YAML Configuration System

This demo showcases a sophisticated YAML-based configuration system similar to enterprise Jenkins pipelines, featuring hierarchical configuration inheritance and variable substitution.

## Directory Structure

```
groovy-demo/
├── demoScript.groovy          # Main demo script
├── config/
│   └── global.yml             # Global default settings
├── teams/
│   ├── frontend/
│   │   └── markup.yml         # Frontend team configuration
│   └── backend/
│       └── markup.yml         # Backend team configuration
└── README_DEMO.md             # This file
```

## Configuration Hierarchy

The configuration system follows a hierarchical inheritance model:

1. **Global Config** (`config/global.yml`) - Base defaults for all teams
2. **Team Config** (`teams/{team}/markup.yml`) - Team-specific overrides
3. **Suite Config** (within team markup) - Test suite specific settings
4. **Test Config** (within suite) - Individual test settings
5. **Job Parameters** (runtime) - Highest priority overrides

Later configurations override earlier ones, allowing for flexible customization at each level.

## Usage

### Method 1: Default Demo (Recommended for first run)
```bash
groovy demoScript.groovy
```
This runs three predefined scenarios showing different configuration combinations.

### Method 2: Specific Team/Suite/Test
```bash
groovy demoScript.groovy <team> <suite> <test>
```

**Examples:**
```bash
# Frontend smoke test
groovy demoScript.groovy frontend ui-tests smoke-test

# Backend unit test
groovy demoScript.groovy backend unit-tests service-test

# Frontend regression test
groovy demoScript.groovy frontend ui-tests regression-test
```

### Method 3: With Custom Parameters
```bash
groovy demoScript.groovy <team> <suite> <test> key1=value1 key2=value2
```

**Example:**
```bash
groovy demoScript.groovy frontend ui-tests smoke-test greeting="Custom Hello!" environment=production logLevel=DEBUG
```

## Available Teams and Tests

### Frontend Team
- **ui-tests** suite:
  - `smoke-test` - Basic UI smoke tests
  - `regression-test` - Full UI regression tests
- **api-tests** suite:
  - `integration-test` - API integration tests

### Backend Team
- **unit-tests** suite:
  - `service-test` - Service layer unit tests
  - `controller-test` - Controller unit tests
- **integration-tests** suite:
  - `database-test` - Database integration tests

## Key Features Demonstrated

1. **Hierarchical Configuration**: Shows how settings cascade from global → team → suite → test → custom
2. **Variable Substitution**: Uses `${variable}` syntax for dynamic content
3. **Map Flattening**: Converts nested configurations to flat key-value pairs
4. **YAML Loading**: Demonstrates robust YAML file loading with error handling
5. **Configuration Merging**: Shows how multiple configuration sources combine
6. **Flexible Output**: Supports different output formats (JSON, plain text)
7. **Notification System**: Simulates email notifications with templated content

## Configuration Examples

### Global Settings (config/global.yml)
```yaml
GLOBAL_SETTINGS:
  greeting: "Hello"
  environment: "development"
  logLevel: "INFO"
  timeout: 30
  enableNotifications: true
```

### Team Settings (teams/frontend/markup.yml)
```yaml
common:
  greeting: "Hello Frontend Team"
  appName: "WebApp"
  environment: "staging"
  suites:
    ui-tests:
      greeting: "Hello UI Testers"
      timeout: 60
      tests:
        smoke-test:
          greeting: "Running Smoke Tests"
          testFiles: "tests/smoke/**/*.spec.js"
```

## How Configuration Inheritance Works

For a frontend smoke test, the final configuration would be:

1. Start with global defaults
2. Override with frontend team settings
3. Override with ui-tests suite settings  
4. Override with smoke-test specific settings
5. Override with any custom job parameters

This creates a powerful, flexible system where teams can define their defaults while still allowing per-job customization.

## Sample Output

The script provides detailed output showing:
- Configuration loading steps
- Final merged configuration
- Test execution simulation
- Formatted output (JSON or plain text)
- Notification details

This demonstrates how enterprise Jenkins pipelines can manage complex, multi-team configurations while maintaining flexibility and inheritance.
