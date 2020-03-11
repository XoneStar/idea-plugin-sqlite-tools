package com.plugin.sqlite.ui;

import com.intellij.openapi.vfs.VirtualFile;
import com.plugin.sqlite.GenerateClassUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sql2ClassDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton selectButton;
    private JLabel db_label;

    private String dbFilePath;
    private String dbFileName;
    private String selectPath;
    private String packageName;
    private VirtualFile file;
    private String selectedText;

    public Sql2ClassDialog(VirtualFile file, String selectedText) {
        this.file = file.getParent();
        this.selectedText = selectedText.replace("\"", "");
        this.selectPath = this.file.getPath();
        this.packageName = selectPath.split("src")[1].
                substring(1).replace("/", ".");
        setContentPane(contentPane);
        setModal(true);
        setSize(250, 180);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 250;
        int height = 180;
        setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        selectButton.addActionListener(e -> {
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
                db_label.setText(dbFileName);
                System.out.println("Your selected file is: " +
                        chooser.getSelectedFile().getName());
            }
        });
    }

    private void onOK() {
        // add your code here
        GenerateClassUtil.generateBean(dbFilePath, packageName, selectPath, selectedText);
        file.refresh(true, true);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        String line = "SELECT erpuser.UserCode,erpuser.UserName,erpuser.UserPwd,erpuser.Phone,erpuser.GroupCode,erpuser.ZaiGang,temp_tn_repair_daily_plan.REPAIR_CASE_CODE,temp_tn_repair_daily_plan.MONTH_PLAN_CODE,temp_tn_repair_daily_plan.START_WORK_DATE,temp_tn_repair_daily_plan.END_WORK_DATE,temp_tn_repair_daily_plan.FZ_USER_CODE,temp_tn_repair_daily_plan.LL_USER_CODE FROM ERPUser LEFT JOIN TEMP_TN_REPAIR_DAILY_PLAN ON TEMP_TN_REPAIR_DAILY_PLAN.FZ_USER_CODE = ERPUser.UserCode";
        String pattern = ".*(?:FROM|from) (.*?) ";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(line);
        if (m.find()) {
            System.out.println("Found value: " + m.group(1));
        } else {
            System.out.println("NO MATCH");
        }
    }
}
