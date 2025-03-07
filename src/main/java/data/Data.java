package data;

import com.github.javafaker.Faker;

public class Data {

    // Initialize JavaFaker
    private static final Faker faker = new Faker();

    /**
     * Generates a random email using JavaFaker.
     *
     * @return A random email address.
     */
    public static String generateRandomEmail() {
        return faker.internet().emailAddress(); // Generates a realistic email address
    }

    /**
     * Generates a random password using JavaFaker.
     *
     * @return A random strong password.
     */
    public static String generateRandomPassword() {
        return faker.internet().password(8, 16, true, true, true); // Generates a strong password
    }
}
