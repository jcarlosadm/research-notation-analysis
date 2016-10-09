package refactorAnalysis.folderManager;

import java.io.File;

public class FolderManager {

    private static final String OUTPUT_FOLDER = "output";
    private static final String PROJECTS_FOLDER = "projects";
    private static final String REPORTS_FOLDER = "reports";
    private static final String CACHE_FOLDER = "cache";

    public static void createAllMainFolders() {
        createOutputFolder();
        createProjectsFolder();
        createReportsFolder();
        createCacheFolder();
    }

    public static String createProjectFolder(String projectName) {
        File projectFolder = new File(OUTPUT_FOLDER + File.separator + PROJECTS_FOLDER + File.separator + projectName);
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        
        return projectFolder.getAbsolutePath();
    }

    private static void createOutputFolder() {
        File file = new File(OUTPUT_FOLDER);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private static void createProjectsFolder() {
        File file = new File(OUTPUT_FOLDER + File.separator + PROJECTS_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static void createReportsFolder() {
        File file = new File(OUTPUT_FOLDER + File.separator + REPORTS_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static void createCacheFolder() {
        File file = new File(OUTPUT_FOLDER + File.separator + CACHE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
