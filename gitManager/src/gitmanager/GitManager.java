package gitmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import gitmanager.directoryManager.DirectoryManager;
import gitmanager.report.GeneralReport;

public class GitManager {

    private Iterable<RevCommit> commitList = null;
    private Map<String, RevCommit> commitMap = new HashMap<>();
    private Repository repository;
    private Git git;
    private Hashtable<String, ArrayList<String>> changeMap;
    private ArrayList<String> changeMapKeys;
    private boolean changeMapGenerated = false;

    // remotePath; //Ex git@github.com:me/mytestrepo.git
    // localPath; //Ex /home/repos/...
    public GitManager() {
        changeMap = new Hashtable<String, ArrayList<String>>();
        changeMapKeys = new ArrayList<String>();
    }

    public static boolean cloneRepo(String remotePath, String localPath) {
        try {
            File projectFile = new File(localPath);
            if (projectFile.exists()) {
                GeneralReport.getInstance().reportInfo("Deletando: " + localPath);
                DirectoryManager.getInstance().deleteFile(projectFile);
            }
            GeneralReport.getInstance().reportInfo("Clonando: " + remotePath);
            Git.cloneRepository().setURI(remotePath).setDirectory(projectFile).call();
            return true;
        } catch (Exception e) {
            GeneralReport.getInstance().reportError("Não foi possível clonar repositório");
            return false;
        }

    }

    /**
     * get commit object
     * 
     * @param hashKey
     *            hash of the commit target
     * @return the commit object
     */
    public RevCommit getCommit(String hashKey) {
        return this.commitMap.get(hashKey);
    }

    public void setRepository(String path) {
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            File localRepo = new File(path + System.getProperty("file.separator") + ".git");
            
            repositoryBuilder.setGitDir(localRepo);
            repositoryBuilder.readEnvironment();

            repository = repositoryBuilder.build();
            git = new Git(repository);

        } catch (Exception e) {
            GeneralReport.getInstance().reportError("Não foi possível acessar repositório" + path);
        }
    }

    public Repository getRepository() {
        return repository;
    }

    public Iterable<RevCommit> getCommitList() {
        if (this.commitList == null) {
            try {
                this.commitList = git.log().call();
            } catch (Exception e) {
                GeneralReport.getInstance().reportError("Não foi possível acessar commits do repositório");
            }
        }

        return commitList;
    }

    public ArrayList<String> getCommitHashList() {
        ArrayList<String> commitIds = new ArrayList<String>();
        for (RevCommit commit : this.getCommitList()) {
            commitIds.add(commit.getId().getName());
        }
        return commitIds;
    }

    public void generateChangedFilesMap() {
        if (this.changeMapGenerated == true)
            return;

        String commitId = "";
        try {
            RevWalk rw = new RevWalk(repository);
            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);

            for (RevCommit commit : getCommitList()) {
                try {
                    commitId = commit.getId().getName();
                    this.commitMap.put(commitId, commit);
                    
                    if (commit.getParentCount() == 0) {
						continue;
					}
                    RevCommit parent = rw.parseCommit(commit.getParent(0).getId());

                    List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
                    ArrayList<String> changedFiles = new ArrayList<String>();
                    for (DiffEntry diff : diffs) {

                        if ((diff.getChangeType().name().equals("MODIFY") || diff.getChangeType().name().equals("ADD"))
                                && diff.getNewPath().substring(diff.getNewPath().length() - 2).equals(".c")) {
                            changedFiles.add(diff.getNewPath());
                        }
                    }
                    if (changedFiles.size() > 0) {
                        changeMap.put(commitId, changedFiles);
                        changeMapKeys.add(commitId);
                    }
                } catch (Exception e) {
                    GeneralReport.getInstance()
                            .reportError("Não foi possível acessar as diferenças do commit " + commitId);
                }
            }

            rw.close();
            df.close();

        } catch (Exception e) {
            GeneralReport.getInstance()
                    .reportError("Não foi possível acessar as diferenças nos commits do repositório");
        }
        this.changeMapGenerated = true;
    }

    public Hashtable<String, ArrayList<String>> getChangedFilesMap() {
        generateChangedFilesMap();
        return changeMap;
    }

    public ArrayList<String> getChangeMapKeys() {
        generateChangedFilesMap();
        return changeMapKeys;
    }

    public boolean checkout(String hash) {
    	if (hash == null || hash.isEmpty()) {
			return false;
		}
        try {
            git.checkout().setName(hash).call();
            return true;
        } catch (Exception e) {
            
        }
        try {
            git.reset().setMode(ResetCommand.ResetType.HARD).call();
            git.checkout().setName(hash).call();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            GeneralReport.getInstance().reportError("Não foi possível dar o checkout no commit: " + hash);
            return false;
        }
    }
}
