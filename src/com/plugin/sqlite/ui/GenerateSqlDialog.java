package com.plugin.sqlite.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static com.plugin.sqlite.util.DButils.getTableNames;
import static com.plugin.sqlite.util.DButils.getTables;

public class GenerateSqlDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton btn_select;
    private JLabel db_file;
    private JButton btn_field_add;
    private JButton btn_main_change;
    private JTree db_tree;
    private JLabel selectLabel;
    private JLabel mainTable;
    private JList<String> joinList;
    private JButton btn_field_del;
    private JButton btn_join_del;
    private JButton btn_join_add;
    private JList<String> fieldList;
    private JComboBox cb_type;
    private Editor editor;
    private Project project;

    private File dbFile;
    private String dbFileName;
    private Vector<String> fieldItems = new Vector<>();
    private Vector<String> joinItems = new Vector<>();
    private String TableMain = "FROM";

    public GenerateSqlDialog(Editor editor, Project project) {
        this.editor = editor;
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        setTitle("Database File Selection");
        setSize(700, 500);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 700;
        int height = 500;
        setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);
        setResizable(false);

        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        btn_main_change.setEnabled(false);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        fieldList.setListData(fieldItems);

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        initView();
        cb_type.addItemListener(e -> {
            switch (e.getItem().toString()) {
                case "Field":
                    btn_field_add.setEnabled(true);
                    btn_join_add.setEnabled(false);
                    btn_main_change.setEnabled(false);
                    break;
                case "Main":
                    btn_field_add.setEnabled(false);
                    btn_join_add.setEnabled(false);
                    btn_main_change.setEnabled(true);
                    break;
                case "Join":
                    btn_field_add.setEnabled(false);
                    btn_join_add.setEnabled(true);
                    btn_main_change.setEnabled(false);
                    break;
            }
        });
    }

    private void initView() {

        db_tree.setModel(null);

        btn_select.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "db", "db", "sqlite");
            //设置文件类型
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(new JPanel());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                dbFile = chooser.getSelectedFile();
                dbFileName = dbFile.getName();
                db_file.setText(dbFileName);
                List<String> list = getTables(dbFile.getPath());
                db_tree.setModel(new DefaultTreeModel(initNodes(list)));
                DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer) db_tree
                        .getCellRenderer();
                cellRenderer.setOpenIcon(null);
                cellRenderer.setClosedIcon(null);
                cellRenderer.setLeafIcon(null);
                db_tree.setCellRenderer(cellRenderer);
                db_tree.addTreeSelectionListener(e1 -> {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                            db_tree.getLastSelectedPathComponent();
                    if (node == null) return;

                    if (node.getLevel() == 1) {
                        TableMain = String.format("FROM %s", node.toString());
                        if (btn_field_add.isEnabled()) {
                            String table = node.toString();
                            Enumeration childs = node.children();
                            //获取该选中节点的子节点
                            while (childs.hasMoreElements()) {
                                String selectItem = childs.nextElement().toString();
                                String item = String.format("%s.%s",
                                        table.toLowerCase(), selectItem);
                                if (!fieldItems.contains(item)) {
                                    fieldItems.add(item);
                                }
                            }
                        }
                    } else if (node.getLevel() == 2) {
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                        String table = parent.toString();
                        if (btn_field_add.isEnabled()) {
                            String item = String.format("%s.%s",
                                    table.toLowerCase(), node.toString());
                            if (!fieldItems.contains(item)) {
                                fieldItems.add(item);
                            }
                        } else if (btn_main_change.isEnabled()) {
                            String item = String.format("%s.%s",
                                    table, node.toString());
                            if (!joinItems.contains(item)) {
                                joinItems.add(item);
                            }
                        }
                    }

                });
                System.out.println("Your selected file is: " +
                        chooser.getSelectedFile().getName());
            }
        });

        btn_field_add.addActionListener(e -> {
            fieldList.setListData(fieldItems);
        });

        btn_field_del.addActionListener(e -> {
            List<String> selectedList = fieldList.getSelectedValuesList();
            fieldItems.removeAll(selectedList);
            fieldList.setListData(fieldItems);
        });

        btn_join_add.addActionListener(e -> {
            int count = joinItems.size();
            if (count > 0 && count % 2 == 0) {
                for (int i = 0; i < count / 2; i++) {
                    String table_name1 = joinItems.get(i).split("\\.")[0];
                    String filed_name1 = joinItems.get(i).split("\\.")[1];
                    String table_name2 = joinItems.get(i + 1).split("\\.")[0];
                    String filed_name2 = joinItems.get(i + 1).split("\\.")[1];
                    joinItems.add(String.format("LEFT JOIN %s %s ON %s = %s",
                            table_name1, table_name1.toLowerCase(),
                            table_name1 + filed_name1,
                            table_name2 + filed_name2));
                }
                joinList.setListData(joinItems);
            } else {
                System.out.println(count);
            }
        });

        btn_join_del.addActionListener(e -> {
            List<String> selectedList = joinList.getSelectedValuesList();
            joinItems.removeAll(selectedList);
            joinList.setListData(joinItems);
        });

        btn_main_change.addActionListener(e -> mainTable.setText(TableMain));
    }

    private void onOK() {
        String field_sql = String.format("%s %s %s %s", selectLabel.getText(),
                StringUtils.join(fieldItems, ","),
                mainTable.getText(), StringUtils.join(joinItems, " "));
        Document document = editor.getDocument();
        Runnable runnable = () -> {
            //genGetterAndSetter为生成getter和setter函数部分
            document.insertString(editor.getCaretModel().getOffset(),
                    String.format("\tpublic String sql = \"%s\";", field_sql));
        };

        //加入任务，由IDEA调度执行这个任务
        WriteCommandAction.runWriteCommandAction(project, runnable);

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private DefaultMutableTreeNode initNodes(List<String> tables) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(dbFileName);
        for (String table : tables) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(table);
            List<String> cols = getTableNames(dbFile.getPath(), table);
            for (String col : cols) {
                node.add(new DefaultMutableTreeNode(col));
            }
            top.add(node);
        }
        return top;
    }

    public static void main(String[] args) {
        GenerateSqlDialog dialog = new GenerateSqlDialog(null, null);
        dialog.pack();
        dialog.setVisible(true);
    }
}
