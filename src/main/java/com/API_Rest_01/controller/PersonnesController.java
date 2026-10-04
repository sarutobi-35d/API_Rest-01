package com.API_Rest_01.controller;


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
    public List<Personnes> getAllsPersons(){
        return personnesService.getPersons();
    }

    @PostMapping("/add")
    public Personnes addPersonnes(@RequestBody Personnes personnes){
        return personnesService.addPerson(personnes);
    }

    @PutMapping("/update/{id}")
    public Personnes update(@PathVariable Long id, @RequestBody Personnes personnes){
        return personnesService.updatePerson(id, personnes);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        return personnesService.deletePerson(id);
    }
}
