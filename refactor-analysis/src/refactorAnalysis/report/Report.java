package refactorAnalysis.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import refactorAnalysis.folderManager.FolderManager;
import refactorAnalysis.report.email.EmailManager;

public class Report {

	private BufferedWriter bufferedWriter = null;
	
	private static Report instance = null;
	
	private Report() {
	}
	
	public static Report getCurrentInstance(String projectName) {
		if (instance == null) {
			getNewReportInstance(projectName);
		}
		
		return instance;
	}
	
	public static Report getNewReportInstance(String projectName) {
		File reportFile = new File(FolderManager.getReportFolder() + File.separator + projectName + ".html");
		
		Report report = new Report();
		
		try {
			report.bufferedWriter = new BufferedWriter(new FileWriter(reportFile));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		instance = report;
		
		return instance;
	}
	
	public void write(String line) throws Exception {
		this.bufferedWriter.write(System.lineSeparator()+"<br>"+line);
	}
	
	public void writeLink(String link, String text) throws Exception {
		String formatedLink = "<a href=\"";
		formatedLink += link +"\" target=\"_blank\">"+text+"</a>";
		this.write(formatedLink);
		
		EmailManager emailManager = EmailManager.getInstance();
		emailManager.appendMessage(formatedLink + "<br>");
		emailManager.sendMessage();
	}
	
	public void closeReport() throws Exception {
		this.bufferedWriter.close();
	}
}
