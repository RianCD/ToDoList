package br.com.ifba.todolist.repository;

import br.com.ifba.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskIRepository extends JpaRepository<Task, Long> {
}
