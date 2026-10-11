package com.API_Rest_01;

//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
//class ApiRest01ApplicationTests {

//	@Test
//	void contextLoads() {
//	}
//}


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.API_Rest_01.dto.PersonneResponseDTO;
import com.API_Rest_01.entity.Personnes;
import com.API_Rest_01.repository.PersonnesRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static com.API_Rest_01.service.PersonnesServiceImpl.PERSONNES_LISTE_CACHE;
import static com.API_Rest_01.service.PersonnesServiceImpl.PERSONNE_CACHE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ApiRest01ApplicationTests {

	private static final String URL = "/personnes";

	// Un Redis jetable, lancé dans Docker uniquement pour les tests
	@Container
	@ServiceConnection(name = "redis")
	static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:8.8.3"))
			.withExposedPorts(6379);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CacheManager cacheManager;

	// Espion : un vrai repository dont Mockito compte les appels
	@MockitoSpyBean
	private PersonnesRepository personnesRepository;

	@BeforeEach
	void setUp() {
		personnesRepository.deleteAll();                        // base propre
		cache(PERSONNE_CACHE).clear();                          // cache propre
		cache(PERSONNES_LISTE_CACHE).clear();
		Mockito.clearInvocations(personnesRepository);          // compteurs à zéro
	}

	// ---------- Méthodes utilitaires ----------

	private Personnes sauvegarder(String nom, String prenom, int age) {
		Personnes p = new Personnes();
		p.setNom(nom);
		p.setPrénom(prenom);
		p.setAge(age);
		return personnesRepository.save(p);
	}

	private Cache cache(String nom) {
		return Objects.requireNonNull(cacheManager.getCache(nom));
	}

	private String json(String nom, String prenom, int age) {
		return """
                {"nom":"%s","prénom":"%s","age":%d}
                """.formatted(nom, prenom, age);
	}

	// ---------- Tests ----------

	@Test
	void addPerson_metLaPersonneEnCache() throws Exception {
		MvcResult result = mockMvc.perform(post(URL + "/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json("Scarlet", "Jean", 30)))
				.andExpect(status().isOk())
				.andReturn();

		Number idJson = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long id = idJson.longValue();

		assertTrue(personnesRepository.findById(id).isPresent());   // en base

		PersonneResponseDTO enCache = cache(PERSONNE_CACHE).get(id, PersonneResponseDTO.class);
		assertNotNull(enCache);                                     // en cache
		assertEquals("Scarlet", enCache.nom());
	}

	@Test
	void getPerson_deuxiemeAppelNeTouchePasLaBase() throws Exception {
		Personnes p = sauvegarder("Martin", "Paul", 25);

		mockMvc.perform(get(URL + "/" + p.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nom").value("Martin"));
		Mockito.verify(personnesRepository, Mockito.times(1)).findById(p.getId());

		Mockito.clearInvocations(personnesRepository);

		mockMvc.perform(get(URL + "/" + p.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nom").value("Martin"));
		Mockito.verify(personnesRepository, Mockito.never()).findById(p.getId());
	}

	@Test
	void getPersons_laListeEstMiseEnCache() throws Exception {
		sauvegarder("A", "a", 20);
		sauvegarder("B", "b", 21);

		mockMvc.perform(get(URL + "/all"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
		Mockito.verify(personnesRepository, Mockito.times(1)).findAll();

		Mockito.clearInvocations(personnesRepository);

		mockMvc.perform(get(URL + "/all"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
		Mockito.verify(personnesRepository, Mockito.never()).findAll();
	}

	@Test
	void addPerson_invalideLaListeEnCache() throws Exception {
		sauvegarder("A", "a", 20);

		// 1. Remplit le cache de la liste (1 personne)
		mockMvc.perform(get(URL + "/all")).andExpect(jsonPath("$.length()").value(1));

		// 2. Ajoute une personne
		mockMvc.perform(post(URL + "/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json("Bernard", "Luc", 22)))
				.andExpect(status().isOk());

		// 3. La liste doit contenir 2 personnes (sinon l'ancienne liste périmée serait renvoyée)
		mockMvc.perform(get(URL + "/all")).andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	void updatePerson_metLeCacheAJour() throws Exception {
		Personnes p = sauvegarder("Ancien", "Nom", 40);

		// Remplit le cache avec l'ancienne valeur
		mockMvc.perform(get(URL + "/" + p.getId())).andExpect(status().isOk());

		mockMvc.perform(put(URL + "/update/" + p.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(json("Nouveau", "Nom", 41)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nom").value("Nouveau"));

		PersonneResponseDTO enCache = cache(PERSONNE_CACHE).get(p.getId(), PersonneResponseDTO.class);
		assertNotNull(enCache);
		assertEquals("Nouveau", enCache.nom());
	}

	@Test
	void deletePerson_retireLaPersonneDuCache() throws Exception {
		Personnes p = sauvegarder("Suppr", "Moi", 50);

		// 1. Remplit le cache (sinon le test ne prouverait rien)
		mockMvc.perform(get(URL + "/" + p.getId())).andExpect(status().isOk());
		assertNotNull(cache(PERSONNE_CACHE).get(p.getId()));

		// 2. Supprime
		mockMvc.perform(delete(URL + "/delete/" + p.getId())).andExpect(status().isOk());

		// 3. Ni en base, ni en cache
		assertFalse(personnesRepository.findById(p.getId()).isPresent());
		assertNull(cache(PERSONNE_CACHE).get(p.getId()));
	}

	@Test
	void getPerson_inexistante_nEstPasMiseEnCache() throws Exception {
		mockMvc.perform(get(URL + "/9999")).andExpect(status().isNotFound());

		assertNull(cache(PERSONNE_CACHE).get(9999L));
	}
}
