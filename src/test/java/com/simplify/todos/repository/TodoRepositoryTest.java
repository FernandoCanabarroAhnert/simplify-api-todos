package com.simplify.todos.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static com.simplify.todos.commons.TodosConstants.TODO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.simplify.todos.entities.Todo;
import com.simplify.todos.repositories.TodoRepository;

import jakarta.persistence.EntityNotFoundException;

@DataJpaTest
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void AfterEach(){
        TODO.setId(null);
    }

    @Test
    public void createTodo_withValidData_returnsTodo(){
        Todo todo = todoRepository.save(TODO);

        Todo sut = testEntityManager.find(Todo.class, todo.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getNome()).isEqualTo(TODO.getNome());
        assertThat(sut.getDescricao()).isEqualTo(TODO.getDescricao());
        assertThat(sut.getRealizado()).isEqualTo(TODO.getRealizado());
        assertThat(sut.getPrioridade()).isEqualTo(TODO.getPrioridade());
    }

    @Test
    public void createTodo_withInvalidData_throwsException(){
        Todo emptyTodo = new Todo();
        Todo invalidTodo = new Todo("","",null,null);

        assertThatThrownBy(() -> todoRepository.save(emptyTodo)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> todoRepository.save(invalidTodo)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void findByIdTodo_withExistingId_returnsTodo(){
        Todo todo = testEntityManager.persistAndFlush(TODO);
        Optional<Todo> todoOpt = todoRepository.findById(todo.getId());

        assertThat(todoOpt).isNotEmpty();
        assertThat(todoOpt.get()).isEqualTo(todo);
    }

    @Test
    public void findByIdTodo_withUnexistingId_returnsEmpty(){
        Optional<Todo> todoOpt = todoRepository.findById(1L);

        assertThat(todoOpt).isEmpty();
    }

    @Test
    public void findAllTodo_returnsAllTodos(){
        List<Todo> list = new ArrayList<>();
        list.add(TODO);
        list.add(new Todo("nome2","descricao2",false,2));
        list.add(new Todo("nome3","descricao3",false,3));

        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(3);
        assertThat(list.get(0)).isEqualTo(TODO);
    }

    @Test
    public void findAllTodo_returnsNoTodos(){
        List<Todo> list = new ArrayList<>();
        list.add(new Todo());

        list = todoRepository.findAll();

        assertThat(list).isEmpty();
    }

    @Test
    public void updateTodo_withValidData_returnsUpdatedTodo() {
        Todo todo = new Todo();
        todo.setNome("Tarefa original");
        todo.setDescricao("Descrição original");
        todo.setRealizado(false);
        todo.setPrioridade(2);

        Todo savedTodo = todoRepository.save(todo);

        savedTodo.setNome("Tarefa atualizada");
        savedTodo.setDescricao("Descrição atualizada");
        savedTodo.setRealizado(true);
        savedTodo.setPrioridade(1);

        Todo updatedTodo = todoRepository.save(savedTodo);

        assertThat(updatedTodo.getNome()).isEqualTo("Tarefa atualizada");
        assertThat(updatedTodo.getDescricao()).isEqualTo("Descrição atualizada");
        assertThat(updatedTodo.getRealizado()).isTrue();
        assertThat(updatedTodo.getPrioridade()).isEqualTo(1);
    }

    @Test
    public void updateTodo_withUnvalidData_throwsException(){
        Long nonExistentId = 999L;

        assertThrows(EntityNotFoundException.class, () -> {
            Todo todo = todoRepository.getReferenceById(nonExistentId);
            todo.setNome("Tentativa de atualização");
            todoRepository.save(todo);
        });
    }

    @Test
    public void deleteTodoById_withExistingId_removesTodoFromDatabase() {
        Todo todo = testEntityManager.persistFlushFind(TODO);

        todoRepository.deleteById(todo.getId());

        Todo removedTodo = testEntityManager.find(Todo.class, todo.getId());
        assertThat(removedTodo).isNull();
    }

    @Test
    public void deleteTodoById_withUnexistingId_throwsException(){
        assertThatThrownBy(() -> todoRepository.deleteById(TODO.getId())).isInstanceOf(RuntimeException.class);
    }


}

