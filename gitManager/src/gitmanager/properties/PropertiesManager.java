package gitmanager.properties;

import gitmanager.report.GeneralReport;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
    private static Properties properties;
    private static String path = "general.properties";

    public static String getProperty(String propertie) {
        try {
            if (properties == null) {
                properties = new Properties();
                FileInputStream file = new FileInputStream(path);
                properties.load(file);
            }
        } catch (Exception e) {
            GeneralReport.getInstance().reportError("Não foi possível carregar arquivo de propeties");
        }

        return properties.getProperty(propertie);
    }

    public static void setNewPath(String newpath) {
        path = newpath;
        properties = null;
    }

    public static void main(String args[]) throws IOException {
        System.out.println("************Teste de leitura do arquivo de propriedades************");
        System.out.println("path = " + PropertiesManager.getProperty("path"));
        System.out.println("path = " + PropertiesManager.getProperty("path.src2xml"));
    }
}
