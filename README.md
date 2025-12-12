# Agentic Azure App Service app with Semantic Kernel and Foundry Agent Service (Spring WebFlux)

This is a Spring Boot WebFlux application that demonstrates AI agent integration using both Semantic Kernel and Foundry Agent Service for intelligent task management. It provides a simple CRUD task list and two interactive chat agents.

## Getting Started

See [Tutorial: Build an agentic web app in Azure App Service with Microsoft Semantic Kernel or Foundry Agent Service (Spring Boot)](https://learn.microsoft.com/azure/app-service/tutorial-ai-agent-web-app-semantic-kernel-java).

## Features

- **CRUD Operations**: Create, Read, Update, Delete tasks using REST API.
- **OpenAPI Documentation**: Auto-generated OpenAPI schema at `/api/schema` for Foundry agent integration.
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
2. **Managed Function Calling**: Agent and tools configured in the Foundry portal using the OpenAPI schema.
3. **OpenAPI Integration**: REST API endpoints are documented with OpenAPI annotations and exposed at `/api/schema`.
4. **Conversation Management**: Each session maintains its own conversation thread.
5. **Cloud Orchestration**: Agent runs in Azure with API-based access to task functions.

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
        │       ├── config/
        │       │   └── OpenApiConfig.java              # OpenAPI/Swagger configuration
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

### OpenAPI Documentation
The API schema is auto-generated and available at:
```properties
springdoc.api-docs.path=/api/schema
```
Access the schema at `http://localhost:8080/api/schema` when running locally, or `https://your-app.azurewebsites.net/api/schema` when deployed.

### Semantic Kernel (Azure OpenAI)
```properties
azure.openai.endpoint=https://your-openai-resource.openai.azure.com/
azure.openai.deployment=gpt-4o
```

### Foundry Agent
```properties
azure.foundry.endpoint=https://<resource-name>.services.ai.azure.com/api/projects/<project-name>
azure.foundry.agent.name=your-agent-name
```

Both agents use Azure managed identity for authentication.

