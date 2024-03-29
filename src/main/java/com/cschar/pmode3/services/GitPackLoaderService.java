package com.cschar.pmode3.services;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

//UI  https://jetbrains.github.io/ui/principles/groups_of_controls/
/** Clones git packs, thats it. */
public class GitPackLoaderService implements Disposable {

    private static final Logger LOGGER = Logger.getInstance(GitPackLoaderService.class.getName());

    /** used to keep track of whether to change label to 'downloading...' when we reopen the GitPackLoaderJComponent panel */
    public HashMap<String, GitPackLoaderProgressMonitor> runningMonitors;
    public HashMap<String, Task.Backgroundable> backgroundTasks;

    public GitPackLoaderService()
    {
//        
        runningMonitors = new HashMap<>();
//        backgroundTasks = new HashMap<>();
    }


    public void getRepo(String REMOTE_URL, File localPath, ProgressMonitor progressMonitor) throws IOException, GitAPIException {
        LOGGER.trace("GitPackLoaderService: getRepo");

        if(localPath == null){
            String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            localPath = new File(path + File.separator + "zeranthium-extras");
        }

        localPath.mkdir();
//        if(!localPath.delete()) {
//            throw new IOException("Could not delete  file " + localPath + "please make sure, no files are in this directory path");
//        }

        //Check If the repository already exists on the local filesystem
        Git result = null;
        boolean found = false;
        try {
            result = Git.open(localPath);

            LOGGER.trace("Repo is clean:  " + result.status().call().isClean());

            //reset hard
            result.reset().setMode(ResetCommand.ResetType.HARD).call();
            //checkout main
            Ref r = result.checkout().setName("main").call();
            // optionally assert we are at a specific commit
            // assert(r.getObjectId().getName().equals("831b714961dc667b62c5ed4aa74c4505a17561a9"));
            LOGGER.trace("found repo @ commit" + r.getObjectId().getName());

            found = true;
        }catch(RepositoryNotFoundException e){
            LOGGER.debug("Initial Repository Check: repository not found for " + REMOTE_URL);
        }catch(IOException e) {
            LOGGER.debug("Initial Repository Check: IOException...");
        }catch(RefNotFoundException e){
            LOGGER.debug("Initial Repository Check: RefNotfoundException...");
        }finally {
            if(result != null) {
                result.close();
            }
        }

        if (found) {
            LOGGER.trace("Repo already exists.. skipping clone");
            return;
        }

        // then clone

        LOGGER.trace("Repo not found... cloning");
        try {
            result = Git.cloneRepository()
                    .setURI(REMOTE_URL)
                    .setDirectory(localPath)
                    .setProgressMonitor(progressMonitor)
                    .call();
            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            
        }catch(TransportException e){
            LOGGER.info("Repository download cancelled");
//            e.printStackTrace();
        }
        finally {
            if(result != null){
                result.close();
            }
        }

    }

    @Override
    public void dispose() {
        LOGGER.trace("GitPackLoaderService: Disposing...");
    }
}
