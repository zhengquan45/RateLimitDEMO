package com.example.demo.demos.web;

import com.example.demo.demos.service.MyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class RateLimiterController {

    private final MyService myService;

    @GetMapping("/rateLimit")
    public String rateLimit() {
        return myService.limitedMethod();
    }
}
