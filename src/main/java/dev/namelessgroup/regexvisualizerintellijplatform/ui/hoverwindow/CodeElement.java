package dev.namelessgroup.regexvisualizerintellijplatform.ui.hoverwindow;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.namelessgroup.regexvisualizerintellijplatform.controller.RegexUtilites;
import dev.namelessgroup.regexvisualizerintellijplatform.model.RegexLanguage;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents an element from the code that can be hovered over
 */
class CodeElement {
    private final Pattern stringRegEx;
    @Nullable
    private final PsiElement element;
    @Nullable
    private String elementText;
    private final RegexLanguage language;


    /**
     * Creates a new CodeElement
     */
    CodeElement(@Nullable PsiFile file, int offset) {
        if (file == null) {
            element = null;
            stringRegEx = null;
            language = null;
            return;
        }
        this.element = file.findElementAt(offset);
        if (element == null) {
            stringRegEx = null;
            language = null;
            return;
        }
        language = RegexLanguage.getLanguage(element.getLanguage());
        String stringBeginning = getStringBeginning(language);
        stringRegEx = Pattern.compile(stringBeginning + "(.*)" + stringBeginning);
    }

    /**
     * Gets the start offset of the element
     *
     * @return The offset of the element
     */
    public int getOffset() {
        if (element == null) {
            return -1;
        }
        return element.getTextOffset();
    }

    /**
     * Checks if the directly hovered over element is a string
     *
     * @return True if the element is a string, false otherwise
     */
    public boolean isRegEx() {
        if (element == null) {
            return false;
        }
        if (elementText == null) {
            elementText = element.getText();
            Matcher m = stringRegEx.matcher(elementText);
            if (m.matches()) {
                elementText = m.group(1);
            } else {
                return false;
            }
        }
        return RegexUtilites.isRegex(elementText);
    }

    /**
     * Gets the contents of the string hovered over
     *
     * @return Elements or its references String contents
     */
    @Nullable
    public String getStringContents() {
        return elementText;
    }

    public RegexLanguage getLanguage() {
        return language;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CodeElement) {
            CodeElement other = (CodeElement) obj;
            if (element == null) {
                return other.element == null;
            }
            if (other.element == null) {
                return false;
            }
            return other.element.getText().equals(element.getText());
        }
        return false;
    }

    private static String getStringBeginning(@Nullable RegexLanguage language) {
        if (language == null) {
            return "";
        }
        switch (language) {
            case JAVA:
                return "\"";
            default:
                return "\"";
        }
    }

}
