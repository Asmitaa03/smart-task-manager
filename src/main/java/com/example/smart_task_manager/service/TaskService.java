package com.example.smart_task_manager.service;

import com.example.smart_task_manager.dto.TaskRequest;
import com.example.smart_task_manager.entity.Task;
import com.example.smart_task_manager.entity.User;
import com.example.smart_task_manager.repository.TaskRepository;
import com.example.smart_task_manager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(TaskRequest request, Long userId) {
        validateTaskRequest(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String status = (request.getStatus() == null || request.getStatus().isBlank()) ? "PENDING" : request.getStatus();
        if (!isValidStatus(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        LocalDate deadline = LocalDate.parse(request.getDeadline());
        if (deadline.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deadline cannot be in the past");
        }

        Task task = new Task(
                request.getTitle().trim(),
                request.getDescription() != null ? request.getDescription().trim() : null,
                deadline,
                status,
                user
        );
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, TaskRequest request, Long callerUserId) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!"ADMIN".equals(caller.getRole()) && !existing.getUser().getId().equals(caller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update this task");
        }

        validateTaskRequest(request);
        if (request.getStatus() != null && !request.getStatus().isBlank() && !isValidStatus(request.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        LocalDate deadline = LocalDate.parse(request.getDeadline());
        if (deadline.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deadline cannot be in the past");
        }

        existing.setTitle(request.getTitle().trim());
        existing.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        existing.setDeadline(deadline);
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            existing.setStatus(request.getStatus());
        }
        return taskRepository.save(existing);
    }

    public void deleteTask(Long taskId, Long callerUserId) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!"ADMIN".equals(caller.getRole()) && !existing.getUser().getId().equals(caller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this task");
        }
        taskRepository.deleteById(taskId);
    }

    public List<Task> getTasksForUser(Long userId) {
        return taskRepository.findByUser_Id(userId);
    }

    public List<Task> getAllTasksForAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!"ADMIN".equals(admin.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can view all tasks");
        }
        return taskRepository.findAll();
    }

    private void validateTaskRequest(TaskRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be empty");
        }
        if (request.getDeadline() == null || request.getDeadline().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deadline is required");
        }
        try {
            LocalDate.parse(request.getDeadline());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deadline format must be yyyy-MM-dd");
        }
    }

    private static boolean isValidStatus(String status) {
        return "PENDING".equals(status) || "IN_PROGRESS".equals(status) || "COMPLETED".equals(status);
    }
}
