package dev.namelessgroup.regexvisualizerintellijplatform.ui.hoverwindow;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseEventArea;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.awt.RelativePoint;
import dev.namelessgroup.regexvisualizerintellijplatform.controller.RegexParser;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.RegExImageFactory;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.settings.RegExSettingsState;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Hover listener for editors
 */
public class RegExHoverListener implements EditorMouseMotionListener {

    private static RegExHoverListener INSTANCE;
    private CodeElement hoveredElement;
    private JBPopup popup;
    private static RegExSettingsState settingsState;

    public static RegExHoverListener getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RegExHoverListener();
            settingsState = RegExSettingsState.getInstance();
        }
        return INSTANCE;
    }

    @Override
    public void mouseMoved(@NotNull EditorMouseEvent e) {
        if (!settingsState.isShowPopUpOnRegEx()) {
            return;
        }
        if (!e.isOverText() || e.getArea() != EditorMouseEventArea.EDITING_AREA) {
            hidePopUp();
            return;
        }
        Editor editor = e.getEditor();

        Project project = editor.getProject();
        if (project == null) {
            hidePopUp();
            return;
        }
        MouseEvent mouseEvent = e.getMouseEvent();
        int offset = editor.logicalPositionToOffset(
                editor.xyToLogicalPosition(
                        mouseEvent.getPoint()
                )
        );

        // Get hovered over code
        CodeElement newHovered = new CodeElement(
                PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument()),
                offset
        );
        // if hovered over a same element as previously
        if (newHovered.equals(hoveredElement)) {
            return;
        }
        hoveredElement = newHovered;

        hidePopUp();

        if (!hoveredElement.isRegEx()) {
            hidePopUp();
            return;
        }

        String hoveredString = hoveredElement.getStringContents();
        Image img = RegExImageFactory.createImage(new RegexParser(hoveredString).buildRegexNodes());
        RelativePoint point = getDisplayPosition(editor, hoveredElement, settingsState.isShowPopUpAboveCode(), img.getHeight(null));

        // Create popup
        popup = new RegExPopUpWindowBuilder(new RegExPopUpWindow(img))
                .createPopup();

        // Show popup
        popup.show(point);
    }

    private void hidePopUp() {
        if (popup != null) {
            popup.cancel();
            popup = null;
        }
    }

    private RelativePoint getDisplayPosition(Editor editor, CodeElement element, boolean aboveLine, int h) {
        RelativePoint displayPoint = new RelativePoint(
                editor.getContentComponent(),
                editor.offsetToXY(element.getOffset())
        );

        // Adjust so it is displayed over the text
        displayPoint.getPoint().y += (aboveLine ? -Math.min(h, editor.getContentComponent().getHeight()) - editor.getAscent() : editor.getLineHeight());
        return displayPoint;
    }
}
