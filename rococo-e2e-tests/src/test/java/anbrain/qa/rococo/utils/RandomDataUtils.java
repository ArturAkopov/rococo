package anbrain.qa.rococo.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomArtistName() {
        return faker.artist().name()+faker.artist().name()+faker.artist().name();
    }

    public static String randomMuseumTitle() {
        return faker.gameOfThrones().house();
    }

    public static String randomPaintingTitle() {
        return faker.gameOfThrones().character();
    }

    public static String randomCity() {
        return faker.gameOfThrones().city();
    }

    public static String randomString() {
        return faker.random().hex(50);
    }

    public static String randomFirstName() {
        return faker.name().firstName();
    }

    public static String randomLastName() {
        return faker.name().lastName();
    }

    @Nonnull
    public static String avatar() {
        try {

            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();

            Random random = new Random();
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            graphics.setColor(color);
            graphics.fillRect(0, 0, 100, 100);

            graphics.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", baos);
            byte[] imageBytes = baos.toByteArray();

            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации аватара", e);
        }
    }

}
