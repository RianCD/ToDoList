package br.com.ifba.todolist.service;

import br.com.ifba.todolist.model.Task;
import br.com.ifba.todolist.repository.TaskIRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskIRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Estudar Java", false);
        task.setId(1L);
    }

    @Test
    void testCreateTask_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        assertEquals("Estudar Java", createdTask.getDescription());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testCreateTask_EmptyDescription_ThrowsException() {
        Task invalidTask = new Task("", false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(invalidTask);
        });

        assertEquals("Descrição não pode ser vazia", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
        assertEquals("Estudar Java", tasks.get(0).getDescription());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTaskById_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.getTaskById(1L);

        assertTrue(foundTask.isPresent());
        assertEquals("Estudar Java", foundTask.get().getDescription());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Task> foundTask = taskService.getTaskById(1L);

        assertFalse(foundTask.isPresent());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateTask_Success() {
        Task updatedTask = new Task("Estudar Spring", true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.updateTask(1L, updatedTask);

        assertEquals("Estudar Spring", result.getDescription());
        assertTrue(result.isCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask_NotFound_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.updateTask(1L, task);
        });

        assertEquals("Tarefa não encontrada", exception.getMessage());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTask_NotFound_ThrowsException() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.deleteTask(1L);
        });

        assertEquals("Tarefa não encontrada", exception.getMessage());
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, never()).deleteById(1L);
    }
}
