package com.cschar.pmode3.services;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
import com.cschar.pmode4.PowerMode3SettingsJComponent;
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
import java.util.ArrayList;
import java.util.Scanner;
import com.intellij.openapi.diagnostic.Logger;

//TODO: shouldnt be in services folder
public class GitPackLoaderJComponent extends JPanel{
    private static final Logger LOGGER = Logger.getInstance( GitPackLoaderJComponent.class.getName() );
    public static final String ZERANTHIUM_EXTRAS_GIT_VOL1 = "https://github.com/powermode-zeranthium/zeranthium-extras-vol1.git";
    public static final String ZERANTHIUM_EXTRAS_GIT_VOL2 = "https://github.com/powermode-zeranthium/zeranthium-extras-vol2.git";
    public static final String ZERANTHIUM_EXTRAS_GIT_VOL3 = "https://github.com/powermode-zeranthium/zeranthium-extras-vol3.git";


    public PowerMode3SettingsJComponent menuConfigurable;
    public PowerMode3 settings;
    public String directoryPath;
    private JComponent gitRepoTabbedPane;

    private Color jbDarkGreen = JBColor.green;

    public GitPackLoaderJComponent(String title, PowerMode3SettingsJComponent menuConfigurable){

        this.menuConfigurable = menuConfigurable;
        settings = ApplicationManager.getApplication().getService(PowerMode3.class);
        directoryPath = settings.getPackDownloadPath();

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel packsPanel = new JPanel();
        //DONT REMOVE!!!
        packsPanel.setPreferredSize(new Dimension(1000,700));
        packsPanel.setLayout(new BoxLayout(packsPanel, BoxLayout.PAGE_AXIS));
//        packsPanel.setLayout(new BoxLayout(packsPanel, BoxLayout.Y_AXIS));
//        packsPanel.setBackground(JBColor.CYAN);


        JTextField setPathLabel = new JTextField();
        setPathLabel.setEditable(false);

//        JLabel setPathLabel = new JLabel();
        setPathLabel.setText("base path : " + directoryPath);
        setPathLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        setPathLabel.setMaximumSize(new Dimension(700,50));

//        setPathLabel.setBackground(JBColor.gray);
//        setPathLabel.setBorder(JBUI.Borders.customLineBottom(JBColor.gray));
        setPathLabel.setOpaque(true);
        packsPanel.add(setPathLabel);

        JButton SetPathButton = new JButton();
        SetPathButton.setText("Set Download Path");


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
                LOGGER.debug("selected: " + chosen.getPath() + " as custom download path for zeranthium packs");

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
        LOGGER.trace("building git repo tab at " + directoryPath);

        File localPath = new File(directoryPath + File.separator + "zeranthium-extras");

        //vol1 pack
        JPanel panel2 = new JPanel();
        panel2.setBorder(JBUI.Borders.empty(2, 2, 2, 2));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIcon2 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        gitRepoTabbedPane.addTab("zeranthium-extras-vol1", sliderIcon2, panel2);
        JPanel packListHolder0 = getPackHolder(localPath.getPath(), ZERANTHIUM_EXTRAS_GIT_VOL1, "zeranthium-extras-vol1");
        panel2.add(packListHolder0);

        //vol2 pack
        JPanel panel3 = new JPanel();
//        panel3.setBackground(JBColor.getHSBColor(100,88,20));
        panel3.setBorder(JBUI.Borders.empty(2, 2, 2, 2));
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIcon3 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        gitRepoTabbedPane.addTab("zeranthium-extras-vol2", sliderIcon3, panel3);
        JPanel packListHolder1 = getPackHolder(localPath.getPath(), ZERANTHIUM_EXTRAS_GIT_VOL2, "zeranthium-extras-vol2");
        panel3.add(packListHolder1);

        //vol3 pack
        JPanel panelVol3 = new JPanel();
//        panel3.setBackground(JBColor.getHSBColor(100,88,20));
        panelVol3.setBorder(JBUI.Borders.empty(2, 2, 2, 2));
        panelVol3.setLayout(new BoxLayout(panelVol3, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIconVol3 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        gitRepoTabbedPane.addTab("zeranthium-extras-vol3", sliderIconVol3, panelVol3);
        JPanel packListHolderVol3 = getPackHolder(localPath.getPath(), ZERANTHIUM_EXTRAS_GIT_VOL3, "zeranthium-extras-vol3");
        panelVol3.add(packListHolderVol3);

        //user defined pack
        JPanel panel4 = new JPanel();
        panel4.setBorder(JBUI.Borders.empty(2, 2, 40, 10));
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIcon4 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        gitRepoTabbedPane.addTab("Custom pack file", sliderIcon4, panel4);

        panel4.add(new JLabel("Load your own custom pack from the filesystem here..."));

        JLabel customPackLocationLbl = new JLabel();
//        JTextField customPackLocationLbl = new JTextField();
//        customPackLocationLbl.setEditable(false);

        customPackLocationLbl.setText("custom pack path : ");
        customPackLocationLbl.setFont(new Font("Times", Font.PLAIN, 14));
//        customPackLocationLbl.setMaximumSize(new Dimension(700,50));
        panel4.add(customPackLocationLbl);

        JButton customPackLoader = new JButton();
        customPackLoader.setText("Load pack");

        customPackLoader.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Showing custom config pack UI...");

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

//                    VirtualFile toSelect = LocalFileSystem.getInstance().findFileByPath(directoryPath);
//                    VirtualFile chosenManifest = FileChooser.chooseFile(fd, null, toSelect);
                    VirtualFile chosenManifest = FileChooser.chooseFile(fd, null, null);
                    

                    if(!chosenManifest.getName().equals("manifest.json")){
                        Messages.showInfoMessage("Please select a manifest.json file in a pack directory", "Pack Load Failed");
                        return;
                    }

                    loadPackModal(chosenManifest.getPath());
                    customPackLocationLbl.setText("custom pack path : " + chosenManifest.getPath());
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

        packsRepoUrlHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
//        packsRepoUrlHeader.setBorder(JBUI.Borders.customLine(JBColor.yellow, 5));
        JTextField repoLabel = new JTextField("downloading from repo:   " + repoUrl);
        repoLabel.setMaximumSize(new Dimension(1200,50));
        repoLabel.setPreferredSize(new Dimension(1200,50));
//        JLabel repoLabel = new JLabel("downloading from repo:   " + repoUrl);
//        repoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        JTextField repo = new JTextField("yeah");
//        repoLabel.add(repo);
//        repoLabel.setBackground(JBColor.gray);
        repoLabel.setOpaque(true);
        repoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        repoLabel.setBorder(JBUI.Borders.empty(5));
        packsRepoUrlHeader.add(repoLabel);

//        JLabel repoTarget = new JLabel("Target path:  " + localPath.getPath());
        JTextField repoTarget = new JTextField("Target path:  " + localPath.getPath());
        repoTarget.setMaximumSize(new Dimension(1200,50));
        repoTarget.setMinimumSize(new Dimension(700,50));
        repoTarget.setSize(new Dimension(1200,50));
//        repoTarget.setAlignmentX(Component.LEFT_ALIGNMENT);
//        repoTarget.setBackground(JBColor.gray);
        repoTarget.setOpaque(true);
        repoTarget.setFont(new Font("Arial", Font.PLAIN, 12));
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
        downloadStatusLabel.setBackground(jbDarkGreen);
        downloadStatusLabel.setOpaque(true);
        downloadStatusLabel.setBorder(JBUI.Borders.empty(5));

        JLabel statusLabel = new JLabel();

        if(statusLabel.getText().equals("")){
            statusLabel.setText("Ready"); //if we reopen panel, dont reset
        }

        GitPackLoaderService gitService = ApplicationManager.getApplication().getService(GitPackLoaderService.class);
        if(gitService.runningMonitors.get(customRepoName) != null){
            statusLabel.setText("Downloading...");
        }



        statusLabel.setBackground(jbDarkGreen);
        statusLabel.setOpaque(true);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statusLabel.setBorder(JBUI.Borders.empty(5));

//        DataContext dataContext = (DataContext) DataManager.getDataProvider(this);
//        Project project = (Project) dataContext.getData(CommonDataKeys.PROJECT);
//
//        
//
////        List<LocalTask> localTasks = TaskManager.getManager(project).getLocalTasks();
////        TaskManager.getManager(project).
//
//        ProgressManager.getInstance().


        JButton downloadBUtton = new JButton();
        downloadBUtton.setText("DOWNLOAD");
        downloadBUtton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("launching Git Clone task");

                //TODO Add this task to a hashmap to keep track of it.
//                Task.Modal task = new Task.Modal(null, downloadBUtton,
//                        "Cloning "+customRepoName+"...",
//                        true) {
                Task.Backgroundable task = new Task.Backgroundable(null,
                                                                        "Cloning "+customRepoName+"...",
                                                                true, null) {

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        GitPackLoaderService gitService = ApplicationManager.getApplication().getService(GitPackLoaderService.class);
                        gitService.runningMonitors.remove(customRepoName);
//                        gitService.backgroundTasks.remove(customRepoName);
                    }

                    @Override
                    public void onFinished() {
                        GitPackLoaderService gitService = ApplicationManager.getApplication().getService(GitPackLoaderService.class);
                        gitService.runningMonitors.remove(customRepoName);
//                        gitService.backgroundTasks.remove(customRepoName);
                        super.onFinished();
                    }

                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {
                        GitPackLoaderService gitService = ApplicationManager.getApplication().getService(GitPackLoaderService.class);


                        //need to save this progressMonitor to check if its still going each time we load panel
                        ProgressMonitor progressMonitor = new GitPackLoaderProgressMonitor(downloadStatusLabel, progressIndicator);
//                        statusLabel.setText("Status - Downloading...");

                        if(gitService.runningMonitors.get(customRepoName) == null){
                            LOGGER.debug("No running monitor task found for: " + customRepoName + " ... adding");
                            gitService.runningMonitors.put(customRepoName, (GitPackLoaderProgressMonitor) progressMonitor);
                        }


                        statusLabel.setText("Downloading...");
                        statusLabel.setToolTipText("git repo download status");
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
                            LOGGER.debug("Fetching repo...");
                            gitService.getRepo(repoUrl, localPath, progressMonitor);
                            statusLabel.setText("Status - Finished ...");
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LOGGER.trace("Launching cloning task  from thread " + Thread.currentThread().toString());
//                ProgressManager.getInstance().run(bgTask2);
                //                gitService.backgroundTasks.put(customRepoName, bgTask2);
                ProgressManager.getInstance().run(task);



            };
        });



        JButton loadButton = new JButton();
        loadButton.setText("RELOAD");
        loadButton.setSize(100,100);
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Loading pack for " + customRepoName+"...");

//                statusLabel.
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
                        LOGGER.trace("loading theme: " + theme.getName());
                        packsList.add(getPackRow(theme.getName(), theme.getPath() ));
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
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setHorizontalScrollBar(new MyScrollBar());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBar(new MyScrollBar());

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
                    this.thumbColor = jbDarkGreen;
                }
            });
        }
    }

    private void loadPackModal(String manifestPath){
        Task.Modal modalTask = new Task.Modal(null, "Loading Theme Pack", true) {
            public void run(@NotNull() final ProgressIndicator indicator) {
                LOGGER.debug("Launching pack loader task...");
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

//                    int result = Messages.showYesNoDialog(null,
//                            "<html> <h1> Load config pack? </h1>" +
//                                    "Error loading config pack..." +
//                                    "</html>",
//                            "LOAD PACK","yes","no", null);

                    jsonException.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                LOGGER.debug("cancelling pack load...");
                //TODO rollback changes to a saved state at start of run() above
                super.onCancel();
            }
        };

        ApplicationManager.getApplication().invokeLater(() -> ProgressManager.getInstance().run(modalTask));
    }


    public JComponent getPackRow(String title, String themePath){

        String manifestPath = themePath + File.separator + "manifest.json";

        JPanel packsRow = new JPanel();
        packsRow.setLayout(new BoxLayout(packsRow, BoxLayout.X_AXIS));
        packsRow.setBackground(ZeranthiumColors.specialOption1);
//        packsCol.setBorder(JBUI.Borders.emptyBottom(30));
        packsRow.setBorder(JBUI.Borders.empty(10,10,10,30));

        JButton loadPackButton = new JButton();
        loadPackButton.setText("Load pack ");
        loadPackButton.setSize(200,50);
        loadPackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPackModal(manifestPath);
            }
        });
        packsRow.add(loadPackButton);

        JLabel packPreviewLabel = new JLabel();
        PackScanner ps = new PackScanner();
        ImageIcon sliderIcon = ps.getPreviewIcon(themePath);
//        ImageIcon sliderIcon = new ImageIcon(this.getClass().getResource("/icons/pack-logo6.png"));
        packPreviewLabel.setIcon(sliderIcon);
        packsRow.add(packPreviewLabel);

        //Add Memory header panel
        JPanel packInfoPanel = new JPanel();
        packInfoPanel.setLayout(new BoxLayout(packInfoPanel, BoxLayout.Y_AXIS));
//        packInfoPanel.setBackground(JBColor.CYAN);
        packInfoPanel.setBackground(JBColor.DARK_GRAY);
        packInfoPanel.setBorder(JBUI.Borders.empty(30));
//        packInfoPanel.setPreferredSize(new Dimension());

        JPanel headerPanel = new JPanel();
        packsRow.add(packInfoPanel);

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        JLabel headerSizeLabel = new JLabel();
        headerSizeLabel.setText("[      ]");
        headerSizeLabel.setBackground(ZeranthiumColors.specialOption3);
        headerSizeLabel.setOpaque(true);
        headerSizeLabel.setBorder(JBUI.Borders.empty(5));
        headerPanel.add(headerSizeLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(500,100));
//        headerPanel.setMinimumSize(new Dimension(500,80));
        packInfoPanel.add(headerPanel);

        JLabel configsLabel = new JLabel();
//        configsLabel.setMaximumSize(new Dimension(500,200));
        configsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        configsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        configsLabel.setBorder(JBUI.Borders.empty(5));
        configsLabel.setOpaque(true);

        try {
            Pack p = ps.scanForPack(manifestPath);
            configsLabel.setText(p.getConfigsString());
            configsLabel.setBackground(JBColor.LIGHT_GRAY);
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
            configsLabel.setText("Error loading manifest.json");
            configsLabel.setFont(new Font("Arial", Font.BOLD, 15));
            configsLabel.setBackground(JBColor.red);
            configsLabel.setForeground(Color.black);
        }
        packInfoPanel.add(configsLabel);
        packInfoPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        packsRow.add(packInfoPanel);

        return packsRow;
    }

    public class Pack {
        public int memorySize;
        public ArrayList<String> configs;

        public Pack(){
            configs = new ArrayList<>();
        }

        public String getConfigsString(){
            StringBuilder sb = new StringBuilder();
            sb.append(configs.size() + " Configs used: [");
            for(String s: configs){
                sb.append(s + ",   ");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public class PackScanner {

        public ImageIcon getPreviewIcon(String packPath){
            String previewIconPath = packPath + File.separator + "preview.png";
            File f = new File(previewIconPath);

            ImageIcon sliderIcon;
            if(f.exists()){
                sliderIcon = new ImageIcon(previewIconPath);
                Image sliderIconImage = sliderIcon.getImage(); // transform it
                Image sliderIconResized = sliderIconImage.getScaledInstance(150, 150,  Image.SCALE_SMOOTH);
                sliderIcon = new ImageIcon(sliderIconResized);
            }else{
                sliderIcon = new ImageIcon(this.getClass().getResource("/icons/pack-logo6.png"));
            }
            return sliderIcon;
        }

        public Pack scanForPack(String manifestPath) throws FileNotFoundException, JSONException {
            Pack p = new Pack();

//            Path path = Paths.get(manifestPath);
            InputStream inputStream = new FileInputStream(manifestPath);
            StringBuilder sb = new StringBuilder();
            Scanner s = new Scanner(inputStream);
            while(s.hasNextLine()){
                sb.append(s.nextLine());
            }

            JSONObject jo = new JSONObject(sb.toString());
            JSONArray configsToLoad = jo.getJSONArray("configsToLoad");
            for (int i = 0; i < configsToLoad.length(); i++) {
                p.configs.add(configsToLoad.getString(i));
            }


            return p;
        }
    }


}


