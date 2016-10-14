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
		int[] valuesAfter = this.runCppStats(fileAfter);
		if (valuesBefore == null || valuesAfter == null) {
			return false;
		}

		this.numberOfDisciplinedFile1 = valuesBefore[1];
		this.numberOfUndisciplinedFile1 = valuesBefore[0];
		this.numberOfDisciplinedFile2 = valuesAfter[1];
		this.numberOfUndisciplinedFile2 = valuesAfter[0];

		return this.checkValues();
	}

	private int[] runCppStats(File file) {
		String src2srcmlPath = PropertiesManager.getPropertie("src2srcml.path");
		String dmacrosPath = PropertiesManager.getPropertie("dmacros.path");
		String filePath = file.getAbsolutePath();
		String xmlPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".xml";

		ExecScript runSrc2srcml = new ExecScript();
		runSrc2srcml.addCommand(src2srcmlPath);
		runSrc2srcml.addCommand(filePath);
		runSrc2srcml.execAndRedirectOutput(new File(xmlPath));

		ExecScript runDmacros = new ExecScript();
		runDmacros.addCommand(dmacrosPath);
		runDmacros.addCommand(xmlPath);
		return this.computeValues(runDmacros.exec());
	}

	private int[] computeValues(String stringToParse) {
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
