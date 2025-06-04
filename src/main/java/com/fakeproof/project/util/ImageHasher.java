package com.fakeproof.project.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageHasher {

    // Generates a 64-bit average hash for a given image stream
    public static String getAverageHash(InputStream inputStream) throws Exception {
        BufferedImage img = ImageIO.read(inputStream);

        // Resize to 8x8 grayscale
        BufferedImage resized = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, 8, 8, null);
        g.dispose();

        // Calculate average gray value
        long total = 0;
        int[] pixels = new int[64];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int pixel = resized.getRGB(x, y) & 0xFF;
                pixels[y * 8 + x] = pixel;
                total += pixel;
            }
        }
        int avg = (int)(total / 64);

        // Build hash string based on average
        StringBuilder hash = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            hash.append(pixels[i] >= avg ? "1" : "0");
        }

        return hash.toString();
    }

    // Hamming distance between two hash strings
    public static int hammingDistance(String hash1, String hash2) {
        int dist = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) dist++;
        }
        return dist;
    }

    // Percentage similarity between two hashes (0.0 to 1.0)
    public static double similarity(String hash1, String hash2) {
        int distance = hammingDistance(hash1, hash2);
        return (64 - distance) / 64.0;
    }
}
