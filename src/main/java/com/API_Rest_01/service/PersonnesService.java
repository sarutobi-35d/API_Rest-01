package com.API_Rest_01.service;

import com.API_Rest_01.dto.PersonneRequestDTO;
import com.API_Rest_01.dto.PersonneResponseDTO;
import com.API_Rest_01.dto.PersonnesDTO;

import java.util.List;

public interface PersonnesService {

    List<PersonneResponseDTO> getPersons();

    PersonneResponseDTO addPerson(PersonneRequestDTO requestDTO);

    PersonneResponseDTO updatePerson(Long id, PersonneRequestDTO requestDTO);

    String deletePerson(Long id);

}
