package com.simplify.todos.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.simplify.todos.entities.Todo;
import com.simplify.todos.repositories.TodoRepository;
import com.simplify.todos.services.TodoService;

import jakarta.persistence.EntityNotFoundException;

import static com.simplify.todos.commons.TodosConstants.TODO;
import static com.simplify.todos.commons.TodosConstants.INVALIDTODO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    private Todo todo;

    @BeforeEach
    public void setup(){
        todo = new Todo();
        todo.setId(1L);
        todo.setNome("Test Todo");
        todo.setDescricao("Description");
        todo.setRealizado(false);
        todo.setPrioridade(1);
    }

    @Test
    public void createTodo_withValidData_returnsTodo(){
        when(todoRepository.save(TODO)).thenReturn(TODO);

        Todo sut = todoService.create(TODO);

        assertThat(sut).isEqualTo(TODO);
    }

    @Test
    public void createTodo_withUnvalidData_throwsException(){
        when(todoRepository.save(INVALIDTODO)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> todoService.create(INVALIDTODO)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void findByIdTodo_withExistingId_returnsTodo(){
        when(todoRepository.findById(1L)).thenReturn(Optional.of(TODO));

        Todo sut = todoService.findById(1L);

        assertThat(sut).isNotNull();
        assertThat(sut).isEqualTo(TODO);
    }

    @Test
    public void findByIdTodo_withUnexistingId_returnsEmpty(){
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        Todo sut = todoService.findById(1L);

        assertThat(sut).isNull();
    }

    @Test
    public void findAllTodos_returnsAllTodos(){
        when(todoRepository.findAll()).thenReturn(Collections.singletonList(TODO));

        List<Todo> list = todoService.findAll();
        assertEquals(1, list.size());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    public void findAllTodos_returnsEmpty(){
        when(todoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Todo> sut = todoService.findAll();

        assertThat(sut).isEmpty();
    }

    @Test
    public void updateTodo_withValidData_returnsUpdatedTodo(){
        Long id = 1L;
        Todo existingTodo = new Todo();
        existingTodo.setId(id);
        existingTodo.setNome("Tarefa existente");
        existingTodo.setDescricao("Descrição existente");
        existingTodo.setRealizado(false);
        existingTodo.setPrioridade(2);

        Todo updatedTodo = new Todo();
        updatedTodo.setNome("Tarefa atualizada");
        updatedTodo.setDescricao("Descrição atualizada");
        updatedTodo.setRealizado(true);
        updatedTodo.setPrioridade(1);

        when(todoRepository.getReferenceById(id)).thenReturn(existingTodo);
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        Todo result = todoService.update(id, updatedTodo);

        assertThat(result.getNome()).isEqualTo("Tarefa atualizada");
        assertThat(result.getDescricao()).isEqualTo("Descrição atualizada");
        assertThat(result.getRealizado()).isTrue();
        assertThat(result.getPrioridade()).isEqualTo(1);
    }

    @Test
    public void updateTodo_withInvalidData_throwsException(){
        Long id = 999L;
        Todo updatedTodo = new Todo();
        updatedTodo.setNome("Tarefa atualizada");
        updatedTodo.setDescricao("Descrição atualizada");
        updatedTodo.setRealizado(true);
        updatedTodo.setPrioridade(1);

        when(todoRepository.getReferenceById(id)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> {
            todoService.update(id, updatedTodo);
        });

        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    public void deleteTodoById_withExiistingId_doesNotThrowAnyException(){
        assertThatCode(() -> todoService.deleteById(1L)).doesNotThrowAnyException();
    }

    @Test
    public void deleteTodoById_withUnexistingId_throwsException(){
        doThrow(new RuntimeException()).when(todoRepository).deleteById(99L);

        assertThatThrownBy(() -> todoService.deleteById(99L)).isInstanceOf(RuntimeException.class);
    }
}
