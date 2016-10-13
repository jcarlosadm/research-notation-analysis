package refactorAnalysis;

import gitmanager.GitManager;
import refactorAnalysis.commit.CommitTask;
import refactorAnalysis.folderManager.FolderManager;
import refactorAnalysis.git.GitList;
import refactorAnalysis.git.GitProject;

public class Main {
	public static void main(String[] args) {

		FolderManager.createAllMainFolders();

		GitList gitList = new GitList();

		// each project
		for (String gitUrl : gitList.getGitUrlList()) {
			GitProject gitProject = new GitProject(gitUrl);
			gitProject.setProjectFolder(FolderManager.createProjectFolder(gitProject.getName()));

			if (GitManager.cloneRepo(gitUrl, gitProject.getProjectFolder()) == true) {
				GitManager gitManager = gitProject.getGitManager();

				gitManager.setRepository(gitProject.getProjectFolder());
				gitManager.generateChangedFilesMap();

				// each relevant commit
				for (String commitHash : gitManager.getChangeMapKeys()) {
					// run all files in this commit
					CommitTask commitTask = new CommitTask(gitProject, commitHash);
					commitTask.runAllFiles();
				}
			}
		}

	}
}
