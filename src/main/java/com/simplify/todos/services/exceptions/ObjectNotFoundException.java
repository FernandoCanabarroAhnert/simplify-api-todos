package com.simplify.todos.services.exceptions;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(Long id){
        super("Object not found! Id: " + id);
    }
}
