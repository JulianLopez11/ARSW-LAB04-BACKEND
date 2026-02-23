package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.dto.DrawEvent;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class BluePrintWebSocketController {

    private final SimpMessagingTemplate template;
    private final BlueprintsServices services; 

    public BluePrintWebSocketController(SimpMessagingTemplate template, BlueprintsServices services) {
        this.template = template;
        this.services = services;
    }

    @MessageMapping("/draw")
    public void onDraw(DrawEvent evt) {
        try {
            services.addPoint(evt.author(), evt.name(), evt.point().x(), evt.point().y());
            Blueprint updatedBp = services.getBlueprint(evt.author(), evt.name());
            template.convertAndSend("/topic/blueprints." + evt.author() + "." + evt.name(), updatedBp);
            
        } catch (Exception e) {
            System.err.println("Error en dibujo RT: " + e.getMessage());
        }
    }
}