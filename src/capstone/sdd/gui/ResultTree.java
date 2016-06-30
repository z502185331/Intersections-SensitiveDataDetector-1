package capstone.sdd.gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lieyongzou on 6/19/16.
 * A panel contains all the result with specific sensitive data.
 * Results are categorized by type of PII and grouped by the content of PII
 * For example:
 * - SSN
 * -- 123-45-678
 * ----/user/ssn.txt
 * ----/user/ssn.pdf
 */
public class ResultTree {

    // Name - root node of results
    private Map<String, DefaultMutableTreeNode> result_node_dict = new HashMap<>();
    private Map<String, List<List<String>>> detailed_result_dict = new HashMap<>();

    private JTree tree;
    private DefaultTreeModel model;
    private GuiListener listener;

    public ResultTree(String type, GuiListener listener) {

        this.listener = listener;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(type);
        tree = new JTree(root);
        tree.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        model = (DefaultTreeModel)tree.getModel();

        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());

                // If user double click the sensitive data, shows the detailed information
                if (e.getClickCount() == 1) {
                    Object[] nodes = treePath.getPath();

                    if (nodes.length == 2) {
                        String data = nodes[1].toString();
                        listener.displayDataInfo(type, data, detailed_result_dict.get(data));
                    }

                    else if (nodes.length == 3) {
                        String path = nodes[2].toString();
                        listener.displayFileInfo(new File(path));
                    }

                }
            }
        });

    }

    /**
     * A method to add a category of sensitive data in result panel
     * @param data the data itself
     * @param context the context around data
     * @param file the path to the file
     */
    public void addResult(String data, String context, File file) {

        if (!result_node_dict.containsKey(data)) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
            result_node_dict.put(data, node);
            ((DefaultMutableTreeNode)model.getRoot()).add(node);

            detailed_result_dict.put(data, new ArrayList<>(2));
        }

        DefaultMutableTreeNode node = result_node_dict.get(data);
        DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(file.getAbsolutePath());
        node.add(new_node);
        model.reload();

        List<String> list = new ArrayList<>();
        list.add(context);
        list.add(file.getAbsolutePath());
        detailed_result_dict.get(data).add(list);

    }


    public JTree getTree() {
        return tree;
    }


}