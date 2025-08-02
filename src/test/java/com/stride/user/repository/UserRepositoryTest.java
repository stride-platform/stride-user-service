package com.stride.user.repository;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.stride.user.domain.Organization;
import com.stride.user.domain.User;
import com.stride.user.domain.enums.TaskVisibilityPolicy;
import com.stride.user.domain.enums.UserRole;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_ShouldReturnUser_WhenEmailExists() {

        Organization organization = Organization.builder()
        .id(UUID.randomUUID())
        .name("Test Organization")
        .domain("test.org")
        .taskVisibilityPolicy(com.stride.user.domain.enums.TaskVisibilityPolicy.PUBLIC_TO_ALL)
        .isActive(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

        // Persist the organization first
        Organization savedOrg = entityManager.persistAndFlush(organization);
        
        // Given: Create and save a user
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .fullName("Test User")
                .role(UserRole.TEAM_MEMBER)
                .isActive(true)
                .organization(savedOrg) // Associate with the saved organization
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                
                .build();
        
        entityManager.persistAndFlush(user);

        // When: Search by email
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then: User should be found
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getFullName()).isEqualTo("Test User");
    }

    @Test
    void testFindByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // When: Search for non-existent email
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then: Should return empty
        assertThat(found).isEmpty();
    }

    @Test
void testFindByRole_ShouldReturnUsersWithSpecificRole() {
    // Given: Create organization
    Organization org = Organization.builder()
            .name("Test Org")
            .domain("test.com")
            .taskVisibilityPolicy(TaskVisibilityPolicy.PRIVATE_TO_TEAMS)
            .id(UUID.randomUUID())
            .isActive(true)
            .build();
    Organization savedOrg = entityManager.persistAndFlush(org);

    // Given: Create users with different roles
    User adminUser = User.builder()
            .email("admin@test.com")
            .passwordHash("hash1")
            .fullName("Admin User")
            .role(UserRole.ORG_ADMIN)
            .organization(savedOrg)
            .isActive(true)
            .build();

    User memberUser = User.builder()
            .email("member@test.com")
            .passwordHash("hash2")
            .fullName("Member User")
            .role(UserRole.TEAM_MEMBER)
            .organization(savedOrg)
            .isActive(true)
            .build();

    User anotherAdmin = User.builder()
            .email("admin2@test.com")
            .passwordHash("hash3")
            .fullName("Another Admin")
            .role(UserRole.ORG_ADMIN)
            .organization(savedOrg)
            .isActive(true)
            .build();

    entityManager.persistAndFlush(adminUser);
    entityManager.persistAndFlush(memberUser);
    entityManager.persistAndFlush(anotherAdmin);

    // When: Search by role (Spring Data Magic)
    List<User> admins = userRepository.findByRole(UserRole.ORG_ADMIN);
    List<User> members = userRepository.findByRole(UserRole.TEAM_MEMBER);

    // Then: Should find correct users
    assertThat(admins).hasSize(2);
    assertThat(admins).extracting(User::getEmail)
            .containsExactlyInAnyOrder("admin@test.com", "admin2@test.com");
    
    assertThat(members).hasSize(1);
    assertThat(members.get(0).getEmail()).isEqualTo("member@test.com");
}

@Test
void testFindActiveUsersByOrganizationId_CustomQuery() {
    // Given: Create organization
    Organization org = Organization.builder()
            .name("Test Org")
            .domain("test.com")
            .taskVisibilityPolicy(TaskVisibilityPolicy.PRIVATE_TO_TEAMS)
            .id(UUID.randomUUID())
            .isActive(true)
            .build();
    Organization savedOrg = entityManager.persistAndFlush(org);

    // Given: Create active and inactive users
    User activeUser = User.builder()
            .email("active@test.com")
            .passwordHash("hash1")
            .fullName("Active User")
            .role(UserRole.TEAM_MEMBER)
            .organization(savedOrg)
            .isActive(true)
            .build();

    User inactiveUser = User.builder()
            .email("inactive@test.com")
            .passwordHash("hash2")
            .fullName("Inactive User")
            .role(UserRole.TEAM_MEMBER)
            .organization(savedOrg)
            .isActive(false)  // This user is inactive
            .build();

    entityManager.persistAndFlush(activeUser);
    entityManager.persistAndFlush(inactiveUser);

    // When: Use custom query to find only active users
    List<User> activeUsers = userRepository.findActiveUsersByOrganizationId(savedOrg.getId());

    // Then: Should only return active users
    assertThat(activeUsers).hasSize(1);
    assertThat(activeUsers.get(0).getEmail()).isEqualTo("active@test.com");
    assertThat(activeUsers.get(0).getIsActive()).isTrue();
}

}
