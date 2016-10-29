package refactorAnalysis.file.notation;

import java.io.File;

import gitmanager.properties.PropertiesManager;
import refactorAnalysis.util.ExecScript;

public class CppStatsAnalyze extends NotationAnalyzer {

	private int numberOfDisciplinedFile1 = 0;
	private int numberOfUndisciplinedFile1 = 0;
	private int numberOfDisciplinedFile2 = 0;
	private int numberOfUndisciplinedFile2 = 0;

	@Override
	public boolean analyze(File fileBefore, File fileAfter) {
		int[] valuesBefore = this.runCppStats(fileBefore);
		if (valuesBefore == null)
			return false;
		
		int[] valuesAfter = this.runCppStats(fileAfter);
		if (valuesAfter == null)
			return false;

		this.numberOfDisciplinedFile1 = valuesBefore[1];
		this.numberOfUndisciplinedFile1 = valuesBefore[0];
		this.numberOfDisciplinedFile2 = valuesAfter[1];
		this.numberOfUndisciplinedFile2 = valuesAfter[0];

		return this.checkValues();
	}

	private int[] runCppStats(File file) {
		String src2srcmlPath = PropertiesManager.getProperty("src2srcml.path");
		String dmacrosPath = PropertiesManager.getProperty("dmacros.path");
		String filePath = file.getAbsolutePath();
		String xmlPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".xml";
		File xmlFile = new File(xmlPath);
		
		if (xmlFile.exists()) {
			xmlFile.delete();
		}

		ExecScript runSrc2srcml = new ExecScript();
		runSrc2srcml.addCommand(src2srcmlPath);
		runSrc2srcml.addCommand(filePath);
		runSrc2srcml.execAndRedirectOutput(xmlFile);
		
		if (!xmlFile.exists()) {
			System.out.println("error to create xml file");
			return null;
		}

		ExecScript runDmacros = new ExecScript();
		runDmacros.addCommand(dmacrosPath);
		runDmacros.addCommand(xmlPath);
		return this.computeValues(runDmacros.exec());
	}

	private int[] computeValues(String stringToParse) {
		if (!stringToParse.contains("[\'")) {
			return null;
		}
		
		int[] values = new int[2];
		try {
			String number1 = stringToParse.substring(stringToParse.indexOf("[\'") + 2, stringToParse.indexOf("',"));
			String number2 = stringToParse.substring(stringToParse.indexOf(", '") + 3, stringToParse.indexOf("']"));
			values[0] = Integer.parseInt(number1);
			values[1] = Integer.parseInt(number2);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(stringToParse);
			return null;
		}

		return values;
	}

	private boolean checkValues() {
		if (((this.numberOfDisciplinedFile2 - numberOfDisciplinedFile1) > 0)
				&& ((this.numberOfUndisciplinedFile2 - this.numberOfUndisciplinedFile1) < 0)) {
			return true;
		}

		return false;
	}

}
