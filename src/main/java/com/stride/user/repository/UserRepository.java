package com.stride.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stride.user.domain.User;
import com.stride.user.domain.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findByOrganizationId(UUID organizationId);
    List<User> findByRole(UserRole role);
    List<User> findByIsActiveTrue();
    List<User> findByRoleAndOrganizationId(UserRole role, UUID organizationId);
    List<User> findByEmailContainingIgnoreCase(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.organization.id = :organizationId AND u.isActive = true")
    List<User> findActiveUsersByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.organization.id = :organizationId")
    Long countByOrganizationId(@Param("organizationId") UUID organizationId);

   
    

}
