package com.API_Rest_01.service;

import com.API_Rest_01.dto.PersonnesDTO;

import java.util.List;

public interface PersonnesService {

    List<PersonnesDTO> getPersons();

    PersonnesDTO addPerson(PersonnesDTO personnesDTO);

    PersonnesDTO updatePerson(Long id, PersonnesDTO personnesDTO);

    String deletePerson(Long id);

}
