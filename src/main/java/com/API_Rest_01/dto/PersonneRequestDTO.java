package com.API_Rest_01.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PersonneRequestDTO(

        @NotBlank(message = "Le nom ne doit pas être vide")
        @Size(min = 2, max = 30, message = "Le nom doit contenir entre 2 et 50 caractères")
        String nom,

        @NotBlank(message = "Le prénom ne doit pas être vide")
        String prénom,

        @NotNull(message = "L'âge est obligatoire")
        @Positive(message = "L'âge doit être un nombre positif")
        Integer age
) {}
