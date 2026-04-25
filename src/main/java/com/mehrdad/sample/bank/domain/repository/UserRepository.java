package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Page<UserEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
