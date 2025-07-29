package anbrain.qa.rococo.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.Random;
import java.util.List;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomArtistName() {
        return faker.artist().name() + faker.artist().name() + faker.artist().name();
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

    public static List<String> COUNTRY_LIST = List.of("Австралия", "Австрия", "Азербайджан", "Албания", "Алжир",
            "Ангола", "Андорра", "Антигуа и Барбуда", "Аргентина", "Армения", "Афганистан", "Багамские Острова", "Бангладеш",
            "Барбадос", "Бахрейн", "Белиз", "Белоруссия", "Бельгия", "Бенин", "Болгария", "Боливия", "Босния и Герцеговина",
            "Ботсвана", "Бразилия", "Бруней", "Буркина-Фасо", "Бурунди", "Бутан", "Вануату", "Великобритания", "Венгрия",
            "Венесуэла", "Восточный Тимор", "Вьетнам", "Габон", "Республика Гаити", "Гайана", "Гамбия", "Гана", "Гватемала",
            "Гвинея", "Гвинея-Бисау", "Германия", "Гондурас", "Гренада", "Греция", "Грузия", "Дания", "Джибути", "Доминика",
            "Доминиканская Республика", "Египет", "Замбия", "Зимбабве", "Израиль", "Индия", "Индонезия", "Иордания", "Ирак",
            "Иран", "Ирландия", "Исландия", "Испания", "Италия", "Йемен", "Кабо-Верде", "Казахстан", "Камбоджа", "Камерун",
            "Канада", "Катар", "Кения", "Республика Кипр", "Киргизия", "Кирибати", "Китай", "Колумбия", "Коморы", "Республика Конго",
            "Демократическая Республика Конго", "Корейская Народно-Демократическая Республика", "Республика Корея", "Коста-Рика",
            "Кот-д’Ивуар", "Куба", "Кувейт", "Лаос", "Латвия", "Лесото", "Либерия", "Ливан", "Ливия", "Литва", "Лихтенштейн",
            "Люксембург", "Маврикий", "Мавритания", "Мадагаскар", "Малави", "Малайзия", "Мали", "Мальдивы", "Мальта", "Марокко",
            "Маршалловы Острова", "Мексика", "Федеративные Штаты Микронезии", "Мозамбик", "Молдавия", "Монако", "Монголия",
            "Мьянма", "Намибия", "Науру", "Непал", "Нигер", "Нигерия", "Нидерланды", "Никарагуа", "Новая Зеландия", "Норвегия",
            "Объединённые Арабские Эмираты", "Оман", "Пакистан", "Палау", "Панама", "Папуа — Новая Гвинея", "Парагвай", "Перу",
            "Польша", "Португалия", "Россия", "Руанда", "Румыния", "Сальвадор", "Самоа", "Сан-Марино", "Сан-Томе и Принсипи",
            "Саудовская Аравия", "Северная Македония", "Сейшельские Острова", "Сенегал", "Сент-Винсент и Гренадины",
            "Сент-Китс и Невис", "Сент-Люсия", "Сербия", "Сингапур", "Сирия", "Словакия", "Словения", "Соединённые Штаты Америки",
            "Соломоновы Острова", "Сомали", "Судан", "Суринам", "Сьерра-Леоне", "Таджикистан", "Таиланд", "Танзания", "Того",
            "Тонга", "Тринидад и Тобаго", "Тувалу", "Тунис", "Туркменистан", "Турция", "Уганда", "Узбекистан", "Украина",
            "Уругвай", "Фиджи", "Филиппины", "Финляндия", "Франция", "Хорватия", "Центральноафриканская Республика", "Чад",
            "Черногория", "Чехия", "Чили", "Швейцария", "Швеция", "Шри-Ланка", "Эквадор", "Экваториальная Гвинея", "Эритрея",
            "Эсватини", "Эстония", "Эфиопия", "Южно-Африканская Республика", "Южный Судан", "Ямайка", "Япония", "Ватикан", "Палестина");

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

    @Nonnull
    public static File avatarFile() {
        try {
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();

            Random random = new Random();
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            graphics.setColor(color);
            graphics.fillRect(0, 0, 100, 100);

            graphics.dispose();

            File tempFile = File.createTempFile("avatar-", ".jpg");
            ImageIO.write(image, "jpeg", tempFile);

            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации аватара", e);
        }
    }

    public static String randomCountry() {
        return COUNTRY_LIST.get(faker.random().nextInt(197));
    }

}
