package dev.namelessgroup.regexvisualizerintellijplatform.ui.hoverwindow;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.namelessgroup.regexvisualizerintellijplatform.controller.RegexUtilites;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an element from the code that can be hovered over
 */
class CodeElement {
    private static final String STRING_REGEX = "(\"(.*)\")";
    @Nullable
    private final PsiElement element;
    @Nullable
    private String elementText;


    /**
     * Creates a new CodeElement
     *
     * @param file The PsiFile to get the element from
     * @param offset The offset gathered from the hovered position
     */
    public CodeElement(@Nullable PsiFile file, int offset) {
        if (file == null) {
            element = null;
            return;
        }

        element = file.findElementAt(offset);
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
        }
        return elementText.matches(STRING_REGEX) && RegexUtilites.isRegex(elementText);
    }

    /**
     * Gets the contents of the string hovered over
     *
     * @return Elements or its references String contents
     */
    @Nullable
    public String getStringContents() {
        if (elementText != null) {
            return elementText.substring(1, elementText.length() - 1);
        }
        return null;
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

    @Override
    public String toString() {
        if (element == null) {
            return "null";
        }
        return element.getText();
    }
}
