package com.aloumDaum.user.repository;

import com.aloumDaum.user.data.LoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<LoginEntity,Long> {
}
