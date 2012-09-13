package nlpgraphs;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import nlpgraphs.document.DocumentFile;
import nlpgraphs.graph.Graph;
import nlpgraphs.misc.Fileutils;
import nlpgraphs.misc.GraphUtils;
import nlpgraphs.postprocessing.PlagiarismWorker;
import nlpgraphs.preprocessing.DependencyParser;
import nlpgraphs.preprocessing.PosTagProducer;

public class App {
	
	//TODO: create config file for such settings?
	private static String PARSED_FILES_DIR = "parsed_files/";
	public static void main(String[] args) {
		
		preprocess(args[0]);
		postProcess(args[0]+"source-documents/", PARSED_FILES_DIR+"source-documents/", PARSED_FILES_DIR+"suspicious-documents/");
	}
	
	private static void preprocess(String input) {
		BlockingQueue<DocumentFile> queue = new LinkedBlockingQueue<DocumentFile>();
		DocumentFile[] files = Fileutils.getUnparsedFiles(Paths.get(input), PARSED_FILES_DIR);
		
		int cpuCount = Runtime.getRuntime().availableProcessors();
		int threadCount = 1;
		if((files.length > 10 && cpuCount > 4)) {
			threadCount = (cpuCount < 10) ? 2 : 10;
		}
		
		DependencyParser consumer  = new DependencyParser(queue, "-c engmalt.linear-1.7.mco -m parse -w . -lfi parser.log", PARSED_FILES_DIR, threadCount);
		
		DocumentFile[][] chunks = Fileutils.getChunks(files, threadCount);
		System.out.println("thread count: "+threadCount+" chunks: "+chunks.length);

		if(threadCount > chunks.length) {
			threadCount = chunks.length;
		}
		
		for (int i = 0; i < threadCount; i++) {
			PosTagProducer producer = new PosTagProducer(queue, chunks[i], "english-left3words-distsim.tagger");
			new Thread(producer, "PosTagProducer: "+i).start();
		}

		new Thread(consumer, "maltparserConsumer").start();
    }
	
	private static void postProcess(String originalTrainDir, String trainDir, String testDir) {
		File[] trainFiles = Fileutils.getFiles(Paths.get(trainDir));
		List<Graph> trainGraphs = new ArrayList<>();
		for (File file : trainFiles) {
			trainGraphs.add(GraphUtils.parseGraph(file.toPath()));
		}
		File[] test = Fileutils.getFiles(Paths.get(testDir));

		int cpuCount = Runtime.getRuntime().availableProcessors() - 2;
		int threads = cpuCount;
		if(test.length < cpuCount) {
			threads = test.length;
		}else if(cpuCount < 2) {
			cpuCount = 1;
		}

		System.out.println("using "+cpuCount+" threads");
		PlagiarismWorker worker = new PlagiarismWorker(trainGraphs.toArray(new Graph[0]), Arrays.asList(test), threads, Paths.get(originalTrainDir));

		ForkJoinPool pool = new ForkJoinPool();
		List<String> results = pool.invoke(worker);

		Fileutils.writeToFile("plag.txt", results.toArray(new String[0]));
	}
}
