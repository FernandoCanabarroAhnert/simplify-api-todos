package com.simplify.todos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simplify.todos.dtos.TodoRequestDTO;
import com.simplify.todos.dtos.TodoResponseDTO;
import com.simplify.todos.entities.Todo;
import com.simplify.todos.openApi.TodoControllerOpenAPI;
import com.simplify.todos.services.TodoService;
import com.simplify.todos.services.exceptions.ObjectNotFoundException;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/todos")
public class TodoController implements TodoControllerOpenAPI{

    @Autowired
    private TodoService todoService;

    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",description = "Retorna 201 se a Tarefa foi criada com sucesso"),
        @ApiResponse(responseCode = "422",description = "Retorna 422 se a Tarefa foi criada com dados inválidos")}
        )
    @PostMapping
    public ResponseEntity<List<Todo>> create(@RequestBody @Valid TodoRequestDTO todo){
        List<Todo> list = todoService.create(new Todo(todo));
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",description = "Retorna 200 se existir tarefas criadas")}
        )
    @GetMapping
    public ResponseEntity<List<TodoResponseDTO>> findAll(){
        List<TodoResponseDTO> list = todoService.findAll().stream().map(TodoResponseDTO::new).toList();
        if (list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(list);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",description = "Retorna 200 se a Tarefa foi encontrada com sucesso"),
        @ApiResponse(responseCode = "404",description = "Retorna 404 se o Id da Tarefa não existir")}
        )
    @GetMapping(value = "/{id}")
    public ResponseEntity<TodoResponseDTO> findById(@PathVariable Long id){
        Todo obj = todoService.findById(id);
        return ResponseEntity.ok().body(new TodoResponseDTO(obj));    
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",description = "Retorna 200 se a Tarefa foi encontrada e atualizada com sucesso"),
        @ApiResponse(responseCode = "404",description = "Retorna 404 se o Id da Tarefa não existir")}
        )
    @PutMapping(value = "/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Todo todo){
        try{
            todo = todoService.update(id, todo);
            return ResponseEntity.ok().body(todo);
        }
        catch(EntityNotFoundException e){
            throw new ObjectNotFoundException(id);
        }
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",description = "Retorna 204 se a Tarefa foi deletada com sucesso"),
        @ApiResponse(responseCode = "404",description = "Retorna 404 se o Id da Tarefa não existir")}
        )
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        try{
            todoService.findById(id);
            todoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        catch(EmptyResultDataAccessException e){
            throw new ObjectNotFoundException(id);
        }
    }


}
