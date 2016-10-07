package test;

import java.io.File;

import gitmanager.GitManager;

public class MainTest {

    public static void main(String[] args) {
        
        String RepoUrl = "https://github.com/jcarlosadm/dateC";
        String folderPath = "outputs"+File.separator+"repotest";
        File file = new File(folderPath);
        
        if (!file.exists() && !file.mkdirs()) {
            return;
        }
        
        GitManager gitManager = new GitManager();
        boolean successful = GitManager.cloneRepo(RepoUrl, folderPath);
        
        if (successful == true) {
            gitManager.setRepository(folderPath);
            
            gitManager.generateChangedFilesMap();
            for (String commit : gitManager.getChangeMapKeys()) {
                System.out.println(commit);
            }
        }
        
    }

}
