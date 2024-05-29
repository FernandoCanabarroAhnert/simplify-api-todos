package com.simplify.todos.openApi;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.simplify.todos.dtos.TodoRequestDTO;
import com.simplify.todos.dtos.TodoResponseDTO;
import com.simplify.todos.entities.Todo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Tarefa")
public interface TodoControllerOpenAPI {

    @Operation(summary = "Cria uma nova Tarefa e também retorna todas as Tarefas criadas anteriormente")
    ResponseEntity<List<Todo>> create(@RequestBody @Valid TodoRequestDTO todo);

    @Operation(summary = "Encontra uma Tarefa pelo seu identificador(ID). Retorna o status HTTP 404(NOT_FOUND) se o Id não existir")
    ResponseEntity<TodoResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Retorna todas as Tarefas criadas.")
    ResponseEntity<List<TodoResponseDTO>> findAll();

    @Operation(summary = "Atualiza uma Tarefa com base no seu identificador(ID). Retorna o status HTTP 404(NOT_FOUND) se o Id não existir")
    ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Todo todo);

    @Operation(summary = "Deleta uma Tarefa com base no seu identificador(ID). Retorna o status HTTP 404(NOT_FOUND) se o Id não existir")
    ResponseEntity<Void> deleteById(@PathVariable Long id);

}
