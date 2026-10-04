package com.API_Rest_01.service;

import com.API_Rest_01.entity.Personnes;

import java.util.List;

public interface PersonnesService {

    List<Personnes> getPersons();

    Personnes addPerson(Personnes personnes);

    Personnes updatePerson(Long id, Personnes personnes);

    String deletePerson(Long id);

}
