package com.simplify.todos.openApi;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
            .info(new Info()
                .title("Lista de Tarefas(To-do List)")
                .version("FernandoCanabarroAhnert")
                .description("Esta Lista de Tarefas é um projeto baseado no desafio proposto pela Simplify"))
                .externalDocs(new ExternalDocumentation()
                        .description("Link Github do Desafio proposto")
                        .url("https://github.com/simplify-tec/desafio-junior-backend-simplify"))
                .tags(Arrays.asList(
                    new Tag().name("Tarefa").description("Listagem dos Métodos HTTP para as Tarefas")
                ));
    }

}
