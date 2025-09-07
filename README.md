# Groovy Demo with YAML Configuration System

This repository demonstrates a sophisticated YAML-based configuration system for Groovy scripts, similar to enterprise Jenkins pipelines.

## Quick Start

```bash
# Run the demo with default scenarios
groovy demoScript.groovy

# Run specific team/suite/test
groovy demoScript.groovy frontend ui-tests smoke-test

# Run with custom parameters
groovy demoScript.groovy backend unit-tests service-test greeting="Custom Hello!" environment=production
```

## Files

- `demoScript.groovy` - Main executable demo script

- `jenkins-shared-library/resources/jctr/global.yml` - Global configuration from shared library
- `teams/*/markup.yml` - Team-specific configurations
- `README_DEMO.md` - Detailed documentation

## Features

✅ Hierarchical YAML configuration inheritance  
✅ Variable substitution with `${variable}` syntax  
✅ Configuration merging and flattening  
✅ Multiple output formats (JSON, plain text)  
✅ Simulated notifications with templating  
✅ Command-line parameter support  

See `README_DEMO.md` for complete usage instructions and examples.