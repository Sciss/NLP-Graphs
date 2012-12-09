package no.roek.nlpgraphs.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import no.roek.nlpgraphs.document.PlagiarismPassage;

import org.apache.commons.io.IOUtils;
import org.maltparser.core.helper.HashSet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Fileutils {

	public static void writeToFile(String filename, String[] lines) {
		createParentFolders(filename);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));

			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
		}catch ( IOException ioe ) {
			ioe.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static void writeToFile(String filename, String text) {
		createParentFolders(filename);
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(Paths.get(filename), Charset.forName("UTF-8"));
			writer.write(text);
		}catch ( IOException ioe ) {
			ioe.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static void mkdir(String dir) {
		File d = new File(dir);
		if(!d.exists()) {
			d.mkdir();
		}
	}

	public static void createParentFolders(String filename) {
		File f = new File(filename);
		if(f.getParentFile() != null) {
			new File(filename).getParentFile().mkdirs();
		}
	}

	public static File[] getFiles(Path dir) {
		return dir.toFile().listFiles();
	}

	public static File[] getFiles(String dir) {
		return Paths.get(dir).toFile().listFiles();
	}

	public static int getFileCount(String dir) {
		return new File(dir).list().length;
	}

	public static List<File> getFiles(Path dir, Path baseDir) {
		List<File> tasks = new ArrayList<File>();

		for (File file : getFiles(dir)) {
			if(file.isFile() && file.getName().endsWith(".txt")) {
				tasks.add(file);
			}else if(file.isDirectory()) {
				tasks.addAll(getFiles(file.toPath(), baseDir));
			}
		}
		return tasks;
	}

	
	public static Set<String> getFileNames(String dir, String newSuffix) {
		Set<String> filenames = new HashSet<>();
		File[] files = getFiles(Paths.get(dir));
		for (File file : files) {
			filenames.add(replaceFileExtention(file.getName(), newSuffix));
		}
		
		return filenames;
	}
	public static Set<String> getFileNames(String  dir) {
		Set<String> filenames = new HashSet<>();
		File[] files = getFileList(dir);
		for (File file : files) {
			filenames.add(file.getName());
		}
		
		return filenames;
	}
	
	public static File[] getFileList(String dir) {
		return getFileList(Paths.get(dir));
	}

	public static File[] getFileList(Path dir) {
		return getFiles(dir, dir).toArray(new File[0]);
	}

	
	public static String[] getFilesNotDone(Set<String> files, String outDir, String fileExtention) {
		List<String> out = new ArrayList<String>();
		for (String file : files) {
			String outFile = outDir+file;
			if(fileExtention != null) {
				outFile = Fileutils.replaceFileExtention(outFile, fileExtention);
			}

			if(!new File(outFile).exists()) {
				out.add(file);
			}
		}

		return out.toArray(new String[0]);
	}

	
	public static String[] getFilesNotDone(List<String> files, String outDir, String fileExtention) {
		List<String> out = new ArrayList<String>();
		for (String file : files) {
			String outFile = outDir+file;
			if(fileExtention != null) {
				outFile = Fileutils.replaceFileExtention(outFile, fileExtention);
			}

			if(!new File(outFile).exists()) {
				out.add(file);
			}
		}

		return out.toArray(new String[0]);
	}

	public static String[] getFilesNotDone(List<String> files, String outDir) {
		return getFilesNotDone(files, outDir, null);
	}



	public static File[][] getChunks(File[] files, int n) {
		List<File[]> chunks = new ArrayList<>();

		int fileCount = files.length;
		int chunksize = fileCount / n;
		int i = 0;
		while(i < fileCount) {
			if(i+chunksize*2 <= fileCount) {
				chunks.add(Arrays.copyOfRange(files, i, i+chunksize));
				i += chunksize;
			}else{
				chunks.add(Arrays.copyOfRange(files, i, fileCount));
				i = fileCount;
			}
		}

		return chunks.toArray(new File[0][0]);
	}

	public static Path[][] getChunks(Path[] files, int n) {
		List<Path[]> chunks = new ArrayList<>();

		int fileCount = files.length;
		int chunksize = fileCount / n;
		int i = 0;
		while(i < fileCount) {
			if(i+chunksize*2 <= fileCount) {
				chunks.add(Arrays.copyOfRange(files, i, i+chunksize));
				i += chunksize;
			}else{
				chunks.add(Arrays.copyOfRange(files, i, fileCount));
				i = fileCount;
			}
		}

		return chunks.toArray(new Path[0][0]);
	}

	//	public static <T> List<T[]> getChunks(T[] files, int n) {
	//		List<T[]> chunks = new ArrayList<>();
	//
	//		int fileCount = files.length;
	//		int chunksize = fileCount / n;
	//		int i = 0;
	//
	//		while(i < fileCount) {
	//			if(i+chunksize*2 <= fileCount) {
	//				chunks.add(Arrays.copyOfRange(files, i, i+chunksize));
	//				i += chunksize;
	//			}else{
	//				chunks.add(Arrays.copyOfRange(files, i, fileCount));
	//				i = fileCount;
	//			}
	//		}
	//
	//		return chunks;
	//	}

	public static List<String> getTextLines(String path) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lines;
	}
	
	public static String getText(Path path) {
		try {
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
			StringBuffer sb = new StringBuffer();
			for (String line : lines) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String replaceFileExtention(String filename, String extention) {
		int i = filename.lastIndexOf(".");
		return filename.substring(0, i) +"."+ extention;
	}

	public static List<PlagiarismPassage> getPassages(String candretFile) {
		List<PlagiarismPassage> passages = new ArrayList<>();
		JsonReader jsonReader = null;
		try {
			jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(candretFile)));
			JsonParser parser = new JsonParser();
			JsonObject fileObject = parser.parse(jsonReader).getAsJsonObject();
			for(JsonElement temp : fileObject.get("passages").getAsJsonArray()) {
				JsonObject jsonPassage = temp.getAsJsonObject();
				String trainFile = jsonPassage.get("trainFile").getAsString();
				int trainSentence = jsonPassage.get("trainSentence").getAsInt();
				String testFile = jsonPassage.get("testFile").getAsString();
				int testSentence = jsonPassage.get("testSentence").getAsInt();
				double similarity = jsonPassage.get("candretScore").getAsDouble();

				passages.add(new PlagiarismPassage(trainFile, trainSentence, testFile, testSentence, similarity));
			}
		
		}catch(IOException e) {
			e.printStackTrace();
		}finally{
			try{
				jsonReader.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return passages;
	}
}
