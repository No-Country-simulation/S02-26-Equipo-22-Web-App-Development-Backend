package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.dto.VeterinaryRecordRequestDTO;
import com.nocountry.equitrust.dto.VeterinaryRecordResponseDTO;
import com.nocountry.equitrust.service.VeterinaryRecordService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veterinary-records")
public class VeterinaryRecordController {

    private final VeterinaryRecordService recordService;

    public VeterinaryRecordController(VeterinaryRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/horse/{horseId}")
    public VeterinaryRecordResponseDTO createRecord(
            @PathVariable Long horseId,
            @Valid @RequestBody VeterinaryRecordRequestDTO dto) {

        return recordService.createRecord(horseId, dto);
    }
}
