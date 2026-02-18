package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.dto.request.HorseRequestDTO;
import com.nocountry.equitrust.dto.response.HorseResponseDTO;
import com.nocountry.equitrust.service.HorseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<HorseResponseDTO> getHorseById(@PathVariable Long id) {
        HorseResponseDTO horse = horseService.getHorseById(id);
        return ResponseEntity.ok(horse);
    }

    @GetMapping
    public ResponseEntity<List<HorseResponseDTO>> getAllHorses() {
        List<HorseResponseDTO> horses = horseService.getAllHorses();
        return ResponseEntity.ok(horses);
    }
}
