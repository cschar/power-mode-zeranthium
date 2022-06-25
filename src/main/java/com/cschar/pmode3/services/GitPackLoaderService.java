package com.cschar.pmode3.services;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.NullProgressMonitor;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

//UI  https://jetbrains.github.io/ui/principles/groups_of_controls/
public class GitPackLoaderService {

    /** used to keep track of whether to change label to 'downloading...' when we reopen the GitPackLoaderJComponent panel */
    public HashMap<String, GitPackLoaderProgressMonitor> runningMonitors;

    public GitPackLoaderService()
    {
        System.out.println("intiialized GitService");
        runningMonitors = new HashMap<>();
    }


    public void getRepo(String REMOTE_URL, File localPath, ProgressMonitor progressMonitor) throws IOException, GitAPIException {

//        String REMOTE_URL = "https://github.com/github/testrepo.git";
        if(REMOTE_URL == null){
            REMOTE_URL = "https://github.com/github/testrepo.git";
        }

//        File localPath = File.createTempFile("TestGitRepository", "");
        if(localPath == null){
            String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            localPath = new File(path + File.separator + "zeranthium-extras");
        }
//        localPath.createNewFile();
        localPath.mkdir();
//        if(!localPath.delete()) {
//            throw new IOException("Could not delete  file " + localPath + "please make sure, no files are in this directory path");
//        }
        Git result = null;
        boolean found = false;
        try {
            result = Git.open(localPath);

            System.out.println("Is repo clean? " + result.status().call().isClean());

            //reset hard
            result.reset().setMode(ResetCommand.ResetType.HARD).call();
            //checkout master
            Ref r = result.checkout().setName("master").call();
//            assert(r.getObjectId().getName().equals("831b714961dc667b62c5ed4aa74c4505a17561a9"));
            System.out.println("found repo @ commit" + r.getObjectId().getName());

            found = true;
        }catch(RepositoryNotFoundException e){
            System.out.println("repository not found... gracefully continuing");

        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            if(result != null) {
                result.close();
            }
        }

        if (found) {
            return;
        }

        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);

        try {
            result = Git.cloneRepository()
                    .setURI(REMOTE_URL)
                    .setDirectory(localPath)
                    .setProgressMonitor(progressMonitor)
                    .call();

            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            System.out.println("Having repository: " + result.getRepository().getDirectory());

        } finally {
            if(result != null){
                result.close();
            }

        }

    }

}
