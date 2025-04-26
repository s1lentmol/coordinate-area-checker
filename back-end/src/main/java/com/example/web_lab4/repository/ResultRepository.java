package com.example.web_lab4.repository;

import com.example.web_lab4.model.Result;
import com.example.web_lab4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUser(User user);
}
