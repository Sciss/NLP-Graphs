package no.roek.nlpgraphs.search;

import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

import no.roek.nlpgraphs.concurrency.PlagiarismJob;
import no.roek.nlpgraphs.concurrency.SentenceRetrievalJob;
import no.roek.nlpgraphs.misc.ConfigService;

public class SentenceRetrievalWorker extends Thread {

	private BlockingQueue<SentenceRetrievalJob> queue;
	private BlockingQueue<PlagiarismJob> parseQueue;
	private String trainDir, testDir, dataDir, parsedDir;
	

	public SentenceRetrievalWorker(BlockingQueue<SentenceRetrievalJob> queue, BlockingQueue<PlagiarismJob> parseQueue) {
		this.queue = queue;
		this.parseQueue = parseQueue;
		ConfigService cs = new ConfigService();
		this.trainDir = cs.getTrainDir();
		this.testDir = cs.getTestDir();
		this.dataDir = cs.getDataDir();
		this.parsedDir = cs.getParsedFilesDir();
	}

	@Override
	public void run() {
		boolean running = true;

		while(running) {
			try {
				SentenceRetrievalJob job = queue.take();
				if(job.isLastInQueue()) {
					running = false;
					System.out.println(Thread.currentThread().getName()+ " encountered the last job in queue, stopping.");
					break;
				}
				parseQueue.put(getParseJob(job));
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}
	}

	public PlagiarismJob getParseJob(SentenceRetrievalJob job) {
		PlagiarismJob plagJob = new PlagiarismJob(job.getFile());
		for (String simDoc : job.getSimilarDocs()) {
			
			plagJob.addAllTextPairs(SentenceUtils.getSimilarSentences(dataDir, parsedDir, testDir, trainDir, job.getFilename(), Paths.get(simDoc).getFileName().toString()));
		}
		
		return plagJob;
	}
}
