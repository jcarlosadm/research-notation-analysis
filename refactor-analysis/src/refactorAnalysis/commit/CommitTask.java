package refactorAnalysis.commit;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import gitmanager.GitManager;
import gitmanager.properties.PropertiesManager;
import refactorAnalysis.file.FileTask;
import refactorAnalysis.git.GitProject;

public class CommitTask {

    private GitProject gitProject = null;

    private String commitHash = "";

    public CommitTask(GitProject gitProject, String commitHash) {
        this.gitProject = gitProject;
        this.commitHash = commitHash;
    }

    public void runAllFiles() {
        List<String> filepaths = getRelevantFilepaths();
        
        int numberOfThreads = this.getNumberOfThreads();

        Stack<Thread> threads = new Stack<>();
        for (String filepath : filepaths) {

            threads.push(new Thread(new FileTask(this.gitProject, this.commitHash, filepath)));
            // TODO change the constant!
            if (threads.size() >= numberOfThreads) {
                this.runAllThreads(threads);
            }
        }

        if (!threads.isEmpty()) {
            this.runAllThreads(threads);
        }
    }

    private List<String> getRelevantFilepaths() {
        GitManager gitManager = this.gitProject.getGitManager();

        return gitManager.getChangedFilesMap().get(commitHash);
    }

    private void runAllThreads(Stack<Thread> threads) {
        List<Thread> threadList = new ArrayList<>();
        
        while (!threads.isEmpty()) {
            threadList.add(threads.peek());
            threads.pop().start();
        }
        
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private int getNumberOfThreads(){
        String numberOfThreadsString = PropertiesManager.getPropertie("threads.maximum");
        int numberOfThreads;
        try {
            numberOfThreads = Integer.parseInt(numberOfThreadsString);
        } catch (Exception e) {
            numberOfThreads = 1;
        }
        
        return numberOfThreads;
    }

}
