package com.simplify.todos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simplify.todos.entities.Todo;
import com.simplify.todos.repositories.TodoRepository;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> findAll(){
        return todoRepository.findAll();
    }

    public Todo create(Todo todo){
        return todoRepository.save(todo);
    }

    public Todo update(Long id,Todo todo){
        Todo entity = todoRepository.getReferenceById(id);
        updateData(entity,todo);
        return todoRepository.save(entity);
    }

    private void updateData(Todo entity, Todo todo) {
       entity.setNome(todo.getNome());
       entity.setDescricao(todo.getDescricao());
       entity.setRealizado(todo.getRealizado());
       entity.setPrioridade(todo.getPrioridade());
    }

    public void deleteById(Long id){
        todoRepository.deleteById(id);
    }

}
