package com.example.smart_task_manager.controller;

import com.example.smart_task_manager.dto.TaskRequest;
import com.example.smart_task_manager.entity.Task;
import com.example.smart_task_manager.security.UserPrincipal;
import com.example.smart_task_manager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:8080")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody TaskRequest request,
                           @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return taskService.createTask(request, principal.getId());
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable Long taskId,
                          @Valid @RequestBody TaskRequest request,
                          @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return taskService.updateTask(taskId, request, principal.getId());
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long taskId,
                           @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        taskService.deleteTask(taskId, principal.getId());
    }

    @GetMapping("/user/{userId}")
    public List<Task> getUserTasks(@PathVariable Long userId,
                                   @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        if (!principal.getId().equals(userId) && !"ADMIN".equals(principal.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return taskService.getTasksForUser(userId);
    }

    @GetMapping("/me")
    public List<Task> getMyTasks(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return taskService.getTasksForUser(principal.getId());
    }

    @GetMapping("/admin/all")
    public List<Task> getAllTasks(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return taskService.getAllTasksForAdmin(principal.getId());
    }
}
