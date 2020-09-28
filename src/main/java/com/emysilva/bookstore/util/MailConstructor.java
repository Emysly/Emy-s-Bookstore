package com.emysilva.bookstore.util;

import com.emysilva.bookstore.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import java.util.Locale;

@Component
public class MailConstructor {
    @Autowired
    private Environment env;

    public SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user, String password) throws AddressException {
        String url = contextPath + "/newUser?token=" + token;
        String message = "\nPlease click on this link to your email and edit your personal information, your password is: \n" + password;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Emy's Bookstore - New User");
        email.setText(url+message);
        email.setFrom(env.getProperty("support.email"));

        return email;
    }
}
