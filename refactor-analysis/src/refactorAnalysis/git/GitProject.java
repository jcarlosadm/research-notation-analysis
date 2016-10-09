package refactorAnalysis.git;

import gitmanager.GitManager;

public class GitProject {
    
    private GitManager gitManager = new GitManager();
    
    private String name = "";

    private String url = "";
    
    private String projectFolder = "";
    
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
    
}
