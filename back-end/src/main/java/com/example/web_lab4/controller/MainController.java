package com.example.web_lab4.controller;

import com.example.web_lab4.dto.PointRequest;
import com.example.web_lab4.dto.PointResponse;
import com.example.web_lab4.model.Result;
import com.example.web_lab4.model.User;
import com.example.web_lab4.repository.ResultRepository;
import com.example.web_lab4.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/main")
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResultRepository resultRepository;

    @PostMapping("/check-point")
    public PointResponse checkPoint(@Valid @RequestBody PointRequest pointRequest,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isHit = checkHit(pointRequest.getX(), pointRequest.getY(), pointRequest.getRadius());

        Result result = new Result();
        result.setX(pointRequest.getX());
        result.setY(pointRequest.getY());
        result.setRadius(pointRequest.getRadius());
        result.setHit(isHit);
        result.setTimestamp(LocalDateTime.now());
        result.setUser(user);

        resultRepository.save(result);

        return new PointResponse(isHit);
    }

    @GetMapping("/results")
    public List<Result> getResults(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return resultRepository.findByUser(user);
    }

    public boolean checkHit(double x, double y, double r){
        return checkInTriangle(x, y, r) || checkInRectangle(x, y, r) || checkInCircle(x, y, r);
    }

    public boolean checkInTriangle(double x, double y, double r){
        return (y <= 0.5 * x + r / 2) && (y >= 0) && (x <= 0);
    }

    public boolean checkInRectangle(double x, double y, double r){
        return (x <= 0) && (x >= -r) && (y >= -r/2) && (y <= 0);
    }

    public boolean checkInCircle(double x, double y, double r){
        return (x*x + y*y <= r*r) && (y >= 0) && (x >= 0);
    }
}
