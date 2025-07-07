package com.discrowd.websocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class SimpleGreetingController {

    // Ta metoda zostanie wywołana, gdy klient wyśle wiadomość na /app/hello
    @MessageMapping("/hello")
    // Odpowiedź zostanie wysłana do wszystkich subskrybentów tematu /topic/public
    @SendTo("/topic/public")
    public String greet(String message) { // Możesz tu przyjąć prosty String lub własne DTO
        log.info("Received message from client: {}", message);
        return "Hello from WebSocket server! You said: \"" + message + "\"";
    }
}
