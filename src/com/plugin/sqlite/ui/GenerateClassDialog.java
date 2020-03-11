package com.plugin.sqlite.ui;

import com.intellij.diagram.DiagramDataModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.opslab.util.FileUtil;
import com.plugin.sqlite.GenerateClass;
import com.plugin.sqlite.GenerateClassUtil;
import org.apache.velocity.util.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GenerateClassDialog extends JFrame {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList jList;
    private JButton btn_chooser;
    private JLabel jLabelDbFile;
    private JPanel filedPanel;

    private String dbFilePath;
    private String dbFileName;
    private String packageName;
    private String selectPath;
    private VirtualFile file;

    public GenerateClassDialog(String selectPath, VirtualFile file) {
        this.file = file;
        this.selectPath = selectPath;
        this.packageName = selectPath.split("src")[1].
                substring(1).replace(File.separator, ".");
        if (this.packageName.startsWith("main.java.")) {
            this.packageName = this.packageName.replace("main.java.", "");
        }
        setContentPane(contentPane);
        setTitle("Database table selection");
        setSize(400, 400);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 300;
        int height = 400;
        setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);

        initView();
    }

    @SuppressWarnings("unchecked")
    private void initView() {

        jLabelDbFile.setText("choose your db file");

        btn_chooser.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(selectPath);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "db", "db", "sqlite");
            //设置文件类型
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(new JPanel());
            //保存文件从这里入手，输出的是文件名
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dbFile = chooser.getSelectedFile();
                dbFilePath = dbFile.getPath();
                dbFileName = dbFile.getName();
                jLabelDbFile.setText(dbFileName);
                List<String> list = GenerateClassUtil.getTables(dbFilePath);
                jList.setListData(new Vector<>(list));
                System.out.println("Your selected file is: " +
                        chooser.getSelectedFile().getName());
                this.setAlwaysOnTop(true);
            }
        });

        buttonCancel.addActionListener(e -> dispose());

        buttonOK.addActionListener(e -> {
            List<String> selects = jList.getSelectedValuesList();
            GenerateClassUtil.generateBean(dbFilePath, packageName, selectPath, selects);
            file.refresh(true, true);
            dispose();
        });
    }

}
