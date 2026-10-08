package com.gymmembership.controller;

import com.gymmembership.entity.User;
import com.gymmembership.entity.Resource;
import com.gymmembership.entity.enums.Role;
import com.gymmembership.entity.enums.ResourceStatus;
import com.gymmembership.repository.UserRepository;
import com.gymmembership.repository.ResourceRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public LoginController(UserRepository userRepository, ResourceRepository resourceRepository) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String userId, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/login";
        }
        User user = optUser.get();
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userRole", user.getRole().name());
        
        if (user.getRole() == Role.ADMINISTRATOR) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        String userRole = (String) session.getAttribute("userRole");
        if (!"USER".equals(userRole)) {
            return "redirect:/login";
        }
        
        String userName = (String) session.getAttribute("userName");
        List<Resource> resources = resourceRepository.findByStatus(ResourceStatus.ACTIVE);
        
        model.addAttribute("userName", userName);
        model.addAttribute("resources", resources);
        
        return "user-dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
