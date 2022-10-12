package dev.namelessgroup.regexvisualizerintellijplatform.controller;

import dev.namelessgroup.regexvisualizerintellijplatform.controller.parsers.JavaRegexParser;
import dev.namelessgroup.regexvisualizerintellijplatform.model.RegexLanguage;

/**
 * Utiltiy Factory for regex parsers.
 * Used to return the correct language parser for different programming languages.
 */
public class RegexParserFactory {

    private RegexParserFactory() {}

    /**
     * Returns a RegexParser built for the corresponding language.
     * @param language Programming language of the regex
     * @param regex Regex to supply to the parser
     * @return RegexParser of the corresponding language, with the supplied regex.
     */
    public static RegexParser getParser(RegexLanguage language, String regex) {
        switch (language) {
            case JAVA:
                return new JavaRegexParser(regex);
            default:
                throw new IllegalArgumentException("Invalid regex language");
        }
    }

}