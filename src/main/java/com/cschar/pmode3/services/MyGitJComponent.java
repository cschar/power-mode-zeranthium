package com.cschar.pmode3.services;

import com.cschar.pmode3.PowerMode3ConfigurableUI2;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.logging.Logger;

public class MyGitJComponent extends JPanel{
    private static final Logger LOGGER = Logger.getLogger( MyGitJComponent.class.getName() );


    public JComponent getPackRow(String title, String manifestPath){



        JPanel packsCol = new JPanel();
        packsCol.setLayout(new BoxLayout(packsCol, BoxLayout.X_AXIS));
        packsCol.setBackground(ZeranthiumColors.specialOption1);
//        packsCol.setBorder(JBUI.Borders.emptyBottom(30));
        packsCol.setBorder(JBUI.Borders.empty(10,10,10,30));

        JButton button1 = new JButton();
        button1.setText("Load pack - click it");
        button1.setSize(200,50);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Task.Modal modalTask = new Task.Modal(null, "Loading Theme Pack", true) {
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
        packInfoPanel.setBorder(JBUI.Borders.empty(30));

        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        JLabel headerSizeLabel = new JLabel();
        headerSizeLabel.setText("A size Label");
        headerSizeLabel.setBackground(ZeranthiumColors.specialOption3);
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

    public  PowerMode3ConfigurableUI2 menuConfigurable;
    public String directoryPath;
    private JComponent gitRepoTabbedPane;


    public MyGitJComponent(String title,  PowerMode3ConfigurableUI2 menuConfigurable){

        this.menuConfigurable = menuConfigurable;
        directoryPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel packsPanel = new JPanel();
//        gitdownloadPanel.setPreferredSize(new Dimension(800,400));
        packsPanel.setLayout(new BoxLayout(packsPanel, BoxLayout.Y_AXIS));
//        packsPanel.setBackground(JBColor.CYAN);


        JLabel setPathLabel = new JLabel();
        setPathLabel.setText("base path: " + directoryPath);
        setPathLabel.setFont(new Font("Times", Font.PLAIN, 16));
        setPathLabel.setBackground(JBColor.GREEN);
        setPathLabel.setOpaque(true);
        setPathLabel.setBorder(JBUI.Borders.empty(5));
        packsPanel.add(setPathLabel);

        JButton SetPathButton = new JButton();
        SetPathButton.setText("Set Download Path");
//        downloadBUtton.setSize(300,200);
        SetPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon sliderIcon = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));


            int result = Messages.showYesNoDialog(null,
        "<html> <h1> Set Download Path for Packs</h1>" +
                "\n Config <b>packs</b> from different zeranthium-extras github repos will be downloaded here. " +
                "\n This path will have a zeranthium-extras folder created inside it with sub-folders for each pack" +
                "\n\n" +
                " </html>",
                "Set Download Path","yes","no", sliderIcon);

            if(result == Messages.YES){
                FileChooserDescriptor fd = new FileChooserDescriptor(false,true,false,false,false,false);
                VirtualFile toSelect = LocalFileSystem.getInstance().findFileByPath(directoryPath);
                VirtualFile chosen = FileChooser.chooseFile(fd, null, toSelect);
                System.out.println("selected" + chosen.getPath());

                directoryPath = chosen.getPath();
                setPathLabel.setText("path is: " + chosen.getPath());



                try {
                    packsPanel.remove(gitRepoTabbedPane);
                    packsPanel.revalidate();
                    packsPanel.repaint();
                    Thread.sleep(200);
                    gitRepoTabbedPane = buildGitRepoTabbedPane(directoryPath);
//                    gitRepoTabbedPane.setBorder(JBUI.Borders.customLine(JBColor.red, 10));
                } catch (InterruptedException fds) {
                    fds.printStackTrace();
                }

                packsPanel.add(gitRepoTabbedPane);
                packsPanel.revalidate();
                packsPanel.repaint();
                }
            }

//
        });
        packsPanel.add(SetPathButton);
//        packsPanel.setBorder(JBUI.Borders.customLine(JBColor.green, 10));
        gitRepoTabbedPane = buildGitRepoTabbedPane(directoryPath);
        packsPanel.add(gitRepoTabbedPane);
        this.add(packsPanel);
    }

    private JComponent buildGitRepoTabbedPane(String directoryPath){
        ///////////////////////////////////////////////
        /////////////////////
        ////////////TABBED REPOS
        JBTabbedPane gitRepoTabbedPane = new JBTabbedPane(JTabbedPane.LEFT);
        gitRepoTabbedPane.setOpaque(false);
        LOGGER.info("building git repo tab at " + directoryPath);

        File localPath = new File(directoryPath + File.separator + "zeranthium-extras");

        JPanel panel2 = new JPanel();
        panel2.setBorder(JBUI.Borders.empty(2, 2, 40, 2));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIcon2 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        gitRepoTabbedPane.addTab("zeranthium-extras-vol0", sliderIcon2, panel2);


        JPanel packListHolder0 = getPackHolder(localPath.getPath(),"https://github.com/cschar/demo-proj-data.git", "zeranthium-extras-vol0");
        panel2.add(packListHolder0);

        JPanel panel3 = new JPanel();
//        panel3.setBackground(JBColor.getHSBColor(100,88,20));
        panel3.setBorder(JBUI.Borders.empty(2, 2, 40, 2));
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIcon3 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        gitRepoTabbedPane.addTab("zeranthium-extras-vol1", sliderIcon3, panel3);


        JPanel packListHolder1 = getPackHolder(localPath.getPath(),"https://github.com/cschar/demo-proj-data.git", "zeranthium-extras-vol1");
        panel3.add(packListHolder1);


        JPanel panel4 = new JPanel();
        panel4.setBorder(JBUI.Borders.empty(2, 2, 40, 2));
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIcon4 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        gitRepoTabbedPane.addTab("Custom pack file", sliderIcon4, panel4);

        panel4.add(new JLabel("Load your own custom pack from the filesystem here..."));
        JButton customPackLoader = new JButton();
        customPackLoader.setText("Load pack");
        customPackLoader.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon sliderIcon5 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
                int result = Messages.showYesNoDialog(null,
                    "<html> <h1> Load config pack? </h1>" +
                            "\n Please select a <b> manifest.json </b> file found inside a config pack of your choosing" +
                            "\n Config <b>pack</b> setup instructions can be found on the " +
                            " <a href='https://cschar.github.io/power-mode-zeranthium/'> zeranthium-extras doc site </a>" +
                            "\n\n" +
                            "Example packs can be found in the <a href='https://github.com/cschar/zeranthium-extras'> zeranthium-extras repo </a> " +
                            "</html>",
                    "LOAD PACK","yes","no", sliderIcon5);

                if(result == Messages.YES){
                    FileChooserDescriptor fd = new FileChooserDescriptor(true,false,false,false,false,false);
    //                fd.setForcedToUseIdeaFileChooser(true);
//                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);
                    VirtualFile toSelect = LocalFileSystem.getInstance().findFileByPath(directoryPath);

                    VirtualFile chosen = FileChooser.chooseFile(fd, null, toSelect);

                    if(chosen.getName().equals("manifest.json")){
                        Messages.showInfoMessage("Please select a manifest.json file in a pack directory", "Pack Load Failed");
                    }else {
                        try {
                            menuConfigurable.loadConfigPack(chosen.getPath(),null);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch( JSONException je){
                            Messages.showErrorDialog("<html><h1>Pack Failed to load</h1>" +
                                            "\n There was an error processing the .json info" +
                                            "\n <pre>" + je.toString() + "</pre>" +
                                            "" +
                                            "</html>",
                                    "Pack Load Failed");
                            LOGGER.severe(je.toString());
                        }
                    }

                }
            }
        });

        panel4.add(customPackLoader);

        return gitRepoTabbedPane;

    }

    private JPanel getPackHolder(String baseDirectory, String repoUrl, String customRepoName){
        File localPath = new File(baseDirectory + File.separator + customRepoName);

        JPanel packsContainer = new JPanel();
//        packsContainer.setBorder(JBUI.Borders.customLine(JBColor.yellow, 5));
        packsContainer.setLayout(new BoxLayout(packsContainer, BoxLayout.Y_AXIS));

        JPanel packsRepoUrlHeader = new JPanel();
        packsRepoUrlHeader.setLayout(new BoxLayout(packsRepoUrlHeader, BoxLayout.Y_AXIS));
        JLabel repoLabel = new JLabel("downloading from repo:   " + repoUrl);
//        repoLabel.setBackground(JBColor.gray);
        repoLabel.setOpaque(true);
        repoLabel.setFont(new Font("Times", Font.PLAIN, 14));
        repoLabel.setBorder(JBUI.Borders.empty(5));
        packsRepoUrlHeader.add(repoLabel);

        JLabel repoTarget = new JLabel("Target path:  " + localPath.getPath());
        repoTarget.setMaximumSize(new Dimension(800,50));
//        repoTarget.setBackground(JBColor.gray);
        repoTarget.setOpaque(true);
        repoTarget.setFont(new Font("Times", Font.PLAIN, 12));
        repoTarget.setBorder(JBUI.Borders.empty(5));
        packsRepoUrlHeader.add(repoTarget);

        packsContainer.add(packsRepoUrlHeader);

        JPanel packListHolder = new JPanel();
        packListHolder.setLayout(new BoxLayout(packListHolder, BoxLayout.X_AXIS));
        packsContainer.add(packListHolder);


        JPanel packListDownloadOptions = new JPanel();
        packListDownloadOptions.setLayout(new BoxLayout(packListDownloadOptions, BoxLayout.Y_AXIS));
        packListHolder.add(packListDownloadOptions);

        JPanel packsList = new JPanel();
        packsList.setLayout(new BoxLayout(packsList, BoxLayout.Y_AXIS));
        packsList.setBackground(JBColor.gray);
        packsList.setBorder(JBUI.Borders.empty(30));
//        packsList.setBorder(JBUI.Borders.customLine(JBColor.WHITE));



        JLabel downloadStatusLabel = new JLabel();
        downloadStatusLabel.setText("[]");
        downloadStatusLabel.setBackground(JBColor.GREEN);
        downloadStatusLabel.setOpaque(true);
        downloadStatusLabel.setBorder(JBUI.Borders.empty(5));

        JLabel statusLabel = new JLabel();

        if(statusLabel.getText().equals("")) statusLabel.setText("status - Ready.."); //if we reopen panel, dont reset
        statusLabel.setBackground(JBColor.GREEN);
        statusLabel.setOpaque(true);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statusLabel.setBorder(JBUI.Borders.empty(5));

        JButton downloadBUtton = new JButton();
        downloadBUtton.setText("DOWNLOAD");
        downloadBUtton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Task.Backgroundable bgTask2 = new Task.Backgroundable(null, "Cloning zeranthiium-extras...",
                        true, null) {
                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {
                        MyGitService gitService = ApplicationManager.getApplication().getService(MyGitService.class);

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
                            downloadStatusLabel.setText("[]+");
                            validate();
                            repaint();
                            gitService.getRepo("https://github.com/cschar/demo-proj-data.git", localPath, progressMonitor);
                            statusLabel.setText("Status - Finished ...");
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LOGGER.info("Launching cloning task  from thread " + Thread.currentThread().toString());
                ProgressManager.getInstance().run(bgTask2);

            }
        });



        JButton loadButton = new JButton();
        loadButton.setText("LOAD====");
        loadButton.setSize(100,100);
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Loading...");

                packsList.removeAll();
                packsList.validate();
                packsList.repaint();

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


        packListDownloadOptions.add(downloadBUtton);
        packListDownloadOptions.add(downloadStatusLabel);
        packListDownloadOptions.add(statusLabel);
        packListDownloadOptions.add(loadButton);


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
        packListHolder.add(scrollPane);

        loadButton.doClick();

        return packsContainer;

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


