package com.example.demo.repository;

import com.example.demo.models.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageRepository extends JpaRepository<Attachment,Integer> {
}
