package refactorAnalysis.commit;

import refactorAnalysis.git.GitProject;

public class CommitTask {
    
    private GitProject gitProject = null;
    
    private String commitHash = "";
    
    public CommitTask(GitProject gitProject, String commitHash) {
        this.gitProject = gitProject;
        this.commitHash = commitHash;
    }
    
    public void runAllFiles() {
        // TODO implement. create threads to run files
    }
    
}
