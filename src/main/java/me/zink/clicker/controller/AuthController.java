package me.zink.clicker.controller;

import jakarta.validation.Valid;
import me.zink.clicker.model.*;
import me.zink.clicker.payload.request.LoginRequest;
import me.zink.clicker.payload.request.SignupRequest;
import me.zink.clicker.payload.response.JwtResponse;
import me.zink.clicker.payload.response.MessageResponse;
import me.zink.clicker.repo.RoleRepository;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.jwt.JwtUtils;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.ActionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping(value = "/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if(loginRequest.getActions() != null && loginRequest.getActions().size() > userDetails.getActions().size()){
            for(int i = userDetails.getActions().size(); i < loginRequest.getActions().size(); i++){
                try{
                    Map<String, Object> actionMap = loginRequest.getActions().get(i);
                    String info = (String) actionMap.get("info");
                    if(info != null && info.length() > 24){
                        info = "null";
                    }
                    long serverTimestamp = ActionUtils.calcSTime(userDetails.getActions().get(0).getServerTimestamp(), userDetails.getActions().get(0).getClientTimestamp(), (long) actionMap.get("clientTimestamp"));
                    Action action = new Action(EAction.valueOf((String) actionMap.get("action")), info, (int) actionMap.get("location"), serverTimestamp, (long) actionMap.get("clientTimestamp"));
                    userDetails.addAction(userRepository, action);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return ResponseEntity.ok(new JwtResponse(
                jwt,

                //PlayerData
                userDetails.getLocationLevel(),
                userDetails.getMobSeed(),
                userDetails.getActions()
                ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if(signUpRequest.getPassword() == null || signUpRequest.getPassword().length() < 8 || signUpRequest.getPassword().length() >= 40){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Password is incorrect!"));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        //TODO Verify username and password length
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );
        user.addAction(new Action(EAction.INIT, "null", 1, System.currentTimeMillis(), signUpRequest.getTimestamp()));
        user.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Role 'USER' not found!")))));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
