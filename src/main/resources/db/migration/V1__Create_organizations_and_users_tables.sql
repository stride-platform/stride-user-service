-- V1__Create_organizations_and_users_tables.sql

-- Organizations table
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255) UNIQUE,
    task_visibility_policy VARCHAR(50) NOT NULL DEFAULT 'PRIVATE_TO_TEAMS',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'TEAM_MEMBER',
    organization_id UUID REFERENCES organizations(id),
    is_active BOOLEAN NOT NULL DEFAULT true,
    email_verified BOOLEAN NOT NULL DEFAULT false,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_organization_id ON users(organization_id);

-- Constraints for enum values
ALTER TABLE users ADD CONSTRAINT chk_user_role 
    CHECK (role IN ('SUPER_ADMIN', 'ORG_ADMIN', 'TEAM_ADMIN', 'TEAM_MEMBER'));

ALTER TABLE organizations ADD CONSTRAINT chk_task_visibility_policy 
    CHECK (task_visibility_policy IN ('PUBLIC_TO_ALL', 'PRIVATE_TO_TEAMS'));