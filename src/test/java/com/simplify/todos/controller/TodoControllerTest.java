package com.simplify.todos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplify.todos.controllers.TodoController;
import com.simplify.todos.entities.Todo;
import com.simplify.todos.services.TodoService;

import jakarta.persistence.EntityNotFoundException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static com.simplify.todos.commons.TodosConstants.TODO;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

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
        when(todoService.create(TODO)).thenReturn(TODO);

        mockMvc.perform(post("/todos").content(objectMapper.writeValueAsString(TODO)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(TODO));
    }

    @Test
    public void createTodo_withUnvalidData_returnsBadRequest() throws Exception{
        Todo emptyTodo = new Todo();
        Todo invalidTodo = new Todo("", "", null, null);

        mockMvc.perform(post("/todos").content(objectMapper.writeValueAsString(invalidTodo))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

        mockMvc.perform(post("/todos").content(objectMapper.writeValueAsString(emptyTodo))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void findTodoById_withExistingId_returnsPlanet() throws Exception{
        when(todoService.findById(1L)).thenReturn(TODO);

        mockMvc.perform(get("/todos/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(TODO));
    }

    @Test
    public void findTodoById_withUnexistingId_returnsNotFound() throws Exception{
        mockMvc.perform(get("/tasks/{id}", TODO.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAllTodos_returnsAllTodos() throws Exception{
        when(todoService.findAll()).thenReturn(Collections.singletonList(TODO));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("nome"))
                .andExpect(jsonPath("$[0].descricao").value("descricao"));

        verify(todoService, times(1)).findAll();
    }

    @Test
    public void findAllTodos_returnsEmpty() throws Exception{
        when(todoService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/todos"))
        .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateTodoSuccess() throws Exception {
        when(todoService.update(eq(1L), any(Todo.class))).thenReturn(updatedTodo);

        mockMvc.perform(put("/todos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Tarefa atualizada"))
                .andExpect(jsonPath("$.descricao").value("Descrição atualizada"))
                .andExpect(jsonPath("$.realizado").value(true))
                .andExpect(jsonPath("$.prioridade").value(1));
    }

    @Test
    public void testUpdateTodoFailure() throws Exception {
        when(todoService.update(eq(999L), any(Todo.class))).thenThrow(new EntityNotFoundException());

        mockMvc.perform(put("/todos/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteTodoById_withExistingId_returnsNoContent() throws Exception{
        mockMvc.perform(delete("/todos/1"))
        .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTodoById_withUnexistingId_returnsNotFound() throws Exception{
        final Long todoId = 1L;
        doThrow(new EmptyResultDataAccessException(1)).when(todoService).deleteById(todoId);

        mockMvc.perform(delete("/todos" + todoId))
        .andExpect(status().isNotFound());
    }


}
