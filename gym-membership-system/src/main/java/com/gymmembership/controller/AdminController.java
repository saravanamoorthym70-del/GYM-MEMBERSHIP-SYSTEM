package com.gymmembership.controller;

import com.gymmembership.entity.ServiceRequest;
import com.gymmembership.exception.BusinessException;
import com.gymmembership.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RequestService requestService;

    public AdminController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMINISTRATOR".equals(userRole)) {
            return "redirect:/login";
        }
        
        List<ServiceRequest> requests = requestService.getAllRequests();
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("requests", requests);
        
        return "admin-dashboard";
    }

    @PostMapping("/requests/{refId}/advance")
    public String advanceStatus(@PathVariable String refId, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMINISTRATOR".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only administrators can advance request status.");
            return "redirect:/login";
        }
        
        try {
            requestService.advanceRequestStatus(refId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Request status updated successfully.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }
}
