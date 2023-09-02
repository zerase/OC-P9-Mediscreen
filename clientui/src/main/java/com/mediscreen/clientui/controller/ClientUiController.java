package com.mediscreen.clientui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientUiController {

    private static final Logger logger = LoggerFactory.getLogger(ClientUiController.class);


    // === HOMEPAGE ===========================================================
    @GetMapping("/")
    public String displayLandingPage() {
        logger.debug("### Request called --> GET /");
        return "home";
    }

    @GetMapping("/home")
    public String displayHomePage() {
        logger.debug("### Request called --> GET /home");
        return "redirect:/";
    }

}
