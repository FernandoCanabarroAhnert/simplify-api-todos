package com.simplify.todos;

import static com.simplify.todos.commons.TodosConstants.INVALIDTODO;
import static com.simplify.todos.commons.TodosConstants.TODO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplify.todos.entities.Todo;
import com.simplify.todos.repositories.TodoRepository;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TodoIT {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo existingTodo;
    private Todo updatedTodo;

    @BeforeEach
    public void setUp() {
        existingTodo = new Todo();
        existingTodo.setId(1L);
        existingTodo.setNome("Tarefa existente");
        existingTodo.setDescricao("Descrição existente");
        existingTodo.setRealizado(false);
        existingTodo.setPrioridade(2);

        updatedTodo = new Todo();
        updatedTodo.setNome("Tarefa atualizada");
        updatedTodo.setDescricao("Descrição atualizada");
        updatedTodo.setRealizado(true);
        updatedTodo.setPrioridade(1);
    }

    @Test
    public void createTodo_withValidData_returnsCreated() throws Exception{
        when(todoRepository.save(any(Todo.class))).thenReturn(TODO);

        webTestClient.post().uri("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(TODO))
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.nome").isEqualTo("nome")
            .jsonPath("$.descricao").isEqualTo("descricao")
            .jsonPath("$.realizado").isEqualTo(false)
            .jsonPath("$.prioridade").isEqualTo(1);
    }

    @Test
    public void createTodo_withUnvalidData_returnsBadRequest() throws Exception{
        webTestClient.post().uri("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(INVALIDTODO))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void findTodoById_withExistingId_returnsTodo() throws Exception{
        when(todoRepository.findById(1L)).thenReturn(Optional.of(TODO));

        webTestClient.get().uri("/todos/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.nome").isEqualTo("nome")
            .jsonPath("$.descricao").isEqualTo("descricao")
            .jsonPath("$.realizado").isEqualTo(false)
            .jsonPath("$.prioridade").isEqualTo(1);
    }

    @Test
    public void findTodoById_withUnexistingId_returnsNotFound(){
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        webTestClient.get().uri("/todos/999")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void findAllTodos_returnsAllTodos() throws Exception{
        when(todoRepository.findAll()).thenReturn(List.of(TODO));

        webTestClient.get().uri("/todos")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].nome").isEqualTo(TODO.getNome())
            .jsonPath("$[0].descricao").isEqualTo(TODO.getDescricao())
            .jsonPath("$[0].realizado").isEqualTo(TODO.getRealizado())
            .jsonPath("$[0].prioridade").isEqualTo(TODO.getPrioridade());
    }

    @Test
    public void findAllTodos_returnsEmpty() throws Exception{
        when(todoRepository.findAll()).thenReturn(Collections.emptyList());

        webTestClient.get().uri("/todos")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    public void updateTodo_withValidData_returnsUpdatedTodo() throws Exception{
        when(todoRepository.getReferenceById(1L)).thenReturn(existingTodo);
        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);

        webTestClient.put().uri("/todos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(updatedTodo))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.nome").isEqualTo("Tarefa atualizada")
            .jsonPath("$.descricao").isEqualTo("Descrição atualizada")
            .jsonPath("$.realizado").isEqualTo(true)
            .jsonPath("$.prioridade").isEqualTo(1);
    }

    @Test
    public void updateTodo_withUnvalidData_returnsBadRequest() throws Exception{
        when(todoRepository.getReferenceById(999L)).thenThrow(new EntityNotFoundException());

        webTestClient.put().uri("/todos/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(updatedTodo))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void deleteTodoById_withExistingId_returnsNoContent() throws Exception{
        doNothing().when(todoRepository).deleteById(1L);

        webTestClient.delete().uri("/todos/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void deleteById_withUnexistingId_returnsNotFound() throws Exception{
        doThrow(new EntityNotFoundException()).when(todoRepository).deleteById(999L);

        webTestClient.delete().uri("/todos/999")
            .exchange()
            .expectStatus().isNotFound();
    }

}
