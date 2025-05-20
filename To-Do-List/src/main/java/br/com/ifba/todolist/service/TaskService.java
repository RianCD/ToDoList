package br.com.ifba.todolist.service;

import br.com.ifba.todolist.model.Task;
import br.com.ifba.todolist.repository.TaskIRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskIRepository taskRepository;

    public TaskService(TaskIRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Long id, Task task) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isEmpty()) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }
        Task updatedTask = existingTask.get();
        updatedTask.setDescription(task.getDescription());
        updatedTask.setCompleted(task.isCompleted());
        return taskRepository.save(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }
        taskRepository.deleteById(id);
    }
}
