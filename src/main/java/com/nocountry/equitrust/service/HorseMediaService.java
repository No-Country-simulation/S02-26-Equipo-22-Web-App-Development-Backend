package com.nocountry.equitrust.service;

import com.nocountry.equitrust.controller.dto.horse.HorseMediaResponseDTO;
import com.nocountry.equitrust.model.media.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HorseMediaService {

    HorseMediaResponseDTO uploadMedia(Long horseId, MultipartFile file, MediaType mediaType) throws IOException;

    List<HorseMediaResponseDTO> getMediaByHorseId(Long horseId);

    void deleteMedia(Long horseId, Long mediaId) throws IOException;
}
