package edu.eci.arsw.blueprints.dto;

import java.util.List;
import edu.eci.arsw.blueprints.model.Point;
//PARA STOMP

public record BluePrintUpdate(String author, String name, List<Point> points) {}