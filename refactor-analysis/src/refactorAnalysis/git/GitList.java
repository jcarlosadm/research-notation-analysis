package refactorAnalysis.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import gitmanager.properties.PropertiesManager;

public class GitList {

	/**
	 * Get a list of git project url's
	 * 
	 * @return list of git project url's
	 */
	public List<String> getGitUrlList() {
		List<String> urlList = new ArrayList<>();
		BufferedReader bReader = null;

		try {
			String gitListFilePath = PropertiesManager.getProperty("git.list");
			File gitListFile = new File(gitListFilePath);
			bReader = new BufferedReader(new FileReader(gitListFile));

			String line = "";
			while ((line = bReader.readLine()) != null) {
				if (!line.isEmpty())
					urlList.add(line);
			}

			bReader.close();
		} catch (Exception e) {
			return null;
		}

		return urlList;
	}

}
