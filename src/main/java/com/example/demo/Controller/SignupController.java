// package com.example.demo.Controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.example.demo.User.User;
// import com.example.demo.User.UserAlreadyExistsException;
// import com.example.demo.User.UserService;

// @Controller
// public class SignupController {
//     @Autowired
//     private UserService userService;

//     @PostMapping("/signup")
//     public String signup(
//         @RequestParam String name,
//         @RequestParam String email,
//         @RequestParam String password,
//         @RequestParam("confirm-password") String confirmPassword,
//         Model model
//     ) {
//         if (!password.equals(confirmPassword)) {
//             model.addAttribute("error", "Passwords do not match");
//             return "signup";
//         }

//         try {
//             User newUser = new User();
//             newUser.setName(name);
//             newUser.setEmail(email);
//             newUser.setPassword(password);

//             userService.registerNewUser(newUser);
//             return "redirect:/login";
//         } catch (UserAlreadyExistsException e) {
//             model.addAttribute("error", "Email already in use");
//             return "signup";
//         }
//     }
// }
