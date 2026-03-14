package com.example.smart_task_manager.repository;

import com.example.smart_task_manager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // particular user ke sab tasks
    List<Task> findByUser_Id(Long userId);
}
