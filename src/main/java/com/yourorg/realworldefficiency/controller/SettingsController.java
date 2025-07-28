package com.yourorg.realworldefficiency.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import com.yourorg.realworldefficiency.config.JwtUtil;
import com.yourorg.realworldefficiency.entity.User;

@Controller
public class SettingsController {
    
    @Autowired
    private com.yourorg.realworldefficiency.service.UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private com.yourorg.realworldefficiency.service.DataLoader dataLoader;

    // Helper method to get current user from JWT token
    private User getCurrentUser(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (jwtUtil.validateToken(token)) {
                        String username = jwtUtil.getUsernameFromToken(token);
                        return userService.getUserByUsername(username).orElse(null);
                    }
                }
            }
        }
        return null;
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, HttpServletRequest request) {
        // Get current user
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        String theme = "auto";
        String backgroundStyle = "light";
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("theme".equals(cookie.getName())) {
                    theme = cookie.getValue();
                }
                if ("backgroundStyle".equals(cookie.getName())) {
                    backgroundStyle = cookie.getValue();
                }
            }
        }
        model.addAttribute("theme", theme);
        model.addAttribute("backgroundStyle", backgroundStyle);
        model.addAttribute("username", currentUser.getUsername());
        return "settings";
    }
    
    @PostMapping(value = "/settings/update", consumes = {"multipart/form-data"})
    public String updateSettings(
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) String backgroundStyle,
            @RequestParam(required = false) String fontSize,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String confirmEmail,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        
        // Get current user
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "User session not found. Please login again.");
            return "redirect:/login";
        }
        
        // Theme and appearance
        if (theme != null) {
            Cookie themeCookie = new Cookie("theme", theme);
            themeCookie.setPath("/");
            themeCookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
            response.addCookie(themeCookie);
        }
        if (backgroundStyle != null) {
            Cookie bgCookie = new Cookie("backgroundStyle", backgroundStyle);
            bgCookie.setPath("/");
            bgCookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
            response.addCookie(bgCookie);
        }
        if (fontSize != null) {
            Cookie fontSizeCookie = new Cookie("fontSize", fontSize);
            fontSizeCookie.setPath("/");
            fontSizeCookie.setMaxAge(60 * 60 * 24 * 365);
            response.addCookie(fontSizeCookie);
        }
        // Email change
        if (email != null && !email.isBlank()) {
            if (!email.equals(confirmEmail)) {
                redirectAttributes.addFlashAttribute("error", "Email addresses do not match.");
                return "redirect:/settings";
            }
            // Call userService to update email
            boolean emailUpdated = userService.updateEmail(currentUser.getEmail(), email, currentPassword);
            if (!emailUpdated) {
                redirectAttributes.addFlashAttribute("error", "Email update failed. Please check your current password.");
                return "redirect:/settings";
            }
            redirectAttributes.addFlashAttribute("success", "Email updated successfully.");
        }
        // Password change
        if (currentPassword != null && !currentPassword.isBlank() && newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
                return "redirect:/settings";
            }
            // Call userService to update password (with current password verification)
            boolean passwordUpdated = userService.updatePassword(currentUser.getUsername(), currentPassword, newPassword);
            if (!passwordUpdated) {
                redirectAttributes.addFlashAttribute("error", "Password update failed. Please check your current password.");
                return "redirect:/settings";
            }
            redirectAttributes.addFlashAttribute("success", "Password updated successfully.");
        }
        return "redirect:/settings?updated=true";
    }

    @PostMapping("/settings/update-email")
    public String updateEmail(@RequestParam String email,
                         @RequestParam String confirmEmail,
                         @RequestParam String currentPassword,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request) {
        if (!email.equals(confirmEmail)) {
            redirectAttributes.addFlashAttribute("error", "Email addresses do not match.");
            return "redirect:/settings";
        }
        // Get current email from session or authentication (for demo, get from cookie or param)
        String currentEmail = null;
        if (request.getUserPrincipal() != null) {
            currentEmail = request.getUserPrincipal().getName();
        } else {
            // Fallback: try to get from a cookie or param (customize as needed)
            currentEmail = request.getParameter("currentEmail");
        }
        if (currentEmail == null) {
            redirectAttributes.addFlashAttribute("error", "Unable to determine current email.");
            return "redirect:/settings";
        }
        boolean success = userService.updateEmail(currentEmail, email, currentPassword);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect or new email is already taken.");
            return "redirect:/settings";
        }
        redirectAttributes.addFlashAttribute("success", "Email updated successfully.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/update-password")
    public String updatePassword(@RequestParam String currentPassword,
                            @RequestParam String newPassword,
                            @RequestParam String confirmPassword,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/settings";
        }
        String username = null;
        if (request.getUserPrincipal() != null) {
            username = request.getUserPrincipal().getName();
        }
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Unable to determine current user.");
            return "redirect:/settings";
        }
        boolean success = userService.updatePassword(username, currentPassword, newPassword);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/settings";
        }
        redirectAttributes.addFlashAttribute("success", "Password updated successfully.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/update-appearance")
    public String updateAppearance(@RequestParam(required = false) String theme,
                              @RequestParam(required = false) String backgroundStyle,
                              @RequestParam(required = false) String fontSize,
                              HttpServletResponse response,
                              RedirectAttributes redirectAttributes) {
        if (theme != null) {
            Cookie themeCookie = new Cookie("theme", theme);
            themeCookie.setPath("/");
            themeCookie.setMaxAge(60 * 60 * 24 * 365);
            response.addCookie(themeCookie);
        }
        if (backgroundStyle != null) {
            Cookie bgCookie = new Cookie("backgroundStyle", backgroundStyle);
            bgCookie.setPath("/");
            bgCookie.setMaxAge(60 * 60 * 24 * 365);
            response.addCookie(bgCookie);
        }
        if (fontSize != null) {
            Cookie fontSizeCookie = new Cookie("fontSize", fontSize);
            fontSizeCookie.setPath("/");
            fontSizeCookie.setMaxAge(60 * 60 * 24 * 365);
            response.addCookie(fontSizeCookie);
        }
        redirectAttributes.addFlashAttribute("success", "Appearance updated.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/clear-data")
    public String clearData(@RequestParam String email,
                       @RequestParam String password,
                       RedirectAttributes redirectAttributes) {
        boolean success = userService.clearAllData(email, password);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Email or password incorrect.");
            return "redirect:/settings";
        }
        redirectAttributes.addFlashAttribute("success", "All your data has been cleared.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/delete-account")
    public String deleteAccount(@RequestParam String email,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        // Verify credentials and delete user account
        boolean success = userService.deleteAccount(email, password);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Email or password incorrect.");
            return "redirect:/settings";
        }
        
        // Invalidate JWT cookie
        Cookie jwtCookie = new Cookie("jwt", "");
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        
        redirectAttributes.addFlashAttribute("success", "Your account has been deleted successfully.");
        return "redirect:/login";
    }

    // Debug endpoint to check trip data
    @GetMapping("/settings/debug-trips")
    public String debugTrips(RedirectAttributes redirectAttributes) {
        dataLoader.debugTripData();
        redirectAttributes.addFlashAttribute("success", "Trip data debug info printed to console.");
        return "redirect:/settings";
    }


} 
