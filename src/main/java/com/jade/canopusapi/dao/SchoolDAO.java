package com.jade.canopusapi.dao;

import com.jade.canopusapi.models.School;
import com.jade.canopusapi.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolDAO {

    @Autowired
    private SchoolRepository schoolRepository;

    public ResponseEntity<?> createSchool(School school) {
        if (schoolRepository.existsByName(school.getName())) {
            return ResponseEntity.badRequest().body("Já existe uma escola com esse nome");
        }
        schoolRepository.save(school);
        return ResponseEntity.status(201).body("Escola criada");
    }

    public School findSchoolById(Long id) {
        return schoolRepository.findById(id).orElse(null);
    }

    public List<School> getAll() {
        return schoolRepository.findAll();
    }

    public List<School> getAllByUserId(Long id) {
        return schoolRepository.findByRepsId(id);
    }
}
