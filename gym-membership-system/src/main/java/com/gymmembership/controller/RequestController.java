package com.gymmembership.controller;

import com.gymmembership.dto.RequestDetailsDTO;
import com.gymmembership.dto.RequestSummaryDTO;
import com.gymmembership.entity.enums.Role;
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
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public String myRequests(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<RequestSummaryDTO> requests = requestService.getUserRequests(userId);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("requests", requests);
        
        return "my-requests";
    }

    @GetMapping("/{refId}")
    public String requestDetails(@PathVariable String refId, HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        String userRole = (String) session.getAttribute("userRole");
        Role role = Role.valueOf(userRole);
        
        try {
            RequestDetailsDTO details = requestService.getRequestDetails(refId, userId, role);
            model.addAttribute("request", details);
            model.addAttribute("userName", session.getAttribute("userName"));
            model.addAttribute("userRole", userRole);
            return "request-details";
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{refId}/cancel")
    public String cancelRequest(@PathVariable String refId, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        String userRole = (String) session.getAttribute("userRole");
        if (!"USER".equals(userRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only users can cancel requests.");
            return "redirect:/login";
        }
        
        try {
            requestService.cancelRequest(refId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Request cancelled successfully.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/requests";
    }
}
