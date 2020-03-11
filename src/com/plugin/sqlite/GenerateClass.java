package com.plugin.sqlite;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.plugin.sqlite.ui.GenerateClassDialog;
import org.jetbrains.annotations.NotNull;

public class GenerateClass extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project mProject = event.getData(PlatformDataKeys.PROJECT);
        DataContext dataContext = event.getDataContext();
        //根据是否选择的是目录判定是否进行下面的处理
        if (checkIsDir(dataContext)) {
            //获取选中的文件
            VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
            if (file != null) {
                System.out.println(file.getPresentableUrl());
                System.out.println(file.getPath());
                GenerateClassDialog selectDialog = new GenerateClassDialog(file.getPresentableUrl(), file);
                selectDialog.setAlwaysOnTop(true);
                selectDialog.setVisible(true);
            }
        } else {
            Messages.showMessageDialog(mProject, "Your selection is not folder!", "Tips", Messages.getInformationIcon());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        //在Action显示之前,根据选中是否是判定是否显示此Action
        //boolean isDir = checkIsDir(event.getDataContext());
        //this.getTemplatePresentation().setEnabled(isDir);
    }

    // 判断所选类型是否是目录
    private static boolean checkIsDir(DataContext dataContext) {
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(dataContext);
        return file != null && file.isDirectory();
    }

}
