package com.API_Rest_01.repository;

import com.API_Rest_01.entity.Personnes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnesRepository extends JpaRepository<Personnes, Long> {

}
