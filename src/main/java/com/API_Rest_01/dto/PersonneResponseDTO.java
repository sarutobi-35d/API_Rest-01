package com.API_Rest_01.dto;

public record PersonneResponseDTO(
        Long id,
        String nom,
        String prénom,
        Integer age
) {}
