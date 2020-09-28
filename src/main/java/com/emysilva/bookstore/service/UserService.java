package com.emysilva.bookstore.service;

import com.emysilva.bookstore.model.User;
import com.emysilva.bookstore.model.security.PasswordResetToken;
import com.emysilva.bookstore.model.security.UserRole;

import java.util.Set;

public interface UserService {
    PasswordResetToken getPasswordResetToken(final String token);

    void createPasswordResetTokenForUser(final User user, final String token);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User createUser(User user, Set<UserRole> userRoles) throws Exception;

    void save(User user);

    User findUserById(Long id);
}
