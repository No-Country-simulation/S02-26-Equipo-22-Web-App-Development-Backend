package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.dto.request.HorseRequestDTO;
import com.nocountry.equitrust.dto.response.HorseResponseDTO;
import com.nocountry.equitrust.service.HorseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/horses")
@RequiredArgsConstructor
public class HorseController {

    private final HorseService horseService;

    @PostMapping
    public ResponseEntity<HorseResponseDTO> createHorse(@RequestBody HorseRequestDTO horseRequestDTO) {
        HorseResponseDTO createdHorse = horseService.createHorse(horseRequestDTO);
        return new ResponseEntity<>(createdHorse, HttpStatus.CREATED);
    }
}
