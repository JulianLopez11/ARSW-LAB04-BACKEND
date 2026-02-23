package edu.eci.arsw.blueprints.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.eci.arsw.blueprints.dto.BluePrintUpdate;
import edu.eci.arsw.blueprints.dto.DrawEvent;
import edu.eci.arsw.blueprints.model.Point;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.List;
//PARA STOMP
@Controller
public class BluePrintWebSocketController {

  private final SimpMessagingTemplate template;

  public BluePrintWebSocketController(SimpMessagingTemplate template) {
    this.template = template;
  }

  @MessageMapping("/draw")
  public void onDraw(DrawEvent evt) {
    var upd = new BluePrintUpdate(evt.author(), evt.name(), List.of(evt.point()));
    template.convertAndSend("/topic/blueprints." + evt.author() + "." + evt.name(), upd);
  }

  @ResponseBody
  @GetMapping("/api/v1/blueprints/{author}/{name}")
  public BluePrintUpdate get(@PathVariable String author, @PathVariable String name) {
    return new BluePrintUpdate(author, name, List.of(new Point(10,10), new Point(40,50)));
  }
}