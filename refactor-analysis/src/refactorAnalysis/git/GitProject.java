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

	/**
	 * Constructor
	 * 
	 * @param url
	 *            remote url of git project
	 */
	public GitProject(String url) {
		this.setUrl(url);
	}

	/**
	 * get remote url of git project
	 * 
	 * @return remote url of git project
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * set remote url of git project. project name will be set too.
	 * 
	 * @param url
	 *            remote url of git project
	 */
	public void setUrl(String url) {
		this.url = url;
		this.name = this.url.substring(this.url.lastIndexOf("/") + 1);
	}

	/**
	 * get name of git project
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * get main GitManager
	 * 
	 * @return main GitManager
	 */
	public GitManager getGitManager() {
		return this.gitManager;
	}

	/**
	 * get path of main project folder
	 * 
	 * @return path of main project folder
	 */
	public String getProjectFolder() {
		return this.projectFolder;
	}

	/**
	 * set path of main project folder
	 * 
	 * @param projectFolder
	 *            path of main project folder
	 */
	public void setProjectFolder(String projectFolder) {
		this.projectFolder = projectFolder;
	}

	/**
	 * get path of current commit temporary folder
	 * 
	 * @return path of current commit temporary folder
	 */
	public String getCurrentCommitFolder() {
		return currentCommitFolder;
	}

	/**
	 * set path of current commit temporary folder. GitManager of current commit
	 * will be set too.
	 * 
	 * @param currentCommitFolder
	 *            path of current commit temporary folder
	 */
	public void setCurrentCommitFolder(String currentCommitFolder) {
		this.gitManagerCurrentCommit = new GitManager();
		this.gitManagerCurrentCommit.setRepository(currentCommitFolder);
		this.gitManagerCurrentCommit.generateChangedFilesMap();
		this.currentCommitFolder = currentCommitFolder;
	}

	/**
	 * get path of previous commit temporary folder
	 * 
	 * @return path of previous commit temporary folder
	 */
	public String getPreviousCommitFolder() {
		return previousCommitFolder;
	}

	/**
	 * set path of previous commit temporary folder. GitManager of previous
	 * commit will be set too.
	 * 
	 * @param previousCommitFolder
	 *            path of previous commit temporary folder
	 */
	public void setPreviousCommitFolder(String previousCommitFolder) {
		this.gitManagerPreviousCommit = new GitManager();
		this.gitManagerPreviousCommit.setRepository(previousCommitFolder);
		this.gitManagerPreviousCommit.generateChangedFilesMap();
		this.previousCommitFolder = previousCommitFolder;
	}

	/**
	 * get GitManager of current commit
	 * 
	 * @return GitManager of current commit
	 */
	public GitManager getGitManagerCurrentCommit() {
		return gitManagerCurrentCommit;
	}

	/**
	 * get GitManager of previous commit
	 * 
	 * @return GitManager of previous commit
	 */
	public GitManager getGitManagerPreviousCommit() {
		return gitManagerPreviousCommit;
	}

}
