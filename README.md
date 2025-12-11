# Agentic Azure App Service app with Semantic Kernel and Foundry Agent Service (Spring WebFlux)

This is a Spring Boot WebFlux application that demonstrates AI agent integration using both Semantic Kernel and Foundry Agent Service for intelligent task management. It provides a simple CRUD task list and two interactive chat agents.

## Getting Started

See [Tutorial: Build an agentic web app in Azure App Service with Semantic Kernel (Java)](https://learn.microsoft.com/azure/app-service/tutorial-ai-agent-web-app-semantic-kernel-java).

## Features

- **CRUD Operations**: Create, Read, Update, Delete tasks using REST API.
- **Semantic Kernel Agent**: Local agent orchestration with automatic function calling.
- **Foundry Agent**: Cloud-hosted agent with managed function calling.
- **Dual Agent UI**: Compare and interact with both agent frameworks side-by-side.
- **Azure Deployment**: Azure Developer CLI (azd) template can create App Service with managed identity using `azd up`.

## Implementation Notes

### Semantic Kernel Agent

The Semantic Kernel agent implementation uses the ChatCompletionAgent with automatic function calling:

1. **ChatCompletionAgent**: Uses Microsoft's Semantic Kernel ChatCompletionAgent for intelligent task management.
2. **Automatic Function Calling**: Configured with `FunctionChoiceBehavior.auto(true)` to automatically call appropriate plugin functions.
3. **Direct Plugin Integration**: Uses `@DefineKernelFunction` annotations in `TaskCrudPlugin` for seamless integration.
4. **Local Orchestration**: Agent runs in the application with direct access to task database.

### Foundry Agent

The Foundry Agent implementation uses the Azure AI Agents SDK with async clients:

1. **AgentsAsyncClient**: Uses Azure's cloud-hosted agent service for intelligent task management.
2. **Managed Function Calling**: Agent and tools configured in the Foundry portal.
3. **Conversation Management**: Each session maintains its own conversation thread.
4. **Cloud Orchestration**: Agent runs in Azure with API-based access to task functions.

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
        │       │   └── AgentController.java            # AI agent endpoints (SK & Foundry)
        │       ├── model/
        │       │   └── TaskItem.java                   # Task entity
        │       ├── plugin/
        │       │   └── TaskCrudPlugin.java             # Semantic Kernel plugin
        │       ├── repository/
        │       │   └── TaskRepository.java             # Data repository
        │       └── service/
        │           ├── SemanticKernelAgentService.java # SK agent service
        │           └── FoundryAgentService.java        # Foundry Agent Service
        └── resources/
            ├── application.properties                  # Application configuration
            ├── schema.sql                              # Database schema
            └── static/
                └── index.html                          # Frontend UI (dual agent)
```

## Configuration

The application requires configuration for both agent types:

### Semantic Kernel (Azure OpenAI)
```properties
azure.openai.endpoint=https://your-openai-resource.openai.azure.com/
azure.openai.deployment=gpt-4o
```

### Foundry Agent
```properties
azure.foundry.endpoint=https://your-project.api.azureml.ms
azure.foundry.agent.name=your-agent-name
```

Both agents use Azure managed identity for authentication.

