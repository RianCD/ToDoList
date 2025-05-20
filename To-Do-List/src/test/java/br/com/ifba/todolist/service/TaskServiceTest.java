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

// Habilita a integração do Mockito com JUnit 5, inicializando mocks e injetando dependências automaticamente
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    // Declara um mock do TaskIRepository para simular o comportamento do repositório sem acessar o banco de dados
    @Mock
    private TaskIRepository taskRepository;

    // Cria uma instância do TaskService e injeta o mock taskRepository em seu construtor
    @InjectMocks
    private TaskService taskService;

    // Declara uma variável para armazenar uma tarefa de teste, reutilizada em vários testes
    private Task task;

    // Método executado antes de cada teste para configurar o estado inicial
    @BeforeEach
    void setUp() {
        // Cria uma tarefa válida com descrição "Estudar Java" e status não concluído
        task = new Task("Estudar Java", false);
        // Define o ID da tarefa como 1L, simulando uma tarefa já salva no banco
        task.setId(1L);
    }

    // Testa a criação de uma tarefa válida (cenário de sucesso)
    @Test
    void testCreateTask_Success() {
        // Configura o mock para retornar a tarefa salva quando o método save for chamado
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        // Chama o método createTask do TaskService com a tarefa de teste
        Task createdTask = taskService.createTask(task);
        // Verifica se a tarefa criada não é nula
        assertNotNull(createdTask);
        // Verifica se a descrição da tarefa criada é "Estudar Java"
        assertEquals("Estudar Java", createdTask.getDescription());
        // Confirma que o método save do repositório foi chamado exatamente uma vez com a tarefa
        verify(taskRepository, times(1)).save(task);
    }

    // Testa a criação de uma tarefa com descrição vazia (cenário de erro)
    @Test
    void testCreateTask_EmptyDescription_ThrowsException() {
        // Cria uma tarefa inválida com descrição vazia
        Task invalidTask = new Task("", false);
        // Verifica se o método createTask lança uma IllegalArgumentException para descrição vazia
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            // Chama o método createTask com a tarefa inválida
            taskService.createTask(invalidTask);
        });
        // Verifica se a mensagem da exceção é "Descrição não pode ser vazia"
        assertEquals("Descrição não pode ser vazia", exception.getMessage());
        // Confirma que o método save do repositório nunca foi chamado, pois a tarefa é inválida
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Testa a listagem de todas as tarefas
    @Test
    void testGetAllTasks() {
        // Configura o mock para retornar uma lista com a tarefa de teste quando findAll for chamado
        when(taskRepository.findAll()).thenReturn(List.of(task));
        // Chama o método getAllTasks do TaskService
        List<Task> tasks = taskService.getAllTasks();
        // Verifica se a lista retornada contém exatamente uma tarefa
        assertEquals(1, tasks.size());
        // Verifica se a descrição da primeira tarefa na lista é "Estudar Java"
        assertEquals("Estudar Java", tasks.get(0).getDescription());
        // Confirma que o método findAll do repositório foi chamado exatamente uma vez
        verify(taskRepository, times(1)).findAll();
    }

    // Testa a busca de uma tarefa por ID quando ela existe (cenário de sucesso)
    @Test
    void testGetTaskById_Found() {
        // Configura o mock para retornar a tarefa de teste dentro de um Optional quando findById for chamado com ID 1L
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        // Chama o método getTaskById do TaskService com ID 1L
        Optional<Task> foundTask = taskService.getTaskById(1L);
        // Verifica se o Optional contém uma tarefa (tarefa foi encontrada)
        assertTrue(foundTask.isPresent());
        // Verifica se a descrição da tarefa encontrada é "Estudar Java"
        assertEquals("Estudar Java", foundTask.get().getDescription());
        // Confirma que o método findById do repositório foi chamado uma vez com ID 1L
        verify(taskRepository, times(1)).findById(1L);
    }

    // Testa a busca de uma tarefa por ID quando ela não existe (cenário de erro)
    @Test
    void testGetTaskById_NotFound() {
        // Configura o mock para retornar um Optional vazio quando findById for chamado com ID 1L
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        // Chama o método getTaskById do TaskService com ID 1L
        Optional<Task> foundTask = taskService.getTaskById(1L);
        // Verifica se o Optional está vazio (tarefa não foi encontrada)
        assertFalse(foundTask.isPresent());
        // Confirma que o método findById do repositório foi chamado uma vez com ID 1L
        verify(taskRepository, times(1)).findById(1L);
    }

    // Testa a atualização de uma tarefa existente (cenário de sucesso)
    @Test
    void testUpdateTask_Success() {
        // Cria uma tarefa com dados atualizados (nova descrição e status concluído)
        Task updatedTask = new Task("Estudar Spring", true);
        // Configura o mock para retornar a tarefa original quando findById for chamado
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        // Configura o mock para retornar a tarefa atualizada quando save for chamado
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        // Chama o método updateTask do TaskService com ID 1L e os novos dados
        Task result = taskService.updateTask(1L, updatedTask);
        // Verifica se a descrição foi atualizada para "Estudar Spring"
        assertEquals("Estudar Spring", result.getDescription());
        // Verifica se o status de conclusão foi atualizado para true
        assertTrue(result.isCompleted());
        // Confirma que o método findById do repositório foi chamado uma vez com ID 1L
        verify(taskRepository, times(1)).findById(1L);
        // Confirma que o método save do repositório foi chamado uma vez
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // Testa a tentativa de atualizar uma tarefa inexistente (cenário de erro)
    @Test
    void testUpdateTask_NotFound_ThrowsException() {
        // Configura o mock para retornar um Optional vazio (tarefa não existe)
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        // Verifica se o método updateTask lança uma IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            // Chama o método updateTask com um ID inexistente
            taskService.updateTask(1L, task);
        });
        // Verifica se a mensagem da exceção é "Tarefa não encontrada"
        assertEquals("Tarefa não encontrada", exception.getMessage());
        // Confirma que o método findById do repositório foi chamado uma vez
        verify(taskRepository, times(1)).findById(1L);
        // Confirma que o método save do repositório nunca foi chamado
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Testa a deleção de uma tarefa existente (cenário de sucesso)
    @Test
    void testDeleteTask_Success() {
        // Configura o mock para indicar que a tarefa com ID 1L existe
        when(taskRepository.existsById(1L)).thenReturn(true);
        // Chama o método deleteTask do TaskService com ID 1L
        taskService.deleteTask(1L);
        // Confirma que o método existsById do repositório foi chamado uma vez
        verify(taskRepository, times(1)).existsById(1L);
        // Confirma que o método deleteById do repositório foi chamado uma vez com ID 1L
        verify(taskRepository, times(1)).deleteById(1L);
    }

    // Testa a tentativa de deletar uma tarefa inexistente (cenário de erro)
    @Test
    void testDeleteTask_NotFound_ThrowsException() {
        // Configura o mock para indicar que a tarefa com ID 1L não existe
        when(taskRepository.existsById(1L)).thenReturn(false);
        // Verifica se o método deleteTask lança uma IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            // Chama o método deleteTask com um ID inexistente
            taskService.deleteTask(1L);
        });
        // Verifica se a mensagem da exceção é "Tarefa não encontrada"
        assertEquals("Tarefa não encontrada", exception.getMessage());
        // Confirma que o método existsById do repositório foi chamado uma vez
        verify(taskRepository, times(1)).existsById(1L);
        // Confirma que o método deleteById do repositório nunca foi chamado
        verify(taskRepository, never()).deleteById(1L);
    }
}