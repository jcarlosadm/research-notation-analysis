package refactorAnalysis;

import java.util.List;

import gitmanager.GitManager;
import gitmanager.properties.PropertiesManager;
import refactorAnalysis.commit.CommitTask;
import refactorAnalysis.folderManager.FolderManager;
import refactorAnalysis.git.GitList;
import refactorAnalysis.git.GitProject;
import refactorAnalysis.report.Report;
import refactorAnalysis.report.email.EmailManager;

public class Main {
	public static void main(String[] args) {

		FolderManager.createAllMainFolders();

		EmailManager emailManager = setEmailManager();

		GitList gitList = new GitList();
		List<String> gitUrlList = gitList.getGitUrlList();

		int projectCount = 0;
		// each project
		for (String gitUrl : gitUrlList) {
			++projectCount;
			GitProject gitProject = new GitProject(gitUrl);
			gitProject.setProjectFolder(FolderManager.createProjectFolder(gitProject.getName()));

			if (GitManager.cloneRepo(gitUrl, gitProject.getProjectFolder()) == true) {
				GitManager gitManager = gitProject.getGitManager();
				emailManager.setSubject("Results Project: " + gitProject.getName());
				emailManager.setMessage("");

				gitManager.setRepository(gitProject.getProjectFolder());
				gitManager.generateChangedFilesMap();
				Report report = Report.getNewReportInstance(gitProject.getName());

				int count = 0;
				// each relevant commit
				for (String commitHash : gitManager.getChangeMapKeys()) {
					++count;
					System.out.println("[" + gitProject.getName() + "][" + projectCount + " of " + gitUrlList.size()
							+ " projects]");
					System.out.println("[" + ((100 * count) / gitManager.getChangeMapKeys().size()) + "%]");

					// run all files in this commit
					CommitTask commitTask = new CommitTask(gitProject, commitHash);
					commitTask.runAllFiles();
				}

				try {
					report.closeReport();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static EmailManager setEmailManager() {
		EmailManager emailManager = EmailManager.getInstance();
		emailManager.setFrom(PropertiesManager.getProperty("email.from"),
				PropertiesManager.getProperty("email.from.password"));
		emailManager.setTo(PropertiesManager.getProperty("email.to"));
		return emailManager;
	}
}
