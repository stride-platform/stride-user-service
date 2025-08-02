package com.stride.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stride.user.domain.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

}
