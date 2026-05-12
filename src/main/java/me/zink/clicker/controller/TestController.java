package me.zink.clicker.controller;

import jakarta.validation.Valid;
import me.zink.clicker.payload.request.GetCheatInfoRequest;
import me.zink.clicker.payload.request.LoginRequest;
import me.zink.clicker.repo.ActionRepository;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.ActionUtils;
import me.zink.clicker.util.CheatReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private ActionRepository actionRepository;

    @GetMapping("/all")
    public String allAccess() {

        return "Test for all!"; //actionRepository.findById(2).get().getAction().name();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        //UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //userDetails.addExp(repo, 1);
        //System.out.println(userDetails.getExp());

        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> moderatorAccess(@Valid @RequestBody GetCheatInfoRequest cheatInfoRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CheatReport report = ActionUtils.validateActions(repo.getReferenceById(cheatInfoRequest.getPlayerID()));

        return ResponseEntity.ok(report.genReport());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
