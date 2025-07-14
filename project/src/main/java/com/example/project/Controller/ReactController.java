package com.example.project.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactController {
    /**
     * React 애플리케이션의 index.html로 리다이렉트하는 메소드입니다.
     * 모든 경로에 대해 React 애플리케이션을 서빙합니다.
     */ 
    @GetMapping("/{path:[^\\.]*}")
    public String redirectToReact() {
        return "forward:/index.html";
    }
}
