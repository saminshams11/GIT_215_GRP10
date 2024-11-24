import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class TranslationTool {

    private Properties translations;
    private String translationsFile;

    public TranslationTool(String translationsFile) {
        this.translationsFile = translationsFile;
        translations = new Properties();
        try {
            File file = new File(translationsFile);
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    translations.load(reader);
                    System.out.println("Loaded translations from " + translationsFile);
                }
            } else {
                System.out.println("Translations file not found. Creating a new one...");
                if (file.createNewFile()) {
                    System.out.println("New translations file created: " + translationsFile);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading translations file: " + e.getMessage());
        }
    }

    // To convert spaces into underscores and make it lowercase
    public String normalizeInput(String input) {
        return input.trim().toLowerCase().replace(" ", "_");
    }

    // To translate based on the converted input
    public String translate(String text) {
        return translations.getProperty(normalizeInput(text), null);
    }

    // To add a new translation to the file
    public void addNewTranslation(String word, String translation) {
        translations.setProperty(normalizeInput(word), translation);
        try (FileWriter writer = new FileWriter(translationsFile)) {
            translations.store(writer, "Updated translations");
            System.out.println("Translation saved: " + word + " -> " + translation);
        } catch (IOException e) {
            System.out.println("Error saving translation to file: " + e.getMessage());
        }
    }

    // To log the translation request to a file with timestamp
    public void logTranslation(String original, String translated) {
        try (FileWriter logWriter = new FileWriter("translation_log.txt", true)) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            logWriter.write(
                    "Original: " + original + " | Translated: " + translated + " | Timestamp: " + timestamp + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }

    // To handle the translation request
    public void handleTranslationRequest(String text, Scanner scanner) {
        String translated = translate(text);
        if (translated == null) {
            System.out.println("Translation not found for '" + text + "'.");
            System.out.print("Please provide a translation: ");
            String newTranslation = scanner.nextLine();
            addNewTranslation(text, newTranslation);
            translated = newTranslation;
        }
        System.out.println("Translation: " + translated);
        logTranslation(text, translated);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Language selection menu
        System.out.println("Welcome! Please choose a language for translation!");
        System.out.println("1. English to Spanish");
        System.out.println("2. English to French");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String translationsFile;
        switch (choice) {
            case 1:
                translationsFile = "translations_es.properties";
                break;
            case 2:
                translationsFile = "translations_fr.properties";
                break;
            default:
                System.out.println("Invalid choice. Defaulting to English to Spanish.");
                translationsFile = "translations_es.properties";
        }

        TranslationTool tool = new TranslationTool(translationsFile);

        System.out.println("Welcome to the Translation Tool!");
        System.out.println("Enter text to translate (type 'exit' to quit):");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the Translation Tool. Goodbye!");
                break;
            }

            tool.handleTranslationRequest(input, scanner);
        }

        scanner.close();
    }
}
