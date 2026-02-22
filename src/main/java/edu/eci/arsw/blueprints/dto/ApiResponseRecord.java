package edu.eci.arsw.blueprints.dto;

public record ApiResponseRecord<T>(
        int code,
        String message,
        T data
) {}
