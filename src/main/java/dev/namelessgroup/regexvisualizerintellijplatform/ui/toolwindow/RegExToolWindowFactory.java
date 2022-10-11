package dev.namelessgroup.regexvisualizerintellijplatform.ui.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RegExToolWindowFactory implements ToolWindowFactory, DumbAware {
    private ContentManager contentManager;
    private ToolWindow toolWindow;
    private static RegExToolWindowFactory instance;

    public RegExToolWindowFactory() {
        instance = this;
    }

    public static RegExToolWindowFactory getInstance() {
        if (instance == null) {
            instance = new RegExToolWindowFactory();
        }
        return instance;
    }


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        contentManager = toolWindow.getContentManager();
        this.toolWindow = toolWindow;

        defaultSetUp();
        contentManager.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void contentAdded(@NotNull ContentManagerEvent event) {
                onTabCreated(event);
            }
            
            @Override
            public void contentRemoved(@NotNull ContentManagerEvent event) {
                onTabClosed(event);
            }
            
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                onTabSwitched(event);
            }
        });
    }
    
    private void onTabCreated(@NotNull ContentManagerEvent event) {}
    
    private void onTabClosed(@NotNull ContentManagerEvent event) {
        if (contentManager.getContents().length == 1) {
            toolWindow.hide();
            contentManager.removeAllContents(true);
            createAddButton();
        }
    }
    
    private void onTabSwitched(@NotNull ContentManagerEvent event) {
        if (event.getContent().getComponent() instanceof PlusButtonTab) {
            plusClicked();
        }
    }

    private void defaultSetUp() {
        contentManager.addContent(createNewTab(new RegExToolWindowTab("RegEx")));
        createAddButton();
    }

    private Content createNewTab(RegExToolWindowTab tab) {
        Content content = contentManager.getFactory().createContent(tab, tab.getName(), false);
        content.setCloseable(true);
        return content;
    }


    private void createAddButton() {
        Content plus = contentManager.getFactory().createContent(new PlusButtonTab(), "+", false);
        plus.setCloseable(false);
        plus.setPinnable(false);
        plus.setPinned(true);
        contentManager.addContent(plus);
    }

    private void plusClicked() {
        Content tab = contentManager.getContent(contentManager.getContents().length - 1);
        assert tab != null;
        RegExToolWindowTab newTab = new RegExToolWindowTab("RegEx");
        tab.setComponent(newTab);
        tab.setCloseable(true);
        tab.setDisplayName(newTab.getName());
        createAddButton();
    }

    public void insertTab(RegExToolWindowTab tab) {
        contentManager.addContent(createNewTab(tab), contentManager.getContents().length - 1);
        contentManager.setSelectedContent(contentManager.getContent(contentManager.getContentCount() - 2));
        toolWindow.show();
    }

    private static class PlusButtonTab extends JPanel { }
}
