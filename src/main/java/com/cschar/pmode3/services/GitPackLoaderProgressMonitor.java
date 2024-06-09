package com.cschar.pmode3.services;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import org.eclipse.jgit.lib.ProgressMonitor;

import javax.swing.*;

public class GitPackLoaderProgressMonitor implements ProgressMonitor {

    private static final Logger LOGGER = Logger.getInstance(GitPackLoaderProgressMonitor.class.getName());

    JLabel statusLabel;
    ProgressIndicator progressIndicator;

    public GitPackLoaderProgressMonitor(JLabel lbl, ProgressIndicator progressIndicator) {
        this.statusLabel = lbl;
        this.progressIndicator = progressIndicator;
        progressIndicator.setIndeterminate(false);

    }


    public int compl = 0;

    @Override
    public void start(int totalTasks) {
        //2 tasks
        LOGGER.debug("Starting work on " + totalTasks + " git tasks");
    }

    private int totalWork;
    private String currentTask;

    @Override
    public void beginTask(String title, int totalWork) {
        compl = 0;
        this.totalWork = totalWork;
        progressIndicator.setText2("doing task: " + title);
        currentTask = title;
        LOGGER.debug("Starting task: " + title + " ------- totalWork: " + totalWork);
        //
    }

    @Override
    public void update(int completed) {
        //https://plugins.jetbrains.com/docs/intellij/general-threading-rules.html#background-processes-and-processcanceledexception

//        if (progressIndicator.isCanceled()) {
//            LOGGER.info("task was cancelled");
//        }

        //set the progress indicator here
        double percent = (compl / (double) totalWork) * 100;
        progressIndicator.setFraction(percent);
        progressIndicator.setText2("doing task: " + currentTask + " % " + String.format("%3.3f", percent));

        if (compl % 2 == 0) {
            statusLabel.setText(statusLabel.getText() + "|");
        } else {
            statusLabel.setText(statusLabel.getText().substring(0, statusLabel.getText().length() - 1));

        }
        statusLabel.validate();
        statusLabel.repaint();

        compl += completed;
//        System.out.print(completed + "-");
//        if (compl % 10 == 0) {
//            System.out.print("|");
//        }

    }

    @Override
    public void endTask() {
        LOGGER.debug("ending task");
    }

    //custom logic to check to cancel process...
    @Override
    public boolean isCancelled() {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (progressIndicator.isCanceled()) {
            LOGGER.debug("cancelled the git pack loader task");
            return true;
        }
        return false;
    }

    @Override
    public void showDuration(boolean enabled) {
        return;
    }
}
