package dev.namelessgroup.regexvisualizerintellijplatform.ui;

import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.hoverwindow.RegExHoverListener;
import org.jetbrains.annotations.NotNull;

/**
 * {@link #runActivity(Project project) run method} of this class gets called after indexing process is done <br>
 * Non-DumbAware parts of the system can be called in this class
 */
public class PostIndexingLoader implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        EditorFactory.getInstance().getEventMulticaster().addEditorMouseMotionListener(RegExHoverListener.getInstance(), project);
    }
}
