package com.nocountry.equitrust.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nocountry.equitrust.controller.dto.horse.HorseMediaResponseDTO;
import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.model.horse.Horse;
import com.nocountry.equitrust.model.horse.HorseMedia;
import com.nocountry.equitrust.model.media.MediaType;
import com.nocountry.equitrust.repository.HorseMediaRepository;
import com.nocountry.equitrust.service.HorseMediaService;
import com.nocountry.equitrust.service.HorseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorseMediaServiceImpl implements HorseMediaService {

    private final Cloudinary cloudinary;
    private final HorseMediaRepository horseMediaRepository;
    private final HorseService horseService;

    @Value("${cloudinary.horse.photos}")
    private String photosPath;

    @Value("${cloudinary.horse.videos}")
    private String videosPath;

    @Override
    @Transactional
    public HorseMediaResponseDTO uploadMedia(Long horseId, MultipartFile file, MediaType mediaType) throws IOException {
        Horse horse = horseService.getHorseEntityById(horseId);

        String resourceType = mediaType == MediaType.VIDEO ? "video" : "image";
        String pathType = resourceType.equals("video") ? videosPath : photosPath;

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", pathType,
                "resource_type", resourceType
        );
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

        String publicId = (String) uploadResult.get("public_id");
        String secureUrl = (String) uploadResult.get("secure_url");

        HorseMedia media = new HorseMedia(publicId, secureUrl, mediaType, horse);
        HorseMedia savedMedia = horseMediaRepository.save(media);

        return HorseMediaResponseDTO.fromHorseMedia(savedMedia);
    }

    @Override
    public List<HorseMediaResponseDTO> getMediaByHorseId(Long horseId) {
        horseService.existsHorseById(horseId);

        return horseMediaRepository.findByHorseId(horseId).stream()
                .map(HorseMediaResponseDTO::fromHorseMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMedia(Long horseId, Long mediaId) throws IOException {
        horseService.existsHorseById(horseId);

        HorseMedia media = horseMediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen/video no encontrado con id: " + mediaId));

        if (!media.getHorse().getId().equals(horseId)) {
            throw new ResourceNotFoundException(
                    "Imagen/video con id " + mediaId + " no pertenece al caballo con id " + horseId);
        }

        String resourceType = media.getMediaType() == MediaType.VIDEO ? "video" : "image";

        Map<String, Object> deleteParams = ObjectUtils.asMap(
                "resource_type", resourceType
        );
        cloudinary.uploader().destroy(media.getPublicId(), deleteParams);

        horseMediaRepository.delete(media);
    }
}
