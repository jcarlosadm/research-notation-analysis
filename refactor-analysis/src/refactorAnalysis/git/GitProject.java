package refactorAnalysis.git;

import gitmanager.GitManager;

public class GitProject {
    
    private GitManager gitManager = new GitManager();
    
    private String name = "";

    private String url = "";
    
    private String projectFolder = "";
    
    private String currentCommitFolder = "";
    
    private String previousCommitFolder = "";
    
    private GitManager gitManagerCurrentCommit = null;
    
    private GitManager gitManagerPreviousCommit = null;
    
    public GitProject(String url) {
        this.setUrl(url);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.name = this.url.substring(this.url.lastIndexOf("/"));
    }

    public String getName() {
        return this.name;
    }
    
    public GitManager getGitManager(){
        return this.gitManager;
    }

    public String getProjectFolder() {
        return this.projectFolder;
    }

    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }
    
    public String getCurrentCommitFolder() {
        return currentCommitFolder;
    }

    public void setCurrentCommitFolder(String currentCommitFolder) {
        this.gitManagerCurrentCommit.setRepository(currentCommitFolder);
        this.gitManagerCurrentCommit.generateChangedFilesMap();
        this.currentCommitFolder = currentCommitFolder;
    }

    public String getPreviousCommitFolder() {
        return previousCommitFolder;
    }

    public void setPreviousCommitFolder(String previousCommitFolder) {
        this.gitManagerPreviousCommit.setRepository(previousCommitFolder);
        this.gitManagerPreviousCommit.generateChangedFilesMap();
        this.previousCommitFolder = previousCommitFolder;
    }

    public GitManager getGitManagerCurrentCommit() {
        return gitManagerCurrentCommit;
    }

    public GitManager getGitManagerPreviousCommit() {
        return gitManagerPreviousCommit;
    }
    
}
