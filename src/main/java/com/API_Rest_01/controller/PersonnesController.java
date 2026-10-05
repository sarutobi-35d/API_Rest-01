package com.API_Rest_01.controller;


import com.API_Rest_01.dto.PersonneRequestDTO;
import com.API_Rest_01.dto.PersonneResponseDTO;
import com.API_Rest_01.dto.PersonnesDTO;
import com.API_Rest_01.entity.Personnes;
import com.API_Rest_01.service.PersonnesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personnes")
@RequiredArgsConstructor
public class PersonnesController {

    private final PersonnesService personnesService;

    @GetMapping("/all")
    public List<PersonneResponseDTO> getAllsPersons(){
        return personnesService.getPersons();
    }

    @PostMapping("/add")
    public PersonneResponseDTO addPersonnes(@Valid @RequestBody PersonneRequestDTO requestDTO){
        return personnesService.addPerson(requestDTO);
    }

    @PutMapping("/update/{id}")
    public PersonneResponseDTO update(@PathVariable Long id, @Valid @RequestBody PersonneRequestDTO requestDTO){
        return personnesService.updatePerson(id, requestDTO);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        return personnesService.deletePerson(id);
    }
}
