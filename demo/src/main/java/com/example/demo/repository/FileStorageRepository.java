package com.example.demo.repository;

import com.example.demo.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageRepository extends JpaRepository<File,Integer> {
}
