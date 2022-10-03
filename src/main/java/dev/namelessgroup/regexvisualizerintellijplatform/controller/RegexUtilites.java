package dev.namelessgroup.regexvisualizerintellijplatform.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class RegexUtilites {
    
    private RegexUtilites() {}

    /**
     * Returns a boolean value indicating if the input string is a valid regex
     * @param regex String to verify
     * @return true, if the input is a valid regular expression, false otherwise
     */
    public static boolean isRegex(String regex) {
        try {
            Pattern pattern = Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * Checks whether the input string matches the given regex with additional flags
     * @param regex Regular expression to check against
     * @param input Input string
     * @param regexFlags Match flags
     * @return true, if the input string matches the given regular expression, false otherwise
     */
    public static boolean matchesRegex(String regex, String input, int regexFlags) {
        Pattern pattern = Pattern.compile(regex, regexFlags);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    /**
     * Checks whether the input string matches the given regex
     * @param regex Regular expression to check against
     * @param input Input string
     * @return true, if the input string matches the given regular expression, false otherwise
     */
    public static boolean matchesRegex(String regex, String input) {
        return RegexUtilites.matchesRegex(regex, input, 0);
    }

}
