package com.API_Rest_01.controller;


import com.API_Rest_01.dto.PersonnesDTO;
import com.API_Rest_01.entity.Personnes;
import com.API_Rest_01.service.PersonnesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personnes")
@RequiredArgsConstructor
public class PersonnesController {

    private final PersonnesService personnesService;

    @GetMapping("/all")
    public List<PersonnesDTO> getAllsPersons(){
        return personnesService.getPersons();
    }

    @PostMapping("/add")
    public PersonnesDTO addPersonnes(@RequestBody PersonnesDTO personnesDTO){
        return personnesService.addPerson(personnesDTO);
    }

    @PutMapping("/update/{id}")
    public PersonnesDTO update(@PathVariable Long id, @RequestBody PersonnesDTO personnesDTO){
        return personnesService.updatePerson(id, personnesDTO);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        return personnesService.deletePerson(id);
    }
}
