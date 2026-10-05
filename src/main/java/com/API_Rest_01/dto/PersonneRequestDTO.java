package com.API_Rest_01.dto;

public record PersonneRequestDTO(
        String nom,
        String prénom,
        Integer age
) {}
