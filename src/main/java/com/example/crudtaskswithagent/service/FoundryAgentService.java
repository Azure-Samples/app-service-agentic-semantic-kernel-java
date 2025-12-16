package com.example.crudtaskswithagent.service;

import com.azure.ai.agents.AgentsClientBuilder;
import com.azure.ai.agents.AgentsServiceVersion;
import com.azure.ai.agents.ConversationsAsyncClient;
import com.azure.ai.agents.ResponsesAsyncClient;
import com.azure.ai.agents.models.AgentReference;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.openai.models.responses.EasyInputMessage;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Service that provides Foundry Agent Service functionality.
 * Note: Create instances on a boundedElastic thread because the constructor
 * performs blocking operations (credential acquisition and conversation creation).
 */
@Service
@Scope("prototype") // Create new instance per session
public class FoundryAgentService {
    
    private static final Logger logger = LoggerFactory.getLogger(FoundryAgentService.class);
    
    private ConversationsAsyncClient conversationsClient;
    private ResponsesAsyncClient responsesClient;
    private AgentReference agentReference;
    private String conversationId;
    
    public FoundryAgentService(
            @Value("${azure.foundry.endpoint:}") String endpoint,
            @Value("${azure.foundry.agent.name:}") String agentName) {
        
        if (endpoint == null || endpoint.isBlank() || agentName == null || agentName.isBlank()) {
            logger.warn("Foundry agent service not configured - missing endpoint or agent name");
            return;
        }
        
        try {
            logger.info("Initializing Foundry agent with endpoint: {}", endpoint);
            
            // Configure Foundry client with endpoint and credentials
            AgentsClientBuilder builder = new AgentsClientBuilder()
                    .endpoint(endpoint)
                    .serviceVersion(AgentsServiceVersion.V2025_11_15_PREVIEW)
                    .credential(new DefaultAzureCredentialBuilder().build());
            
            // Build specialized async clients for conversations and responses
            this.conversationsClient = builder.buildConversationsAsyncClient();
            this.responsesClient = builder.buildResponsesAsyncClient();
            
            // Reference the agent by name (configured in Foundry portal)
            this.agentReference = new AgentReference(agentName);
            
            // Create a conversation for this session
            this.conversationId = conversationsClient.getConversationServiceAsync()
                    .create()
                    .get() // Blocking call - must be on boundedElastic thread
                    .id();
            
            logger.info("Foundry agent configured with agent: {}, conversation: {}", agentName, conversationId);
        } catch (Exception e) {
            logger.error("Failed to initialize Foundry agent: {}", e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Processes a user message using the Foundry Agent Service (async).
     * The agent automatically handles function calling based on the tools configured in the Foundry portal.
     */
    public Mono<String> processMessage(String userMessage) {
        if (conversationId == null) {
            return Mono.just("Foundry agent is not properly configured. Please check your environment variables.");
        }
        
        // Build user message input
        ResponseInputItem inputItem = ResponseInputItem.ofEasyInputMessage(
            EasyInputMessage.builder()
                .role(EasyInputMessage.Role.USER)
                .content(userMessage)
                .build()
        );
        
        // Create response parameters with user input
        ResponseCreateParams.Builder paramsBuilder = ResponseCreateParams.builder()
            .inputOfResponse(List.of(inputItem));
        
        // Send message to agent and get response
        return responsesClient
            .createWithAgentConversation(agentReference, conversationId, paramsBuilder)
            .map(response -> {
                // Extract text from response - find the message item in output array
                return response.output().stream()
                    .filter(item -> item.message().isPresent())
                    .findFirst()
                    .flatMap(item -> item.message())
                    .map(msg -> !msg.content().isEmpty() && msg.content().get(0).isOutputText()
                        ? msg.content().get(0).asOutputText().text()
                        : "I received your message but couldn't generate a response.")
                    .orElse("I received your message but couldn't generate a response.");
            })
            .timeout(Duration.ofSeconds(60))
            .onErrorResume(throwable -> {
                logger.error("Error processing message: {}", throwable.getMessage(), throwable);
                return Mono.just("Sorry, I encountered an error processing your request: " + throwable.getMessage());
            });
    }
}
