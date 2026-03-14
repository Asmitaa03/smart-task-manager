package com.example.smart_task_manager.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDate deadline;

    // PENDING, IN_PROGRESS, COMPLETED
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;   // yeh User entity se mapping hai

    public Task() {
    }

    public Task(String title, String description, LocalDate deadline, String status, User user) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.user = user;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

