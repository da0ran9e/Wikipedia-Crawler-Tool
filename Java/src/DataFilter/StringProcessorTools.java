package DataFilter;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringProcessorTools {
        /**
     *This function replaces spaces in a string with underscores
     * @param input - string to be processed
     *               The input string is a string containing spaces
     * @return output - processed string
     */
    public static String Space2Underscores(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ' ') {
                sb.append('_');
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
    public static String Underscore2Space(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '_') {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
    public static String removeUnnecessarySpaces(String input) {
        input = input.trim();
        input = input.replaceAll("\\s+", " ");

        return input;
    }
    public static String removeDiacritics(String text) {
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String removedSignsText = pattern.matcher(normalizedText).replaceAll("");
        return removedSignsText;
    }
    public static String sanitizeFileName(String fileName) {
        // Define a set of invalid characters not allowed in file names
        String invalidChars = "\\/:*?\"<>|";

        // Replace each invalid character with an empty string
        for (char c : invalidChars.toCharArray()) {
            fileName = fileName.replace(c, '_');
        }

        return fileName;
    }
}
