package com.example.demo.repository;

import com.example.demo.model.ResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<ResultEntity, Integer> {

    List<ResultEntity> findAllByOrderByIdDesc();
}