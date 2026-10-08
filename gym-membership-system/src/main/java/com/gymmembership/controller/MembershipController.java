package com.gymmembership.controller;

import com.gymmembership.dto.MembershipFormDTO;
import com.gymmembership.entity.Resource;
import com.gymmembership.entity.ServiceRequest;
import com.gymmembership.entity.enums.ResourceStatus;
import com.gymmembership.exception.BusinessException;
import com.gymmembership.repository.ResourceRepository;
import com.gymmembership.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/membership")
public class MembershipController {

    private final RequestService requestService;
    private final ResourceRepository resourceRepository;

    public MembershipController(RequestService requestService, ResourceRepository resourceRepository) {
        this.requestService = requestService;
        this.resourceRepository = resourceRepository;
    }

    @GetMapping("/create")
    public String createMembershipPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        String userRole = (String) session.getAttribute("userRole");
        if (!"USER".equals(userRole)) {
            return "redirect:/login";
        }
        
        List<Resource> resources = resourceRepository.findByStatus(ResourceStatus.ACTIVE);
        List<Resource> allResources = resourceRepository.findAll();
        
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("resources", resources);
        model.addAttribute("allResources", allResources);
        model.addAttribute("form", new MembershipFormDTO());
        
        return "create-membership";
    }

    @PostMapping("/submit")
    public String submitMembership(@ModelAttribute("form") MembershipFormDTO form, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        String userRole = (String) session.getAttribute("userRole");
        if (!"USER".equals(userRole)) {
            return "redirect:/login";
        }
        
        try {
            ServiceRequest request = requestService.submitRequest(userId, form);
            redirectAttributes.addFlashAttribute("successMessage",
                "Membership request submitted successfully! Reference: " + request.getReferenceId());
            return "redirect:/requests";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/membership/create";
        }
    }
}
