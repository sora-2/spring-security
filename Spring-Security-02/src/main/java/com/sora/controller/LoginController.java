package com.sora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage() {
        return "login"; // ⬅️ 返回 login.html 模板（不要写 .html）
    }
}
