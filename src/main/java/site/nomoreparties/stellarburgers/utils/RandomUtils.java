package site.nomoreparties.stellarburgers.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomUtils {
    private static final String cyrillicCharacters = "абвгдежзийклмнопрстуфхцчшщъыьэюя";

    public static String randomPassword(int length) {
        String letters = RandomStringUtils.randomAlphabetic(length);
        String specials = RandomStringUtils.randomAscii(length);
        String allChars = letters + specials;
        return RandomStringUtils.random(length, allChars);
    }

    public static String randomEmail(int length) {
        return RandomStringUtils.randomAlphanumeric(length).toLowerCase() + "@yandex.ru";
    }

    public static String cyrillic(int length) {
        return RandomStringUtils.random(length, cyrillicCharacters);
    }

    public static int randomNumber(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }
}
