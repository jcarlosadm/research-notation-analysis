package gitmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import gitmanager.properties.PropertiesManager;
import gitmanager.report.GeneralReport;

public class RepoManager {
    private ArrayList<String> repoList;
    private static RepoManager instance;
    private RepoManager() {
        repoList = new ArrayList<String>();
        try {
            File file = new File(PropertiesManager.getProperty("path.repolist"));

            BufferedReader br = new BufferedReader(new FileReader(file));
            for(String line; (line = br.readLine()) != null; ) {
                // process the line.
                if (line.contains("github.com/")) {
                    String repo = line.substring(line.indexOf("github.com/"));
                    repo = repo.replaceAll(",", "/");
                    repo = repo.replaceAll(" ", "/");
                    String[] tempRepo = repo.split("/");
                    if (tempRepo[1] != null && tempRepo[2] != null) {
                        repo = "https://github.com/" + tempRepo[1] + "/" + tempRepo[2] + ".git";
                        repoList.add(repo);
                    }
                }
            }
            br.close();

        } catch (Exception e) {
            GeneralReport.getInstance().reportError("Não foi possível acessar o arquivo de repositórios");
        }
    }

    public static RepoManager getInstance(){
        if (instance == null) {
            instance = new RepoManager();
        }
        return instance;
    }

    public ArrayList<String> getRepoList() {
        GeneralReport.getInstance().reportInfo("Gerando lista de repositórios");
        return repoList;
    }

    public static void main(String args[]){
        RepoManager teste = RepoManager.getInstance();
        System.out.println(teste.getRepoList());
    }
}
