package com.example.demo.controllers;

import java.util.HashMap;

import com.example.demo.repository.UserRepository;
import com.example.demo.security.MyUserDetailsService;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.security.models.AuthenticateResponse;
import com.example.demo.security.util.JwtUtil;
import com.example.demo.services.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WelcomeController {

    HashMap<String, String> db = new HashMap<>();
    @Autowired
    UserRepository userRepository;
    @Autowired
    LoginService loginService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private JwtUtil jwtTokenUtil;
            
    @RequestMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping(path="/login")
    public String login() {
        return "login";
    }
    
    @RequestMapping(value="/authenticate",method=RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken (@RequestBody AuthenticateRequest authenticateRequest) throws Exception{
        try{
            authenticate(authenticateRequest.getUsername(),authenticateRequest.getPassword());
        }
        catch(BadCredentialsException e){
            throw new Exception("Incorrect username or password",e);
        }
        
        final UserDetails userDetails=myUserDetailsService.loadUserByUsername(authenticateRequest.getUsername());
        final String jwt=jwtTokenUtil.generateToken(userDetails);
        
        return ResponseEntity.ok(new AuthenticateResponse(jwt));
    }
    private void authenticate(String username, String password) throws Exception {
	try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	} catch (DisabledException e) {
		throw new Exception("USER_DISABLED", e);
	} catch (BadCredentialsException e) {
		throw new Exception("INVALID_CREDENTIALS", e);
	}
    }
    
    @PostMapping(path="/login")
    public String loginPost(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        if (!loginService.authenticate(username, password)) {
            return "401";
        }
        redirectAttributes.addFlashAttribute("username", username);
        return "redirect:/project-dashboard";
    }

    @GetMapping(path="/register")
    public String register() {
        return "register";
    }

    @PostMapping(path="/register") 
    public String registerPost(@RequestParam String username, @RequestParam String password) {
        System.out.println(username + " " + password);
        db.put(username, password);
        return "redirect:/login";
    }

    @GetMapping(path="/project-dashboard")
    public String projectDashboard(@ModelAttribute("username") String username, Model model) {
        model.addAttribute("username", username);
        return "project_dashboard";
    }

    @GetMapping(path="/project-page/{projectName}")
    public String projectPage(@PathVariable String projectName, Model model) {
        model.addAttribute("projectName", projectName);
        return "project_page";
    }

    @GetMapping(path="/issues/{projectName}")
    public String issues(@PathVariable String projectName, Model model) {
        model.addAttribute("projectName", projectName);
        return "issue_dashboard";
    }

    @GetMapping(path="/issue-page/{issueName}")
    public String issuePage(@PathVariable String issueName, Model model) {
        model.addAttribute("issueName", issueName);
        return "issue_page";
    }

}
