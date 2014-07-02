/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.MSGViewer;

import at.redeye.FrameWork.Plugin.AboutPlugins;
import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import static at.redeye.FrameWork.base.BaseDialog.logger;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.prm.impl.gui.LocalConfig;
import at.redeye.FrameWork.utilities.StringUtils;
import com.auxilii.msgparser.Message;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sourceforge.MSGViewer.MSGNavigator.MSGNavigator;

/**
 *
 * @author martin
 */
public class SingleWin extends BaseDialog implements MainDialog {

    private static String last_path = null;   
    private String dialog_id;    
    
    /**
     * Creates new form SingleWin
     */
    public SingleWin(Root root, final String file_name ) 
    {
        super(root, file_name != null ? (root.MlM(root.getAppTitle()) + ": " + file_name) : root.getAppTitle() );
        initComponents();
        
        last_path = root.getSetup().getLocalConfig("LastPath","");
        
        viewerPanel.setRoot(root, this);
        
        if( file_name != null )
        {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    
                    if (file_name.toLowerCase().endsWith(".msg")) {
                        jMNav.setEnabled(true);
                    } else {
                        jMNav.setEnabled(false);
                    }                    
                    
                    viewerPanel.parse(file_name);
                }
            });
        }
        
        
        registerActionKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_N,0), new Runnable() {

            @Override
            public void run() {
                if( jMNav.isEnabled() )
                    jMNavActionPerformed(null);
            }
        });           
    }

    @Override
    public String getUniqueDialogIdentifier(Object requester)
    {                                              
        /*
         * dadurch können wir später den Titel ändern, ohne das sich dadurch
         * die Dialog ID verändert.
         */
        if( dialog_id == null )
         dialog_id = super.getUniqueDialogIdentifier(requester);

        return dialog_id;
    }    
    
    void cleanUp()
    {
    
    }

    @Override
    public void close()
    {
        cleanUp();
        
        if( last_path != null )
            root.getSetup().setLocalConfig("LastPath", last_path);


        super.close();
    }    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewerPanel = new net.sourceforge.MSGViewer.ViewerPanel();
        menubar = new javax.swing.JMenuBar();
        jMFileOpen = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMFileSave = new javax.swing.JMenuItem();
        jMOptions = new javax.swing.JMenuItem();
        jMQuit = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMNav = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jMFileOpen.setText("Program");

        jMenuItem1.setText("File Open ...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMFileOpen.add(jMenuItem1);

        jMFileSave.setText("Save File as ...");
        jMFileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMFileSaveActionPerformed(evt);
            }
        });
        jMFileOpen.add(jMFileSave);

        jMOptions.setText("Options");
        jMOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMOptionsActionPerformed(evt);
            }
        });
        jMFileOpen.add(jMOptions);

        jMQuit.setText("Quit");
        jMQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMQuitActionPerformed(evt);
            }
        });
        jMFileOpen.add(jMQuit);

        menubar.add(jMFileOpen);

        jMenu4.setText("Info");

        jMenuItem5.setText("Details");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMNav.setText("MSG Navigator");
        jMNav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMNavActionPerformed(evt);
            }
        });
        jMenu4.add(jMNav);
        jMenu4.add(jSeparator1);

        jMenuItem7.setText("About");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem7);

        jMenuItem8.setText("Changelog");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenuItem9.setText("Plugins");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem9);

        menubar.add(jMenu4);

        setJMenuBar(menubar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(viewerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(viewerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMQuitActionPerformed
        
        close();
        
    }//GEN-LAST:event_jMQuitActionPerformed

    private void jMOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMOptionsActionPerformed
         invokeDialogUnique(new LocalConfig(root));  
    }//GEN-LAST:event_jMOptionsActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        JFileChooser fc = new JFileChooser();
        
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new MSGFileFilter(root));
        fc.setMultiSelectionEnabled(true);
        
        logger.info("last path: " + last_path);
        if (last_path != null) {
            fc.setCurrentDirectory(new File(last_path));
        }
        int retval = fc.showOpenDialog(this);
        if (retval != 0) {
            return;
        }
        final File[] files = fc.getSelectedFiles();
        for (File file : files) {
            loadMessage(file.getPath());
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMFileSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMFileSaveActionPerformed
        
        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        final FileFilter msg_filter = new FileNameExtensionFilter(MlM("Outlook *.msg File"), "msg");
        final FileFilter mbox_filter = new FileNameExtensionFilter(MlM("Unix *.mbox File"), "mbox");
        final FileFilter eml_filter = new FileNameExtensionFilter(MlM("Thunderbird *.eml File"), "eml");
        fc.addChoosableFileFilter(msg_filter);
        fc.addChoosableFileFilter(mbox_filter);
        fc.addChoosableFileFilter(eml_filter);
        fc.setMultiSelectionEnabled(false);
        if (last_path != null) {
            fc.setCurrentDirectory(new File(last_path));
        }
        int retval = fc.showSaveDialog(this);
        if (retval != 0) {
            return;
        }
        final File file = fc.getSelectedFile();
        new AutoMBox(this.getClass().getName()) {
            @Override
            public void do_stuff() throws Exception {
                File export_file = file;
                if (!file.getName().toLowerCase().endsWith(".msg") && !file.getName().toLowerCase().endsWith(".eml") && !file.getName().toLowerCase().endsWith(".mbox")) {
                    if (fc.getFileFilter() == msg_filter) {
                        export_file = new File(file.getAbsolutePath() + ".msg");
                    } else if (fc.getFileFilter() == eml_filter) {
                        export_file = new File(file.getAbsolutePath() + ".eml");
                    } else {
                        export_file = new File(file.getAbsolutePath() + ".mbox");
                    }
                }
                viewerPanel.exportFile(export_file);
            }
        };
        
    }//GEN-LAST:event_jMFileSaveActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        
        invokeDialogUnique(new About(root));
        
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        
        invokeDialogUnique(new LocalHelpWin(root, "ChangeLog"));
        
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        
        invokeDialogUnique(new AboutPlugins(root));
        
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
            
        if( viewerPanel.getMessage() != null)
             invokeDialogUnique(new Internals(root, viewerPanel.getMessage()));
            
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMNavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMNavActionPerformed
        
        invokeDialogUnique(new MSGNavigator(root, new File(viewerPanel.getFileName())));
        
    }//GEN-LAST:event_jMNavActionPerformed
    
    
    void loadMessage(String file_name)
    {
        logger.info("filename: " + file_name);

        if( file_name.startsWith("file://") )
        {
            try
            {
                file_name = URLDecoder.decode(file_name,"UTF-8");
                file_name = file_name.substring(7);

            } catch( UnsupportedEncodingException ex ) {
                logger.error(StringUtils.exceptionToString(ex));
                file_name = file_name.substring(7);
            } 
        }
        
        if( file_name.toLowerCase().endsWith(".msg") )
        {
            jMNav.setEnabled(true);
        } else {
            jMNav.setEnabled(false);
        }           
        
        if( viewerPanel.getMessage() == null )
        {
            viewerPanel.parse(file_name);
        }
        else
        {
            SingleWin win = new SingleWin( root, file_name );
            
            if( !menubar.isVisible() )
                win.hideMenuBar();
            
            invokeMainDialog( win );
        }
    }
    
    @Override
    public void hideMenuBar() {
       menubar.setVisible(false);
    }    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMFileOpen;
    private javax.swing.JMenuItem jMFileSave;
    private javax.swing.JMenuItem jMNav;
    private javax.swing.JMenuItem jMOptions;
    private javax.swing.JMenuItem jMQuit;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuBar menubar;
    private net.sourceforge.MSGViewer.ViewerPanel viewerPanel;
    // End of variables declaration//GEN-END:variables
}
