package com.emysilva.bookstore.controller;

import com.emysilva.bookstore.model.Book;
import com.emysilva.bookstore.model.User;
import com.emysilva.bookstore.model.security.PasswordResetToken;
import com.emysilva.bookstore.model.security.Role;
import com.emysilva.bookstore.model.security.UserRole;
import com.emysilva.bookstore.service.BookService;
import com.emysilva.bookstore.service.UserService;
import com.emysilva.bookstore.service.impl.UserSecurityService;
import com.emysilva.bookstore.util.MailConstructor;
import com.emysilva.bookstore.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.*;

@Controller
public class HomeController {
    @Autowired
    UserService userService;
    @Autowired
    UserSecurityService userSecurityService;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    MailConstructor mailConstructor;
    @Autowired
    BookService bookService;

    @RequestMapping("/")
    public String getHome() {
        return "index";
    }

    @RequestMapping("/myAccount")
    public String getAccount() {
        return "myAccount";
    }

    @RequestMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("classActiveLogin", true);
        return "myAccount";
    }

    @RequestMapping(value="/newUser", method = RequestMethod.POST)
    public String registerNewUser(
            HttpServletRequest request,
            @ModelAttribute("email") String email,
            @ModelAttribute("username") String username,
            Model model) throws Exception{
        model.addAttribute("classActiveNewAccount", true);
        model.addAttribute("email", email);
        model.addAttribute("username", username);

        if (userService.findUserByUsername(username) != null) {
            model.addAttribute("usernameExists", true);
            return "myAccount";
        }

        if (userService.findUserByEmail(email) != null) {
            model.addAttribute("emailExists", true);
            return "myAccount";
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);

        String password = SecurityUtil.randomPassword();

        String encryptedPassword  = SecurityUtil.passwordEncoder().encode(password);

        user.setPassword(encryptedPassword);

        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        User user1 = userService.createUser(user, userRoles);

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        SimpleMailMessage mail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);

        mailSender.send(mail);

        model.addAttribute("emailSent", true);

        return "myAccount";

    }

    @RequestMapping("/newUser")
    public String newUser(Locale locale, @RequestParam("token") String token, Model model) {
        PasswordResetToken passToken = userService.getPasswordResetToken(token);

        if (passToken == null) {
            String message = "Invalid Token.";
            model.addAttribute("message", message);
            return "redirect:/badRequest";
        }

        User user = passToken.getUser();
        String username = user.getUsername();

        UserDetails userDetails = userSecurityService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("user", user);

        model.addAttribute("classActiveEdit", true);
        return "myProfile";
    }

    @RequestMapping("/forgetPassword")
    public String getForgetPasswordForm(
            HttpServletRequest request,
            @ModelAttribute("email") String email,
            Model model
    ) throws Exception {

        model.addAttribute("classActiveForgetPassword", true);

        User user = userService.findUserByEmail(email);

        if (user == null) {
            model.addAttribute("emailNotExist", true);
            return "myAccount";
        }

        String password = SecurityUtil.randomPassword();

        String encryptedPassword  = SecurityUtil.passwordEncoder().encode(password);

        user.setPassword(encryptedPassword);


        userService.save(user);

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);

        mailSender.send(newEmail);

        model.addAttribute("forgetPasswordEmailSent", true);

        return "myAccount";
    }

    @RequestMapping(value="/updateUserInfo", method=RequestMethod.POST)
    public String updateUserInfo(
            @ModelAttribute("user") User user,
            @ModelAttribute("newPassword") String newPassword,
            Model model
    ) throws Exception {
        User currentUser = userService.findUserById(user.getId());

        if(currentUser == null) {
            throw new Exception ("User not found");
        }

        /*check email already exists*/
        if (userService.findUserByEmail(user.getEmail())!=null) {
            if(!userService.findUserByEmail(user.getEmail()).getId().equals(currentUser.getId())) {
                model.addAttribute("emailExists", true);
                return "myProfile";
            }
        }

        /*check username already exists*/
        if (userService.findUserByUsername(user.getUsername())!=null) {
            if(userService.findUserByUsername(user.getUsername()).getId() != currentUser.getId()) {
                model.addAttribute("usernameExists", true);
                return "myProfile";
            }
        }

//		update password
        if (newPassword != null && !newPassword.isEmpty()){
            BCryptPasswordEncoder passwordEncoder = SecurityUtil.passwordEncoder();
            String dbPassword = currentUser.getPassword();
            if(passwordEncoder.matches(user.getPassword(), dbPassword)){
                currentUser.setPassword(passwordEncoder.encode(newPassword));
            } else {
                model.addAttribute("incorrectPassword", true);

                return "myProfile";
            }
        }

        currentUser.setFirstname(user.getFirstname());
        currentUser.setLastname(user.getLastname());
        currentUser.setUsername(user.getUsername());
        currentUser.setEmail(user.getEmail());

        userService.save(currentUser);

        model.addAttribute("updateSuccess", true);
        model.addAttribute("user", currentUser);
        model.addAttribute("classActiveEdit", true);

        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("listOfCreditCards", true);

        UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
//        model.addAttribute("orderList", user.getOrderList());

        return "myProfile";
    }

    @RequestMapping("/myProfile")
    public String myProfile(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("classActiveEdit", true);

        return "myProfile";
    }

    @RequestMapping("/bookshelf")
    public String bookshelf(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "bookshelf";
    }

    @RequestMapping("/bookDetail")
    public String bookDetail(
            @PathParam("id") Long id, Model model, Principal principal
    ) {
        if(principal != null) {
            String username = principal.getName();
            User user = userService.findUserByUsername(username);
            model.addAttribute("user", user);
        }

        Book book = bookService.getABook(id);

        model.addAttribute("book", book);

        List<Integer> qtyList = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        model.addAttribute("qtyList", qtyList);
        model.addAttribute("qty", 1);

        return "bookDetail";
    }
}
