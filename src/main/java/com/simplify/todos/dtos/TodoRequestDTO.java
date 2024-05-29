package com.simplify.todos.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record TodoRequestDTO(
    @NotEmpty
    @Column(nullable = false)
    String nome,

    @NotEmpty
    @Column(nullable = false)
    String descricao,

    @NotNull
    @Column(nullable = false)
    Boolean realizado,

    @NotNull
    @Column(nullable = false)
    Integer prioridade) {

}
