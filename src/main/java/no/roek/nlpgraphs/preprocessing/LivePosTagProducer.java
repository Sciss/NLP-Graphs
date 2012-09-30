package no.roek.nlpgraphs.preprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import no.roek.nlpgraphs.concurrency.PlagiarismJob;
import no.roek.nlpgraphs.document.NLPSentence;
import no.roek.nlpgraphs.document.TextPair;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class LivePosTagProducer extends Thread {

	private final BlockingQueue<PlagiarismJob> queue;
	private final BlockingQueue<PlagiarismJob> parseQueue;
	private MaxentTagger tagger;

	public LivePosTagProducer(BlockingQueue<PlagiarismJob> queue, BlockingQueue<PlagiarismJob> parseQueue, String taggerParams){
		this.queue = queue;
		this.parseQueue = parseQueue;
		try {
			this.tagger = new MaxentTagger(taggerParams);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean running = true;
		while(running) {
			try {
				PlagiarismJob job = queue.take();
				if(job.isLastInQueue()) {
					running = false;
					break;
				}
				parseQueue.put(getPosTags(job));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public PlagiarismJob getPosTags(PlagiarismJob job) {
		//TODO: this might not update correctly. might have to create a new object? alternatively a new attr "taggedTextPairs" or something
		for(TextPair pair : job.getTextPairs()) {
			pair.getTestSentence().setPostags(getMaltString(pair.getTestSentence()));
			pair.getTrainSentence().setPostags(getMaltString(pair.getTrainSentence()));
		}

		return job;
	}

	public String[] getMaltString(NLPSentence sentence) {
		List<TaggedWord> taggedSentence = tagger.tagSentence(sentence.getWords());

		List<String> temp = new ArrayList<>();
		int i = 1;
		for (TaggedWord token : taggedSentence) {
			temp.add(i+"\t"+token.word()+"\t"+"_"+"\t"+token.tag()+"\t"+token.tag()+"\t"+"_");
            i++;
		}

		return temp.toArray(new String[0]);
	}

//	public DocumentFile tagFile(DocumentFile file, boolean isLastInQueue) {
//		file.setLastInQueue(isLastInQueue);
//		file.setSentences(SentenceUtils.getSentences(file.getPath().toString()));
//
//		int sentenceNumber = 1;
//		for (NLPSentence sentence : file.getSentences()) {
//			try {
//				List<TaggedWord> taggedSentence = tagger.tagSentence(sentence.getWords());
//				List<String> temp = new ArrayList<>();
//
//				int i = 1;
//				for (TaggedWord token : taggedSentence) {
//					temp.add(i+"\t"+token.word()+"\t"+"_"+"\t"+token.tag()+"\t"+token.tag()+"\t"+"_");
//					i++;
//				}
//				sentenceNumber++;
//				sentence.setPostags(temp.toArray(new String[0]));
//			}catch (IndexOutOfBoundsException e) {
//				e.printStackTrace();
//			}
//		} 
//
//		return file;
//	}
}
