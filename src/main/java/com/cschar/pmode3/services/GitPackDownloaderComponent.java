package com.cschar.pmode3.services;

import com.cschar.pmode3.MenuConfigurableUI;
import com.cschar.pmode3.ParticleContainerManager;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.PowerMode3ConfigurableUI2;
import com.cschar.pmode3.config.DrosteConfig;
import com.cschar.pmode3.config.LizardConfig;
import com.cschar.pmode3.config.LockedLayerConfig;
import com.cschar.pmode3.config.TapAnimConfig;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;

public class GitPackDownloaderComponent extends JPanel{
    private static final Logger LOGGER = Logger.getLogger(GitPackDownloaderComponent.class.getName());

    public static JLabel statusLabel = new JLabel();


    public JComponent getPackRow(String title, String manifestPath){

        JPanel packsCol = new JPanel();
        packsCol.setLayout(new BoxLayout(packsCol, BoxLayout.X_AXIS));
        packsCol.setBackground(JBColor.red);
//        packsCol.setBorder(JBUI.Borders.emptyBottom(30));
        packsCol.setBorder(JBUI.Borders.empty(10,10,10,30));

        JButton button1 = new JButton();
        button1.setText("Load pack");
        button1.setSize(200,50);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked");
                button1.setSize(button1.getWidth()+10, 50);


                Task.Modal modalTask = new Task.Modal(null, "Modal cancelable task", true) {
                    public void run(@NotNull() final ProgressIndicator indicator) {
                        //TODO save state beforehand to rollback if cancelled
                        indicator.setText2("loading assets...");

                        try {
                            menuConfigurable.loadConfigPack(manifestPath, indicator);
                        } catch (FileNotFoundException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {
                        LOGGER.info("cancelling pack load...");
                        //TODO rollback changes to a saved state at start of run() above
                        super.onCancel();
                    }
                };

                ApplicationManager.getApplication().invokeLater(() -> ProgressManager.getInstance().run(modalTask));

            }
        });
        packsCol.add(button1);

        //Add Memory header panel
        JPanel packInfoPanel = new JPanel();
        packInfoPanel.setLayout(new BoxLayout(packInfoPanel, BoxLayout.Y_AXIS));
        packInfoPanel.setBackground(JBColor.CYAN);
//        packInfoPanel.setBorder(JBUI.Borders.empty(30));
        packInfoPanel.setBorder(JBUI.Borders.empty(5));

        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        JLabel headerSizeLabel = new JLabel();
        headerSizeLabel.setText("A size Label");
        headerSizeLabel.setBackground(JBColor.GREEN);
        headerSizeLabel.setOpaque(true);
        headerSizeLabel.setBorder(JBUI.Borders.empty(5));
        headerPanel.add(headerSizeLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(500,100));
        packInfoPanel.add(headerPanel);

        packsCol.add(packInfoPanel);


        return packsCol;
    }



    private JPanel packsList;

    PowerMode3ConfigurableUI2 menuConfigurable;

    public GitPackDownloaderComponent(String title, PowerMode3ConfigurableUI2 menuConfigurable){

        this.menuConfigurable = menuConfigurable;

        this.setMaximumSize(new Dimension(1000,500));

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));



        JPanel gitdownloadPanel = new JPanel();
        gitdownloadPanel.setLayout(new BoxLayout(gitdownloadPanel, BoxLayout.Y_AXIS));
        gitdownloadPanel.setBackground(JBColor.CYAN);
        gitdownloadPanel.setMaximumSize(new Dimension(300,600));

        JLabel downloadStatusLabel = new JLabel();
        downloadStatusLabel.setText("status");
        downloadStatusLabel.setBackground(JBColor.GREEN);
        downloadStatusLabel.setOpaque(true);
        downloadStatusLabel.setBorder(JBUI.Borders.empty(5));


        if(statusLabel.getText().equals("")) statusLabel.setText("status - Ready.."); //if we reopen panel, dont reset ... can make a state machine
        //statusLabel.setText("status - Ready..");
        statusLabel.setBackground(JBColor.GREEN);
        statusLabel.setOpaque(true);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statusLabel.setBorder(JBUI.Borders.empty(5));

        JButton downloadBUtton = new JButton();
        downloadBUtton.setText("DOWNLOAD");
//        downloadBUtton.setSize(300,200);
        downloadBUtton.setPreferredSize(new Dimension(300,100));
//        downloadBUtton.setMinimumSize(new Dimension(300,100));
        downloadBUtton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {



//                ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, progressIndicator);
//
//                PerformInBackgroundOption opts = new PerformInBackgroundOption() {
//                    @Override
//                    public boolean shouldStartInBackground() {
//                        return false;
//                    }
//                };


                Task.Backgroundable bgTask2 = new Task.Backgroundable(null, "Cloning zeranthiium-extras...",
                        true, null) {

                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {
                        MyGitService gitService = ApplicationManager.getApplication().getService(MyGitService.class);

                        String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                        File localPath = new File(path + File.separator + "zeranthium-extras");

                        //                  String GIT_URL = "https://github.com/cschar/demo-proj-data.git";
                        //                String GIT_URL = "https://github.com/github/testrepo.git";
                        ProgressMonitor progressMonitor = new SimpleProgressMonitor(downloadStatusLabel, progressIndicator);

                        statusLabel.setText("Status - Downloading...");
                        validate();
                        repaint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        try {
                            downloadStatusLabel.setText(downloadStatusLabel.getText() + "+");
                            validate();
                            repaint();

                            System.out.println("cloning from thread " + Thread.currentThread().toString());

                            gitService.getRepo("https://github.com/cschar/demo-proj-data.git", localPath, progressMonitor);
                            statusLabel.setText("Status - Finished ...");
                        } catch (CanceledException cancelledException) {
                            System.out.println("cloning Cancelled.. ");
                            cancelledException.printStackTrace();

                        } catch (GitAPIException gitAPIException) {
                            System.out.println("Git api exception.. ");
                            gitAPIException.printStackTrace();
                        } catch (IOException ioException) {
                            System.out.println("File IO exception.. ");
                            ioException.printStackTrace();

                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };

                System.out.println("Launchign cloning task  from thread " + Thread.currentThread().toString());
                ProgressManager.getInstance().run(bgTask2);


            }
        });


//        JButton testButton = new JButton();
//        testButton.setText("TEST====");
//        testButton.setSize(300,100);
//        testButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Testing...");
//                downloadStatusLabel.setText(downloadStatusLabel.getText() + "9");
//            }
//        });

        JButton loadButton = new JButton();
        loadButton.setText("LOAD====");

        loadButton.setPreferredSize(new Dimension(300,100));
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Loading...");

                packsList.removeAll();
                packsList.validate();
                packsList.repaint();

//                File f = new File("C:\\users\\codywin\\desktop\\work\\zeranthium-extras");

                String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                File localPath = new File(path + File.separator + "zeranthium-extras");
                File f = localPath;
//
                File[] themes = f.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.endsWith("Theme")){
                            return true;
                        }
                        return false;
                    }
                });

                 if(themes != null){
                    for(File theme : themes){
                        System.out.println("--- " + theme.getName());
                        packsList.add(getPackRow(theme.getName(), theme.getPath() + File.separator + "manifest.json"));
                    }
                 }

                validate();
                repaint();

            }
        });

//        JButton clearButton = new JButton();
//        clearButton.setText("Clear");
//        clearButton.setSize(200,100);
//        clearButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Pakcslit before remove has children: " + packsList.getComponents().length);
//                packsList.removeAll();
//                packsList.validate();
//                packsList.repaint();
//                System.out.println("Pakcslit now has children: " + packsList.getComponents().length);
//            }
//        });

        gitdownloadPanel.add(downloadBUtton);
        gitdownloadPanel.add(downloadStatusLabel);
        gitdownloadPanel.add(statusLabel);
//        gitdownloadPanel.add(testButton);

        gitdownloadPanel.add(loadButton);
//        gitdownloadPanel.add(clearButton);



        packsList = new JPanel();
        packsList.setLayout(new BoxLayout(packsList, BoxLayout.Y_AXIS));
        packsList.setBackground(JBColor.gray);
        packsList.setBorder(JBUI.Borders.empty(30));

        this.add(gitdownloadPanel);
        //this.add(packsList);

        //wrap packsList in more visible scrollBar
        JBScrollPane scrollPane = new JBScrollPane(packsList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.DARK_GRAY;
            }
        });
        this.add(scrollPane);


//        this.add(secondCol);
//        this.add(mainPanel);
    }

    private class SimpleProgressMonitor implements ProgressMonitor {

        JLabel statusLabel;
        ProgressIndicator progressIndicator;
        public SimpleProgressMonitor(JLabel lbl, ProgressIndicator progressIndicator){
            this.statusLabel = lbl;
            this.progressIndicator = progressIndicator;
        }


        public int compl = 0;
        @Override
        public void start(int totalTasks) {

            //2 tasks
            System.out.println("Starting work on " + totalTasks + " git tasks");
            System.out.println("--------------------------------");
        }

        private int totalWork;
        private String currentTask;

        @Override
        public void beginTask(String title, int totalWork)
        {
            compl = 0;
            this.totalWork = totalWork;
            progressIndicator.setText2("doing task: " + title);
            currentTask = title;

            System.out.println();
            System.out.println("Starting task: " + title + " ------- totalWork: " + totalWork );
            //System.out.println("Starting from thread " + Thread.currentThread().getName());
        }

        @Override
        public void update(int completed) {
            //https://plugins.jetbrains.com/docs/intellij/general-threading-rules.html#background-processes-and-processcanceledexception

            if(progressIndicator.isCanceled()){
                System.out.println("cancelled the task");
            }

            //set the progress indicator here
            double percent = (compl/ (double) totalWork)*100;
            progressIndicator.setFraction(percent);
            progressIndicator.setText2("doing task: " + currentTask + " % " + String.format("%3.3f", percent));

            if(compl % 2 == 0) {
                statusLabel.setText(statusLabel.getText() + "|");
            }else{
                statusLabel.setText(statusLabel.getText().substring(0, statusLabel.getText().length()-1));

            }
            statusLabel.validate();
            statusLabel.repaint();

            compl += completed;
            System.out.print(completed + "-");
            if(compl % 10 == 0){
                System.out.print("|");
            }

        }

        @Override
        public void endTask() {

            System.out.println("Done");
        }

        //custom logic to check to cancel process...
        @Override
        public boolean isCancelled()
        {

            System.out.print(".");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(progressIndicator.isCanceled()){
                System.out.println("cancelled the task");
                return true;
            }
            return false;
        }
    }


}


//JVM BUILTINTS hAS NOT BEEN INTIALIZED POREPRLY?
//        downloadBUtton.getModel().addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                ButtonModel model = (ButtonModel) e.getSource();
//                if (model.isRollover()) {
//                    //do something with Boolean variable
//                    System.out.println("Hover button");
//                    downloadBUtton.setBackground(JBColor.GREEN);
//                    //downloadBUtton.setForeground(JBColor.GREEN);
//                } else {
//                    downloadBUtton.setBackground(JBColor.GRAY);
//                    //downloadBUtton.setForeground(JBColor.GRAY);
//                }
//            }
//        });