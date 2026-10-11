package com.API_Rest_01.service;

import com.API_Rest_01.dto.PersonneRequestDTO;
import com.API_Rest_01.dto.PersonneResponseDTO;
import com.API_Rest_01.entity.Personnes;
import com.API_Rest_01.exception.ResourceNotFoundException;
import com.API_Rest_01.repository.PersonnesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonnesServiceImpl implements PersonnesService {

    public static final String PERSONNE_CACHE = "PERSONNE_CACHE";
    public static final String PERSONNES_LISTE_CACHE = "PERSONNES_LISTE_CACHE";

    private final PersonnesRepository personnesRepository;

    @Override
    @Cacheable(value = PERSONNES_LISTE_CACHE, key = "'all'")
    public List<PersonneResponseDTO> getPersons() {
        return personnesRepository.findAll()
                .stream()
                .map(this::enResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "PERSONNE_CACHE", key = "#id")
    public PersonneResponseDTO getPerson(Long id) {

        Personnes personnes = personnesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cette personne n'existe pas."));

        return enResponseDTO(personnes);
    }

    // @Caching permet de combiner plusieurs opérations de cache sur une méthode
    @Override
    @Caching(
            put = @CachePut(value = PERSONNE_CACHE, key = "#result.id"),
            evict = @CacheEvict(value = PERSONNES_LISTE_CACHE, allEntries = true))
    public PersonneResponseDTO addPerson(PersonneRequestDTO requestDTO) {

        Personnes personneAEnregistrer = enEntity(requestDTO);
        Personnes personneSauvegardee = personnesRepository.save(personneAEnregistrer);

        return enResponseDTO(personneSauvegardee);
    }

    @Override
    @Caching(
            put = @CachePut(value = PERSONNE_CACHE, key = "#result.id"),
            evict = @CacheEvict(value = PERSONNES_LISTE_CACHE, allEntries = true))
    public PersonneResponseDTO updatePerson(Long id, PersonneRequestDTO requestDTO) {

        return personnesRepository.findById(id)
                .map(personneExistante -> {
                    personneExistante.setNom(requestDTO.nom());
                    personneExistante.setPrénom(requestDTO.prénom());
                    personneExistante.setAge(requestDTO.age());

                    Personnes personneMaj = personnesRepository.save(personneExistante);
                    return enResponseDTO(personneMaj);

                })
                .orElseThrow(() -> new ResourceNotFoundException("Aucune personne trouvée avec cet Id : " + id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = PERSONNE_CACHE, key = "#id"),
            @CacheEvict(value = PERSONNES_LISTE_CACHE, allEntries = true)})
    public String deletePerson(Long id) {

        Personnes personnes = personnesRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Pas de personne avec cet ID [" + id + "]"));

        personnesRepository.delete(personnes);

        return "Cette Personne [" + id + "] a été supprimée";
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
