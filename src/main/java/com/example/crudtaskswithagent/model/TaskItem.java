package com.example.crudtaskswithagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("tasks")
@Schema(description = "A task item with title and completion status")
public class TaskItem {
    
    @Id
    @Column("id")
    @Schema(description = "Unique identifier for the task", example = "1")
    private Long id;
    
    @Column("title")
    @Schema(description = "Title of the task", example = "Buy groceries")
    private String title;
    
    @Column("is_complete")
    @Schema(description = "Whether the task is completed", example = "false")
    private boolean complete;
    
    public TaskItem() {}
    
    public TaskItem(String title) {
        this.title = title;
        this.complete = false;
    }
    
    public TaskItem(String title, boolean complete) {
        this.title = title;
        this.complete = complete;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public boolean getComplete() {
        return complete;
    }
    
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
    @Override
    public String toString() {
        return "TaskItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", complete=" + complete +
                '}';
    }
}
