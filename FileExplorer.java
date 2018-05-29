

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.awt.Image;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;
import javax.imageio.ImageIO;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
	 


public class FileExplorer {

    public static final String APP_TITLE = "File Explorer-By Tran Van Quy";
    private Desktop desktop;
    private FileSystemView fileSystemView;
    private File currentFile;
    private JPanel gui;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private JTable table;
    private JProgressBar progressBar;
    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;

    private JButton openFile;
    private JButton deleteFile;
    private JButton nenFile;
    private JLabel fileName;
    private JTextField path;
    private JLabel date;
    private JLabel size;
    private JCheckBox readable;
    private JCheckBox writable;
    private JCheckBox executable;
    private JRadioButton isDirectory;
    private JRadioButton isFile;

   
  
   public Container getGui() {
        if (gui==null) {
            gui = new JPanel(new BorderLayout(3,3));
            gui.setBorder(new EmptyBorder(5,5,5,5));

            fileSystemView = FileSystemView.getFileSystemView();
            desktop = Desktop.getDesktop();

            JPanel detailView = new JPanel(new BorderLayout(3,3));
            //fileTableModel = new FileTableModel();

            table = new JTable();
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoCreateRowSorter(true);
            table.setShowVerticalLines(false);

            listSelectionListener = new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent lse) {
                    int row = table.getSelectionModel().getLeadSelectionIndex();
                    setFileDetails( ((FileTableModel)table.getModel()).getFile(row) );
                }
            };
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
            JScrollPane tableScroll = new JScrollPane(table);
            Dimension d = tableScroll.getPreferredSize();
            tableScroll.setPreferredSize(new Dimension((int)d.getWidth(), (int)d.getHeight()/2));
            detailView.add(tableScroll, BorderLayout.CENTER);

            // the File tree
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            treeModel = new DefaultTreeModel(root);

            TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent tse){
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    showChildren(node);
                    setFileDetails((File)node.getUserObject());
                }
             };

             // show the file system roots.
             File[] roots = fileSystemView.getRoots();
             for (File fileSystemRoot : roots) {
                 DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                 root.add( node );
                 //showChildren(node);
                 File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                 for (File file : files) {
                    if (file.isDirectory()) {
                        node.add(new DefaultMutableTreeNode(file));
                    }
                  }
                }

              tree = new JTree(treeModel);
              tree.setRootVisible(false);
              tree.addTreeSelectionListener(treeSelectionListener);
              tree.setCellRenderer(new FileTreeCellRenderer());
              tree.expandRow(0);
              JScrollPane treeScroll = new JScrollPane(tree);

              // as per trashgod tip
              tree.setVisibleRowCount(15);

              Dimension preferredSize = treeScroll.getPreferredSize();
              Dimension widePreferred = new Dimension(200, (int)preferredSize.getHeight());
              treeScroll.setPreferredSize( widePreferred );

           
              // details for a File
              JPanel fileMainDetails = new JPanel(new BorderLayout(4,2));
              fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

              JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
              fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);
 
              JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
              fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

              fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
              fileName = new JLabel();
              fileDetailsValues.add(fileName);
              fileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
              path = new JTextField(5);
              path.setEditable(false);
              fileDetailsValues.add(path);
              fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
              date = new JLabel();
              fileDetailsValues.add(date);
            
              int count = fileDetailsLabels.getComponentCount();
              for (int ii=0; ii<count; ii++) {
                  fileDetailsLabels.getComponent(ii).setEnabled(false);
              }

              JToolBar toolBar = new JToolBar();
              toolBar.setFloatable(false);

  //Cau 2: Thuc hien nen File         
              
              nenFile = new JButton(" Nen File  ");
              nenFile.setMnemonic('n');
              nenFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                	
                	File inputDir = new File(path.getText());// 
   				 	File outputZipFile = new File(path.getText()+"1.zip");
   				 	outputZipFile.getParentFile().mkdirs();
   				 
   				 	String inputDirPath = inputDir.getAbsolutePath();
   				 	byte[] buffer = new byte[1024];
   		 
   				 	FileOutputStream fileOs = null;
   				 	ZipOutputStream zipOs = null;
   				 	try {
   				 		List<File> allFiles = this.listChildFiles(inputDir);
   				 		// Tạo đối tượng ZipOutputStream để ghi file zip.
   				 		fileOs = new FileOutputStream(outputZipFile);
   				 	 
   				 		zipOs = new ZipOutputStream(fileOs);
   				 		for (File file : allFiles) {
   		                String filePath = file.getAbsolutePath();
   		 
   		                System.out.println("Zipping " + filePath);
   		                // entryName: is a relative path.
   		                String entryName = filePath.substring(inputDirPath.length() + 1);
   		 
   		                ZipEntry ze = new ZipEntry(entryName);
   		                // Thêm entry vào file zip.
   		                zipOs.putNextEntry(ze);
   		                // Đọc dữ liệu của file và ghi vào ZipOutputStream.
   		                FileInputStream fileIs = new FileInputStream(filePath);
   		             
   		                int len;   
   		                while ((len = fileIs.read(buffer)) > 0) {
   		                    zipOs.write(buffer, 0, len);
   		                }
   		                fileIs.close();
   		            }
   				 	JOptionPane.showMessageDialog(null,"Successfully!");
   		        } catch (IOException e) {
   		        	
   		            e.printStackTrace();        
   		        } finally {
   		            closeQuite(zipOs);
   		            closeQuite(fileOs);
   		        }
   		    }
   			private List<File> listChildFiles(File dir)throws IOException {
   				
   			        List<File> allFiles = new ArrayList<File>();
   			 
   			        File[] childFiles = dir.listFiles();
   			        for (File file : childFiles) {
   			            if (file.isFile()) {
   			                allFiles.add(file);
   			            } else {
   			                List<File> files = this.listChildFiles(file);
   			                allFiles.addAll(files);
   			            }
   			        }
   			        System.out.println("Successfully!");
   			        return allFiles;	   
   			}
   			private void closeQuite(OutputStream out) {
   		        try {
   		            out.close();
   		        } catch (Exception e) {
   		        }
   		    }
   		 	
   		});  	
              
              toolBar.add(nenFile);

              
              JPanel fileView = new JPanel(new BorderLayout(3,3));
              fileView.add(toolBar,BorderLayout.NORTH);
              
              
   //Delete File        
              deleteFile = new JButton("  Delete  ");
              deleteFile.setMnemonic('d');
              deleteFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                         deleteFile();
                         JOptionPane.showMessageDialog(null,"Successfully!");
                }
               });
              toolBar.add(deleteFile);
              
              
   //Open File                    
              openFile = new JButton("  Open ");
              openFile.setMnemonic('o');
              openFile.addActionListener(new ActionListener(){
                 public void actionPerformed(ActionEvent ae) {
                      try {
                          desktop.open(currentFile);
                           } catch(Throwable t) {
                                 showThrowable(t);
                           }
                           gui.repaint();
                           
                   }
                 
                });
               toolBar.add(openFile);                                
               // Check the actions are supported on this platform!
               openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
                                                                                                                     
               fileView.add(fileMainDetails,BorderLayout.CENTER);
               detailView.add(fileView, BorderLayout.SOUTH);

               JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detailView);
               gui.add(splitPane, BorderLayout.CENTER);

               JPanel simpleOutput = new JPanel(new BorderLayout(3,3));
               progressBar = new JProgressBar();
               simpleOutput.add(progressBar, BorderLayout.EAST);
               progressBar.setVisible(false);
               gui.add(simpleOutput, BorderLayout.SOUTH);
         } //close if(gui==nul).
        return gui;
    } //close  public Container getGui().
   
   
   

    public void showRootFile() {
        // ensure the main files are displayed
        tree.setSelectionInterval(0,0);
    }

    private TreePath findTreePath(File find) {
        for (int ii=0; ii<tree.getRowCount(); ii++) {
            TreePath treePath = tree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)object;
            File nodeFile = (File)node.getUserObject();

            if (nodeFile==find) {
                return treePath;
            }
        }
        // not found!
        return null;
    }

      
//Xoa File
    private void deleteFile() {
        if (currentFile==null) {
            showErrorMessage("No file selected for deletion.","Select File");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
            gui,
            "Are you sure you want to delete this file?",
            "Delete File",
            JOptionPane.ERROR_MESSAGE
            );
        if (result==JOptionPane.OK_OPTION) {
            try {
                System.out.println("currentFile: " + currentFile);
                TreePath parentPath = findTreePath(currentFile.getParentFile());
                System.out.println("parentPath: " + parentPath);
                DefaultMutableTreeNode parentNode =
                    (DefaultMutableTreeNode)parentPath.getLastPathComponent();
                System.out.println("parentNode: " + parentNode);

                boolean directory = currentFile.isDirectory();
                boolean deleted = currentFile.delete();
                if (deleted) {
                    if (directory) {
                        // delete the node..
                        TreePath currentPath = findTreePath(currentFile);
                        System.out.println(currentPath);
                        DefaultMutableTreeNode currentNode =
                            (DefaultMutableTreeNode)currentPath.getLastPathComponent();

                        treeModel.removeNodeFromParent(currentNode);
                    }

                    showChildren(parentNode);
                } else {
                    String msg = "The file '" +
                        currentFile +
                        "' could not be deleted.";
                    showErrorMessage(msg,"Delete Failed");
                }
            } catch(Throwable t) {
                showThrowable(t);
            }
        }
        gui.repaint();
    }

   
    private void showErrorMessage(String errorMessage, String errorTitle) {
        JOptionPane.showMessageDialog(
            gui,
            errorMessage,
            errorTitle,
            JOptionPane.ERROR_MESSAGE
            );
    }

    private void showThrowable(Throwable t) {
        t.printStackTrace();
        JOptionPane.showMessageDialog(
            gui,
            t.toString(),
            t.getMessage(),
            JOptionPane.ERROR_MESSAGE
            );
        gui.repaint();
    }

    /** Update the table on the EDT */
    private void setTableData(final File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (fileTableModel==null) {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
                if (!cellSizesSet) {
                    Icon icon = fileSystemView.getSystemIcon(files[0]);

                    // size adjustment to better account for icons
                    table.setRowHeight( icon.getIconHeight()+rowIconPadding );
                    setColumnWidth(0,-1);
                    setColumnWidth(3,60);
                    table.getColumnModel().getColumn(3).setMaxWidth(120);
                    setColumnWidth(4,-1);
                    setColumnWidth(5,-1);
                    cellSizesSet = true;
                }
            }
        });
    }

    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width<0) {
            // use the preferred width of the header..
            JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int)preferred.getWidth()+14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    /** Add the files that are contained within the directory of this node.
    Thanks to Hovercraft Full Of Eels. */
    private void showChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    /** Update the File details view with the details of this File. */
    private void setFileDetails(File file) {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        path.setText(file.getPath());
        date.setText(new Date(file.lastModified()).toString());
        size.setText(file.length() + " bytes");
        readable.setSelected(file.canRead());
        writable.setSelected(file.canWrite());
        executable.setSelected(file.canExecute());
        isDirectory.setSelected(file.isDirectory());

        isFile.setSelected(file.isFile());

        JFrame f = (JFrame)gui.getTopLevelAncestor();
        if (f!=null) {
            f.setTitle(
                APP_TITLE +
                " :: " +
                fileSystemView.getSystemDisplayName(file) );
        }

        gui.repaint();
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch(Exception weTried) {
                }
                JFrame f = new JFrame(APP_TITLE);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                FileExplorer fileManager = new FileExplorer();
                f.setContentPane(fileManager.getGui());

                try {
                    URL urlBig = fileManager.getClass().getResource("fm-icon-32x32.png");
                    URL urlSmall = fileManager.getClass().getResource("fm-icon-16x16.png");
                    ArrayList<Image> images = new ArrayList<Image>();
                    images.add( ImageIO.read(urlBig) );
                    images.add( ImageIO.read(urlSmall) );
                    f.setIconImages(images);
                } catch(Exception weTried) {}

                f.pack();
                f.setLocationByPlatform(true);
                f.setMinimumSize(f.getSize());
                f.setVisible(true);

                fileManager.showRootFile();
            }
        });
    }
}


class FileTableModel extends AbstractTableModel {

    private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
        "Icon",
        "File",
        "Path/name",
        "Size",
        "Last Modified",
    };

    FileTableModel() {
        this(new File[0]);
    }
    FileTableModel(File[] files) {
        this.files = files;
    }

    public Object getValueAt(int row, int column) {
        File file = files[row];
        switch (column) {
            case 0:
                return fileSystemView.getSystemIcon(file);
            case 1:
                return fileSystemView.getSystemDisplayName(file);
            case 2:
                return file.getPath();
            case 3:
                return file.length();
            case 4:
                return file.lastModified();
            case 5:
                return file.canRead();
            case 6:
                return file.canWrite();
            case 7:
                return file.canExecute();
            case 8:
                return file.isDirectory();
            case 9:
                return file.isFile();
            default:
                System.err.println("Logic Error");
        }
        return "";
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 3:
                return Long.class;
            case 4:
                return Date.class;
            
            case 9:
                return Boolean.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
}


class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    private FileSystemView fileSystemView;
    private JLabel label;
    FileTreeCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
    }

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        File file = (File)node.getUserObject();
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(fileSystemView.getSystemDisplayName(file));
        label.setToolTipText(file.getPath());

        if (selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }
}