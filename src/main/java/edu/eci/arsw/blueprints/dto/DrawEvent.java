package edu.eci.arsw.blueprints.dto;

import edu.eci.arsw.blueprints.model.Point;
//PARA STOMP
public record DrawEvent(String author, String name, Point point) {}