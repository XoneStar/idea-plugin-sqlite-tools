package com.plugin.sqlite;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.plugin.sqlite.ui.GenerateClassDialog;
import com.plugin.sqlite.ui.Sql2ClassDialog;

/**
 * Created by zhuji on 2018/11/20.
 */
public class Sql2Class extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project mProject = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        if (editor == null)
            return;

        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        //根据是否选择的是目录判定是否进行下面的处理
        if (selectedText != null &&
                (selectedText.contains("SELECT") || selectedText.contains("select"))) {
            //获取选中的文件
            VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
            if (file != null) {
                System.out.println(file.getPresentableUrl());
                System.out.println(file.getParent().getPath());
                Sql2ClassDialog selectDialog = new Sql2ClassDialog(file, selectedText);
                selectDialog.setAlwaysOnTop(true);
                selectDialog.setVisible(true);
            }
        } else {
            Messages.showMessageDialog(mProject, "Your selection is not SQL!", "Tips", Messages.getInformationIcon());
        }
    }
}
