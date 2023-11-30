package com.jade.canopusapi.controller;


import com.jade.canopusapi.controller.util.AddressRetriever;
import com.jade.canopusapi.controller.util.Validator;
import com.jade.canopusapi.dao.SchoolDAO;
import com.jade.canopusapi.dao.UserDAO;
import com.jade.canopusapi.models.School;
import com.jade.canopusapi.models.User;
import com.jade.canopusapi.models.utils.Address;
import com.jade.canopusapi.payload.request.CreateSchoolRequest;
import com.jade.canopusapi.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController()
@RequestMapping("/school")
public class SchoolController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SchoolDAO schoolDAO;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('EMBAIXADOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> createSchool(@Valid @RequestBody CreateSchoolRequest request) {
        if (!Validator.isValidFullName(request.getName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Nome da escola inválido"));
        }
        if (!Validator.isValidCep(request.getCep())) {
            return ResponseEntity.badRequest().body(new MessageResponse("O CEP fornecido é inválido."));
        }
        if (!Validator.isValidStreetNumber(request.getStreetNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("O número de rua está em um formato inválido."));
        }
        Address address = AddressRetriever.retrieveAddressByCep(request.getCep());
        if (address == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erro ao conectar ao serviço de CEP. Favor tentar novamente mais tarde"));
        }
        address.setStreetNumber(request.getStreetNumber());

        Collection<String> emails = request.getEmails();
        if (emails != null && !emails.isEmpty()) {
            Set<User> reps = new HashSet<>();
            for (String email : emails) {
                User user = userDAO.findUserByEmail(email);
                if (user == null) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Usuário com email: " + email + " não existe"));
                }
                reps.add(user);
            }
            School school = new School(request.getName(), address, request.getType(), reps);
            return schoolDAO.createSchool(school);

        }
        return ResponseEntity.badRequest().body(new MessageResponse("Lista de emails está vazia"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMBAIXADOR') or hasAuthority('ADMINISTRADOR') or hasAuthority('REP_ESCOLA')")
    public ResponseEntity<?> getSchoolData(@PathVariable Long id) {
        School school = schoolDAO.findSchoolById(id);
        if (school != null) {
            return ResponseEntity.ok(school);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('EMBAIXADOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> getAllSchools() {
        List<School> schools = schoolDAO.getAll();
        if (!schools.isEmpty()) {
            return ResponseEntity.ok(schools);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/rep/{id}")
    @PreAuthorize("hasAuthority('REP_ESCOLA')")
    public ResponseEntity<?> getRepSchools(@PathVariable Long id) {
        List<School> schools = schoolDAO.getAllByUserId(id);
        if (!schools.isEmpty()) {
            return ResponseEntity.ok(schools);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
