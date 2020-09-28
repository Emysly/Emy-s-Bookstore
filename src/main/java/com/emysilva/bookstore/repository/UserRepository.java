package com.emysilva.bookstore.repository;

import com.emysilva.bookstore.model.User;
import com.emysilva.bookstore.model.security.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    User findUserByEmail(String email);

//    User createUser(User user, Set<UserRole> userRoles);
}
