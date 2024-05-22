package com.simplify.todos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simplify.todos.entities.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo,Long>{

}
