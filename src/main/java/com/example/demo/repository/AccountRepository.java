package com.example.demo.repository;

import com.example.demo.domain.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<UserAccountEntity, Long> {
}
