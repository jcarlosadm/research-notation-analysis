package refactorAnalysis.folderManager;

import java.io.File;

public abstract class FolderManager {

	private static final String OUTPUT_FOLDER = "output";
	private static final String PROJECTS_FOLDER = "projects";
	private static final String REPORTS_FOLDER = "reports";
	private static final String CACHE_FOLDER = "cache";
	private static final String TEMP_FOLDER = "temp";

	private FolderManager() {
	}

	public static void createAllMainFolders() {
		createOutputFolder();
		createProjectsFolder();
		createReportsFolder();
		createCacheFolder();
		createTempFolder();
	}

	public static String createProjectFolder(String projectName) {
		return createSubfolder(PROJECTS_FOLDER, projectName);
	}

	public static String createTempProjectFolder(String projectName) {
		return createSubfolder(TEMP_FOLDER, projectName);
	}
	
	public static String getReportFolder() {
		return OUTPUT_FOLDER + File.separator + REPORTS_FOLDER;
	}

	private static String createSubfolder(String firstFolder, String subfolder) {
		File folder = new File(OUTPUT_FOLDER + File.separator + firstFolder + File.separator + subfolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		return folder.getAbsolutePath();
	}

	private static void createOutputFolder() {
		File file = new File(OUTPUT_FOLDER);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	private static void createProjectsFolder() {
		createFolder(PROJECTS_FOLDER);
	}

	private static void createReportsFolder() {
		createFolder(REPORTS_FOLDER);
	}

	private static void createCacheFolder() {
		createFolder(CACHE_FOLDER);
	}

	private static void createTempFolder() {
		createFolder(TEMP_FOLDER);

	}

	private static void createFolder(String folderpath) {
		File file = new File(OUTPUT_FOLDER + File.separator + folderpath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
