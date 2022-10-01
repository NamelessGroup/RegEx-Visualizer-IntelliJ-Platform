package dev.namelessgroup.regexvisualizerintellijplatform.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * {@link #runActivity(Project project) run method} of this class gets before/during the indexing process <br>
 * Only DumbAware parts of the system can be called in this class
 */
public class PreIndexingLoader implements StartupActivity, DumbAware {
    @Override
    public void runActivity(@NotNull Project project) {}
}
