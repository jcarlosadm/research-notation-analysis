package refactorAnalysis.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import gitmanager.GitManager;

public class GitExplorer {
	private GitManager gitManager;

	public GitExplorer(GitManager gitManager) {
		this.gitManager = gitManager;
	}

	/**
	 * Get a hash of a previous commit
	 * 
	 * @param commitHash
	 *            hash of current commit
	 * @return hash of a previous commit
	 */
	public String getPreviousCommit(String commitHash) {
		RevCommit revCommit = this.gitManager.getCommit(commitHash);
		if (revCommit.getParentCount() == 0) {
			return null;
		}
		return revCommit.getParent(0).getId().getName();
	}

	/**
	 * Get a list of file paths of a commit
	 * 
	 * @param commitHash
	 *            hash of commit
	 * @param endwithFilter
	 *            a filter, like ".c"
	 * @return List of file paths of this commit
	 * @throws Exception
	 */
	public List<String> getFilesOfCommit(String commitHash, String endwithFilter) throws Exception {
		List<String> filepathList = new ArrayList<>();

		RevCommit revCommit = this.gitManager.getCommit(commitHash);
		if (revCommit == null)
			return null;

		TreeWalk treeWalk = new TreeWalk(this.gitManager.getRepository());
		try {
			treeWalk.addTree(revCommit.getTree());
			treeWalk.setRecursive(true);

			while (treeWalk.next()) {
				if (treeWalk.getPathString().endsWith(endwithFilter)) {
					filepathList.add(treeWalk.getPathString());
				}
			}

			treeWalk.close();
		} catch (IOException e) {
			e.printStackTrace();
			treeWalk.close();
			return null;
		}

		return filepathList;
	}

	/**
	 * get path of file in previous commit
	 * 
	 * @param filepathCurrentCommit
	 *            file path in current commit
	 * @param currentCommit
	 *            current commit
	 * @param endwithFilter
	 *            a filter, like ".c"
	 * @return path of file in previous commit, or null
	 * @throws Exception
	 */
	public String getFilePathOfPreviousCommit(String filepathCurrentCommit, String currentCommit, String endwithFilter)
			throws Exception {
		String previousCommit = this.getPreviousCommit(currentCommit);
		List<String> files = this.getFilesOfCommit(previousCommit, endwithFilter);
		if (files == null)
			return null;
		
		String filename = filepathCurrentCommit.substring(filepathCurrentCommit.lastIndexOf(File.separator) + 1);
		for (String file : files) {
			if (file.contains(filename)) {
				return file;
			}
		}

		return null;
	}
	
	public String searchFilePathInCurrentCommit(String filename, String commit, String endwithFilter)
			throws Exception {
		List<String> files = this.getFilesOfCommit(commit, endwithFilter);
		if (files == null)
			return null;
		
		for (String filepath : files) {
			if (filepath.contains(filename)) {
				return filepath;
			}
		}
		
		return null;
	}
}
