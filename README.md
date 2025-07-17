# Agentic Azure App Service app with Semantic Kernel (Spring WebFlux)

This is a Spring Boot WebFlux application that demonstrates AI agent integration using Semantic Kernel for intelligent task management. It provides a simple CRUD task list and an interactive chat agent.

## Getting Started

See [Tutorial: Build an agentic web app in Azure App Service with Semantic Kernel (Java)](https://learn.microsoft.com/azure/app-service/tutorial-ai-agent-web-app-semantic-kernel-java).

## Features

- **CRUD Operations**: Create, Read, Update, Delete tasks using REST API.
- **AI Agent**: Semantic Kernel agent with simplified intent-based function calling.
- **Azure Deployment**: Azure Developer CLI (azd) template can create App Service with managed identity using `azd up`.

## Implementation Notes

The Semantic Kernel agent implementation uses the ChatCompletionAgent with automatic function calling:

1. **ChatCompletionAgent**: Uses Microsoft's Semantic Kernel ChatCompletionAgent for intelligent task management.
2. **Automatic Function Calling**: Configured with `FunctionChoiceBehavior.auto(true)` to automatically call appropriate plugin functions.
3. **Direct Plugin Integration**: Uses `@DefineKernelFunction` annotations in `TaskCrudPlugin` for seamless integration.

## Project Structure

```
├── .devcontainer/
│   └── devcontainer.json                          # Codespace definition
├── azure.yaml                                     # Azure Developer CLI configuration
├── pom.xml                                        # Maven project configuration
├── infra/                                         # Azure Developer CLI configuration
│   ├── main.bicep
│   └── main.parameters.json
└── src/
    └── main/
        ├── java/
        │   └── com/example/crudtaskswithagent/
        │       ├── CrudTasksWithAgentApplication.java  # Main application class
        │       ├── controller/
        │       │   ├── TaskController.java             # REST API endpoints
        │       │   └── AgentController.java            # AI agent endpoints
        │       ├── model/
        │       │   └── TaskItem.java                   # Task entity
        │       ├── plugin/
        │       │   └── TaskCrudPlugin.java             # Semantic Kernel plugin
        │       ├── repository/
        │       │   └── TaskRepository.java             # Data repository
        │       └── service/
        │           └── SemanticKernelAgentService.java # SK agent service
        └── resources/
            ├── application.properties                  # Application configuration
            ├── schema.sql                              # Database schema
            └── static/
                └── index.html                          # Frontend UI
```

