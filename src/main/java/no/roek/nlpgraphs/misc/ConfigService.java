package no.roek.nlpgraphs.misc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigService {
	
	public static String getProperty(String property) {
		Properties configFile = new Properties();
		InputStream is;
		try {
			is = new FileInputStream("app.properties");

			configFile.load(is);

			return configFile.getProperty(property);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getParsedFilesDir() {
		return getProperty("PARSED_DIR");
	}
	
	public static String getDataDir() {
		return getProperty("DATA_DIR");
	}
	
	public static String getTestDir() {
		return getProperty("TEST_DIR");
	}
	
	public static String getTrainDir() {
		return getProperty("TRAIN_DIR");
	}
	
	public static String getAnnotationsDir() {
		return getProperty("ANNOTATIONS_DIR");
	}
	
	public static String getResultsDir() {
		return getProperty("RESULTS_DIR");
	}
	
	public static int getDocumentRecall() {
		return Integer.parseInt(getProperty("DOCUMENT_RECALL"));
	}
	
	public static int getPlagiarismThreshold() {
		return Integer.parseInt(getProperty("PLAGIARISM_THRESHOLD"));
	}
	
	public static String getMaltParams() {
		return getProperty("MALT_PARAMS");
	}
	
	public static String getPOSTaggerParams() {
		return getProperty("POSTAGGER_PARAMS");
	}
}
