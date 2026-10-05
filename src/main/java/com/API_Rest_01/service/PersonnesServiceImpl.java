package com.API_Rest_01.service;

import com.API_Rest_01.dto.PersonneRequestDTO;
import com.API_Rest_01.dto.PersonneResponseDTO;
import com.API_Rest_01.entity.Personnes;
import com.API_Rest_01.repository.PersonnesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonnesServiceImpl implements PersonnesService {

    private final PersonnesRepository personnesRepository;

    @Override
    public List<PersonneResponseDTO> getPersons() {
        return personnesRepository.findAll()
                .stream()
                .map(this::enResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonneResponseDTO addPerson(PersonneRequestDTO requestDTO) {

        Personnes personneAEnregistrer = enEntity(requestDTO);
        Personnes personneSauvegardee = personnesRepository.save(personneAEnregistrer);

        return enResponseDTO(personneSauvegardee);
    }

    @Override
    public PersonneResponseDTO updatePerson(Long id, PersonneRequestDTO requestDTO) {

        return personnesRepository.findById(id)
                .map(personneExistante -> {
                    personneExistante.setNom(requestDTO.nom());
                    personneExistante.setPrénom(requestDTO.prénom());
                    personneExistante.setAge(requestDTO.age());

                    Personnes personneMaj = personnesRepository.save(personneExistante);
                    return enResponseDTO(personneMaj);

                })
                .orElseThrow(() -> new RuntimeException("Aucune personne trouvée avec cet Id : " + id));
    }

    @Override
    public String deletePerson(Long id) {
        personnesRepository.deleteById(id);
        return "Cette Personne *" + id + "* a été supprimée";
    }

    //CONVERTIONS... DTO en ENTITY
    private Personnes enEntity(PersonneRequestDTO requestDTO){

        Personnes personnes = new Personnes();

        personnes.setNom(requestDTO.nom());
        personnes.setPrénom(requestDTO.prénom());
        personnes.setAge(requestDTO.age());

        return personnes;
    }

    //CONVERTIONS... ENTITY en DTO
    private PersonneResponseDTO enResponseDTO(Personnes personnes){

        return new PersonneResponseDTO(
                personnes.getId(),
                personnes.getNom(),
                personnes.getPrénom(),
                personnes.getAge()
        );
    }
}
