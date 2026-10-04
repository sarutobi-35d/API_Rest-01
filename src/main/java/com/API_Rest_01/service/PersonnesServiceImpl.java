package com.API_Rest_01.service;

import com.API_Rest_01.dto.PersonnesDTO;
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
    public List<PersonnesDTO> getPersons() {

        return personnesRepository.findAll()
                .stream()
                .map(this::enDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonnesDTO addPerson(PersonnesDTO personnesDTO) {

        // 1. Convertir le DTO reçu en Entité JPA
        Personnes personneAEnregistrer = enEntity(personnesDTO);

        // 2. Sauvegarder l'entité en BD
        Personnes personneSauvegardee = personnesRepository.save(personneAEnregistrer);

        // 3. Re-convertir l'entité sauvegardée en DTO pour le retour
        return enDTO(personneSauvegardee);
    }

    @Override
    public PersonnesDTO updatePerson(Long id, PersonnesDTO personnesDTO) {

        return personnesRepository.findById(id)
                .map(personneExistante -> {

                    personneExistante.setNom(personnesDTO.getNom());
                    personneExistante.setPrénom(personnesDTO.getPrénom());
                    personneExistante.setAge(personnesDTO.getAge());

                    Personnes personneMaj = personnesRepository.save(personneExistante);
                    return enDTO(personneMaj);

                }).orElseThrow(() -> new RuntimeException("Aucune personne trouvée avec cet Id : " + id));
    }

    @Override
    public String deletePerson(Long id) {
        personnesRepository.deleteById(id);
        return "Cette Personne *" + id + "* a été supprimée";
    }

    //CONVERTIONS... DTO en ENTITY
    private Personnes enEntity(PersonnesDTO personnesDTO){

        Personnes personnes = new Personnes();

        personnes.setNom(personnesDTO.getNom());
        personnes.setPrénom(personnesDTO.getPrénom());
        personnes.setAge(personnesDTO.getAge());

        return personnes;
    }

    //CONVERTIONS... ENTITY en DTO
    private PersonnesDTO enDTO(Personnes personnes){

        PersonnesDTO dto = new PersonnesDTO();

        dto.setNom(personnes.getNom());
        dto.setPrénom(personnes.getPrénom());
        dto.setAge(personnes.getAge());

        return dto;
    }
}
