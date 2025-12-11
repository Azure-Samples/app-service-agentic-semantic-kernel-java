package com.example.crudtaskswithagent.controller;

import com.example.crudtaskswithagent.service.FoundryAgentService;
import com.example.crudtaskswithagent.service.SemanticKernelAgentService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * REST API controller for AI agent interactions.
 * This provides endpoints for both Semantic Kernel and Foundry agents.
 * Each browser session gets its own agent service instance with its own conversation.
 */
@RestController
@RequestMapping("/api/agents")
public class AgentController {
    
    private final ApplicationContext applicationContext;
    private final ConcurrentHashMap<String, SemanticKernelAgentService> skSessionServices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, FoundryAgentService> foundrySessionServices = new ConcurrentHashMap<>();
    
    public AgentController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @PostMapping("/semantic-kernel/chat")
    public Mono<String> chatWithSemanticKernel(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "sessionId") String sessionId) {
        
        SemanticKernelAgentService service = skSessionServices.computeIfAbsent(sessionId, 
            key -> applicationContext.getBean(SemanticKernelAgentService.class));
        
        return service.processMessage(message);
    }
    
    @PostMapping("/foundry/chat")
    public Mono<String> chatWithFoundry(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "sessionId") String sessionId) {
        
        // Create service on boundedElastic thread since constructor has blocking operations
        return Mono.fromCallable(() -> 
            foundrySessionServices.computeIfAbsent(sessionId, 
                key -> applicationContext.getBean(FoundryAgentService.class))
        )
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(service -> service.processMessage(message));
    }
}
