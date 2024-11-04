import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslationTool {

    private Properties translations;

    public TranslationTool(String translationsFile) {
        translations = new Properties();
        try {
            File file = new File(translationsFile);
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    translations.load(reader);
                }
            } else {
                // Try to load from resources if file not found in the directory
                try (InputStream input = getClass().getClassLoader().getResourceAsStream(translationsFile)) {
                    if (input != null) {
                        translations.load(input);
                    } else {
                        System.out.println("Translations file not found: " + translationsFile);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading translations file: " + e.getMessage());
        }
    }

    public String translate(String text) {
        return translations.getProperty(text.toLowerCase(), "Translation not found.");
    }

    public void logTranslation(String original, String translated) {
        try (FileWriter logWriter = new FileWriter("translation_log.txt", true)) {
            logWriter.write("Original: " + original + " | Translated: " + translated + " | Timestamp: " + System.currentTimeMillis() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }

    public void handleTranslationRequest(String text) {
        String translated = translate(text);
        System.out.println("Translation: " + translated);
        logTranslation(text, translated);
    }

    public static void main(String[] args) {
        // Specify the translations file path
        String translationsFile = "translations.properties";

        TranslationTool tool = new TranslationTool(translationsFile);

        // ExecutorService to manage threads for handling translation requests
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Enter text to translate (type 'exit' to quit):");
        while (true) {
            input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String finalInput = input;
            executor.submit(() -> tool.handleTranslationRequest(finalInput));
        }

        executor.shutdown();
        scanner.close();
    }
}
