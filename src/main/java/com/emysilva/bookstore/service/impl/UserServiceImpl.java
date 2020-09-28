package com.emysilva.bookstore.service.impl;

import com.emysilva.bookstore.model.User;
import com.emysilva.bookstore.model.security.PasswordResetToken;
import com.emysilva.bookstore.model.security.UserRole;
import com.emysilva.bookstore.repository.PasswordResetTokenRepository;
import com.emysilva.bookstore.repository.RoleRepository;
import com.emysilva.bookstore.repository.UserRepository;
import com.emysilva.bookstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return  passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User createUser(User user, Set<UserRole> userRoles) throws Exception {
        User localUser = userRepository.findUserByUsername(user.getUsername());

        if (localUser != null) {
            LOG.info("User {} already exists.", user.getUsername());
        } else {

            userRoles.forEach(userRole -> roleRepository.save(userRole.getRole()));

            user.getUserRoles().addAll(userRoles);

            localUser = userRepository.save(user);
        }

        return localUser;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).get();
    }
}
