package com.simplify.todos.dtos;

import com.simplify.todos.entities.Todo;

public record TodoResponseDTO(Long id,String nome,String descricao,Boolean realizado,Integer prioridade) {

    public TodoResponseDTO(Todo todo){
        this(todo.getId(), todo.getNome(), todo.getDescricao(), todo.getRealizado(), todo.getPrioridade());
    }

}
