package com.fakeproof.project.service;


import com.fakeproof.project.exception.ImageNotRegisteredException;
import com.fakeproof.project.exception.ImageTamperedException;
import com.fakeproof.project.model.ImageRecord;
import com.fakeproof.project.repository.ImageRepository;
import com.fakeproof.project.util.ImageHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository repo;

    public ImageRecord registerImage(MultipartFile file) throws Exception {
        byte[] shaHash = generateImageHash(file);
        String visualHash = ImageHasher.getAverageHash(file.getInputStream());

        // Check if exact SHA hash exists
        Optional<ImageRecord> existing = repo.findByHash(shaHash);
        if (existing.isPresent()) return existing.get();

        // Check if visually similar image already exists (≥ 70% match)
        String prefix = visualHash.substring(0, 10); // first 10 bits
        List<ImageRecord> candidates = repo.findVisuallySimilarCandidates(prefix);
        for (ImageRecord img : candidates) {
            if (img.getVisualHash() != null) {
                double similarity = ImageHasher.similarity(visualHash, img.getVisualHash());
                if (similarity >= 0.7) {
                    throw new RuntimeException("Visually similar image already registered.");
                }
            }
        }

        // Process new image
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

        ImageRecord record = new ImageRecord();
        record.setHash(shaHash);
        record.setVisualHash(visualHash);
        record.setOriginalName(file.getOriginalFilename());
        record.setFormat("image/png");
        record.setWidth(bufferedImage.getWidth());
        record.setHeight(bufferedImage.getHeight());
        record.setSizeInBytes(file.getSize());
        record.setCreatedAt(LocalDateTime.now());

        return repo.save(record);
    }

    public boolean validateImage(MultipartFile file) throws Exception {
        byte[] hash = generateImageHash(file);
        Optional<ImageRecord> match = repo.findByHash(hash);

        if (match.isPresent()) return true;

        if (!repo.findAll().isEmpty()) throw new ImageTamperedException("Image has been tampered!");
        throw new ImageNotRegisteredException("Image is not registered!");
    }

    private byte[] generateImageHash(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BufferedImage img = ImageIO.read(file.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return digest.digest(baos.toByteArray());
    }
}
