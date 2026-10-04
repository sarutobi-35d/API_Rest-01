package com.API_Rest_01.service;

import com.API_Rest_01.entity.Personnes;
import com.API_Rest_01.repository.PersonnesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonnesServiceImpl implements PersonnesService {

    private final PersonnesRepository personnesRepository;

    @Override
    public List<Personnes> getPersons() {

        return personnesRepository.findAll();
    }

    @Override
    public Personnes addPerson(Personnes personnes) {
        return personnesRepository.save(personnes);
    }

    @Override
    public Personnes updatePerson(Long id, Personnes personnes) {

        return personnesRepository.findById(id)
                .map(personnes1 -> {
                    personnes1.setNom(personnes.getNom());
                    personnes1.setPrénom(personnes.getPrénom());
                    personnes1.setAge(personnes.getAge());

                    return personnesRepository.save(personnes1);
                }).orElseThrow(() -> new RuntimeException("Aucune personne trouvée avec cet Id."));
    }

    @Override
    public String deletePerson(Long id) {
        personnesRepository.deleteById(id);
        return "Cette Personne *" + id + "* a été supprimée";
    }
}
