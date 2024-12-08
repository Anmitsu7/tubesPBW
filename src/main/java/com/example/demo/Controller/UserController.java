package com.example.demo.Controller;

import com.example.demo.User.User;
import com.example.demo.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
// UserController.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        logger.debug("Showing sign up form");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup") // Changed from /api/users/signup to /signup
    public ResponseEntity<?> signupUser(@RequestBody User user) {
        try {
            // Ensure role is set to PELANGGAN for new user registrations
            user.setRole(User.UserRole.PELANGGAN);

            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Terjadi kesalahan saat menyimpan pengguna baru: " + e.getMessage());
        }
    }
}
// package com.example.demo.Controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import com.example.demo.User.User;
// import com.example.demo.User.UserService;

// @Controller
// @RequestMapping("/signup")
// public class UserController {

// @Autowired
// private UserService userService;

// // Menampilkan form signup
// @GetMapping
// public String showSignUpForm(Model model) {
// // Menambahkan atribut 'user' ke model untuk binding dengan form HTML
// model.addAttribute("user", new User());
// return "signup"; // Mengarahkan ke file signup.html
// }

// // Memproses data signup
// @PostMapping
// public String processSignUp(@ModelAttribute("user") User user, Model model) {
// try {
// // Memanggil metode sign up pada service
// userService.saveUser(user);
// model.addAttribute("successMessage", "Sign up successful! You can now log
// in.");
// } catch (IllegalArgumentException e) {
// // Jika ada error (misalnya username atau email sudah digunakan)
// model.addAttribute("errorMessage", e.getMessage());
// }
// return "signup"; // Kembali ke halaman signup (bisa menampilkan pesan
// sukses/gagal)
// }
// }
// // package com.example.demo.Controller;

// // import com.example.demo.User.User;
// // import com.example.demo.User.UserService;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.stereotype.Controller;
// // import org.springframework.ui.Model;
// // import org.springframework.web.bind.annotation.GetMapping;
// // import org.springframework.web.bind.annotation.PostMapping;
// // import org.springframework.web.bind.annotation.ModelAttribute;

// // @Controller
// // public class UserController {

// // @Autowired
// // private UserService userService;

// // @GetMapping("/signup")
// // public String showSignUpForm(Model model) {
// // model.addAttribute("user", new User());
// // return "signup"; // Return to the signup form view
// // }

// // @PostMapping("/signup")
// // public String saveUser(@ModelAttribute User user, Model model) {
// // // Save user to the database
// // userService.saveUser(user);
// // model.addAttribute("message", "User registered successfully!");
// // return "login"; // Redirect to login page or show success message
// // }
// // }
