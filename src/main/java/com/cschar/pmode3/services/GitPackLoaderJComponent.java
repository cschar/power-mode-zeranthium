package com.cschar.pmode3.services;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.PowerMode3ConfigurableUI2;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

public class GitPackLoaderJComponent extends JPanel{
    private static final Logger LOGGER = Logger.getLogger( GitPackLoaderJComponent.class.getName() );

    public PowerMode3ConfigurableUI2 menuConfigurable;
    public PowerMode3 settings;
    public String directoryPath;
    private JComponent gitRepoTabbedPane;


    public GitPackLoaderJComponent(String title, PowerMode3ConfigurableUI2 menuConfigurable){

        this.menuConfigurable = menuConfigurable;
        settings = ApplicationManager.getApplication().getService(PowerMode3.class);
        directoryPath = settings.getPackDownloadPath();

//        directoryPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel packsPanel = new JPanel();
//        gitdownloadPanel.setPreferredSize(new Dimension(800,400));
        packsPanel.setLayout(new BoxLayout(packsPanel, BoxLayout.PAGE_AXIS));
//        packsPanel.setBackground(JBColor.CYAN);


        JTextField setPathLabel = new JTextField();
        setPathLabel.setEditable(false);

//        JLabel setPathLabel = new JLabel();
        setPathLabel.setText("base path : " + directoryPath);
        setPathLabel.setFont(new Font("Times", Font.PLAIN, 16));
        setPathLabel.setMaximumSize(new Dimension(700,50));

//        setPathLabel.setBackground(JBColor.gray);
        setPathLabel.setBorder(JBUI.Borders.customLineBottom(JBColor.gray));
        setPathLabel.setOpaque(true);
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

                //TODO Serialize this setting on the PowerMode3 settings

                VirtualFile chosen = FileChooser.chooseFile(fd, null, toSelect);
                System.out.println("selected" + chosen.getPath());

                directoryPath = chosen.getPath();
                setPathLabel.setText("path is: " + chosen.getPath());
                settings.setPackDownloadPath(directoryPath);


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
        panel4.setBorder(JBUI.Borders.empty(2, 2, 40, 10));
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

//                            JBPopup popup = JBPopupFactory.getInstance().createMessage("There was an error reading the manifest file:" + je.toString());

//                            popup.show(GitPackLoaderJComponent.this);
//                            JLabel t = new JLabel("failed");
//                            JBPopupFactory.getInstance().createComponentPopupBuilder(t,null).
//                                    setTitle("error").createPopup().show(customPackLoader);

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
//        packsList.setBackground(JBColor.gray);
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
                Task.Backgroundable bgTask2 = new Task.Backgroundable(null, "Cloning zeranthium-extras...",
                        true, null) {
                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {
                        GitPackLoaderService gitService = ApplicationManager.getApplication().getService(GitPackLoaderService.class);

                        ProgressMonitor progressMonitor = new GitPackLoaderProgressMonitor(downloadStatusLabel, progressIndicator);
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
        scrollPane.setVerticalScrollBar(new MyScrollBar());
//        scrollPane.setVerticalScrollBar(new );
//        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
//            @Override
//            protected void configureScrollBarColors() {
////                this.thumbColor = JBColor.DARK_GRAY;
//                this.thumbColor = JBColor.GREEN;
//            }
//        });
        packListHolder.add(scrollPane);

        loadButton.doClick();

        return packsContainer;

    }

    public class MyScrollBar extends JScrollBar {
        @Override
        public void setUI(ScrollBarUI ui) {
//            super.setUI(ui);
            super.setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {
//                this.thumbColor = JBColor.DARK_GRAY;
//                    this.thumbColor = JBColor.GREEN;
                    this.thumbColor = ZeranthiumColors.specialOption1;
                }
            });
        }
    }


    public JComponent getPackRow(String title, String manifestPath){



        JPanel packsCol = new JPanel();
        packsCol.setLayout(new BoxLayout(packsCol, BoxLayout.X_AXIS));
        packsCol.setBackground(ZeranthiumColors.specialOption1);
//        packsCol.setBorder(JBUI.Borders.emptyBottom(30));
        packsCol.setBorder(JBUI.Borders.empty(10,10,10,30));

        JButton loadPackButton = new JButton();
        loadPackButton.setText("Load pack ");
        loadPackButton.setSize(200,50);
        loadPackButton.addActionListener(new ActionListener() {
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

                            Notification n = new Notification(PowerMode3.NOTIFICATION_GROUP_DISPLAY_ID,
                                    PowerMode3.NOTIFICATION_GROUP_DISPLAY_ID + ": Error loading config",
                                    "Could not read manifest.json: " + manifestPath +
                                            "\n\n" + jsonException.toString(),
                                    NotificationType.ERROR);
                            Notifications.Bus.notify(n);

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
        packsCol.add(loadPackButton);

        //Add Memory header panel
        JPanel packInfoPanel = new JPanel();
        packInfoPanel.setLayout(new BoxLayout(packInfoPanel, BoxLayout.Y_AXIS));
        packInfoPanel.setBackground(JBColor.CYAN);
        packInfoPanel.setBorder(JBUI.Borders.empty(30));

        JPanel headerPanel = new JPanel();
        packsCol.add(packInfoPanel);

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        JLabel headerSizeLabel = new JLabel();
        headerSizeLabel.setText("[      ]");
        headerSizeLabel.setBackground(ZeranthiumColors.specialOption3);
        headerSizeLabel.setOpaque(true);
        headerSizeLabel.setBorder(JBUI.Borders.empty(5));
        headerPanel.add(headerSizeLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(500,100));
        packInfoPanel.add(headerPanel);

        JLabel configsLabel = new JLabel();

        PackScanner ps = new PackScanner();
//        try {
//            Pack p = ps.scanForPack(manifestPath);
//            configsLabel.setText(p.getConfigsString());
//        } catch (FileNotFoundException | JSONException e) {
//            e.printStackTrace();
//        }
        packInfoPanel.add(configsLabel);

        packsCol.add(packInfoPanel);

        return packsCol;
    }

    public class Pack {
        public int memorySize;
        public ArrayList<String> configs;

        public String getConfigsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for(String s: configs){
                sb.append(s + " ");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public class PackScanner {

        public Pack scanForPack(String manifestPath) throws FileNotFoundException, JSONException {
            Pack p = new Pack();

            Path path = Paths.get(manifestPath);
            InputStream inputStream = new FileInputStream(manifestPath);
            StringBuilder sb = new StringBuilder();
            Scanner s = new Scanner(inputStream);
            while(s.hasNextLine()){
                sb.append(s.nextLine());
            }
            JSONObject jo = new JSONObject(sb.toString());

            JSONArray configsToLoad = jo.getJSONArray("configsToLoad");
            for(int i =0; i< configsToLoad.length(); i++){
                p.configs.add(configsToLoad.getString(i));
            }

            return p;
        }
    }


}


