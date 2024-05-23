package com.simplify.todos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.simplify.todos.entities.Todo;
import com.simplify.todos.services.TodoService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @PostMapping
    public ResponseEntity<Todo>create(@RequestBody @Valid Todo todo){
        todo = todoService.create(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }

    @GetMapping
    public ResponseEntity<List<Todo>> findAll(){
        List<Todo> list = todoService.findAll();
        if (list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Todo> findById(@PathVariable Long id){
        Todo obj = todoService.findById(id);
        if (obj == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().body(obj);    
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Todo todo){
        try{
            todo = todoService.update(id, todo);
            return ResponseEntity.ok().body(todo);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        try{
            todoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
