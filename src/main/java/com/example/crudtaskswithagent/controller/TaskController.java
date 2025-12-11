package com.example.crudtaskswithagent.controller;

import com.example.crudtaskswithagent.model.TaskItem;
import com.example.crudtaskswithagent.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST API controller for TaskItem CRUD operations.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management operations")
public class TaskController {
    
    private final TaskRepository taskRepository;
    
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @GetMapping
    @Operation(
        summary = "Get all tasks",
        description = "Retrieves all tasks from the database",
        operationId = "getAllTasks"
    )
    @ApiResponse(responseCode = "200", description = "List of tasks")
    public Flux<TaskItem> getAllTasks() {
        return taskRepository.findAllOrderById();
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get task by ID",
        description = "Retrieves a specific task by its ID",
        operationId = "getTaskById"
    )
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public Mono<TaskItem> getTaskById(
            @Parameter(description = "Task ID", required = true) @PathVariable Long id) {
        return taskRepository.findById(id);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new task",
        description = "Creates a new task with the specified title",
        operationId = "createTask"
    )
    @ApiResponse(responseCode = "201", description = "Task created")
    public Mono<TaskItem> createTask(
            @Parameter(description = "Task title", required = true) @RequestParam String title) {
        return taskRepository.save(new TaskItem(title));
    }
    
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Update a task",
        description = "Updates an existing task's title and/or completion status. Only provided fields will be updated.",
        operationId = "updateTask"
    )
    @ApiResponse(responseCode = "204", description = "Task updated")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public Mono<Void> updateTask(
            @Parameter(description = "Task ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated task details (partial updates supported)",
                required = true
            )
            @RequestBody TaskItem item) {
        return taskRepository.findById(id)
                .flatMap(task -> {
                    // Only update fields that are provided
                    if (item.getTitle() != null && !item.getTitle().isEmpty()) {
                        task.setTitle(item.getTitle());
                    }
                    // Always update complete status (boolean defaults to false)
                    task.setComplete(item.getComplete());
                    return taskRepository.save(task);
                })
                .then();
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a task",
        description = "Deletes a task by its ID",
        operationId = "deleteTask"
    )
    @ApiResponse(responseCode = "204", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public Mono<Void> deleteTask(
            @Parameter(description = "Task ID", required = true) @PathVariable Long id) {
        return taskRepository.deleteById(id);
    }
}
