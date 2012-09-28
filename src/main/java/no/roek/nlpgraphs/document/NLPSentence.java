package no.roek.nlpgraphs.document;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import no.roek.nlpgraphs.misc.SentenceUtils;

import org.joda.time.chrono.IslamicChronology;
import org.json.JSONException;
import org.json.JSONObject;


import edu.stanford.nlp.ling.Word;

public class NLPSentence {
	
	private int number, start, length;
	private String text, filename;
	private String[] postags;
	private List<Word> words;
	
	public NLPSentence(String filename, int number, int start, int length, String text, String[] postags) {
		this(filename, number, start, length, text);
		this.postags = postags;
	}
	
	public NLPSentence(String filename, int number, int start, int length, String text, List<Word> words) {
		this(filename, number, start, length, text);
		this.words = words;
	}
	
	public NLPSentence(String filename, int number, int start, int length, String text) {
		this.filename = filename;
		this.number = number;
		this.start = start;
		this.text = text;
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}

	public int getNumber() {
		return number;
	}

	public int getStart() {
		return start;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return text;
	}

	public String[] getPostags() {
		return postags;
	}
	
	public void setPostags(String[] postags) {
		this.postags = postags;
	}

	public List<Word> getWords() {
		return words;
	}
	
	public void addWord(Word word) {
		words.add(word);
	}

	public String getFilename() {
		return filename;
	}
	
	public String getRelativePath() {
		Path file = Paths.get(filename);
		String outfilename = file.getFileName().toString().replace(".txt", "");
		String parentDir = file.getParent().getFileName().toString();
		return parentDir+"/"+outfilename+"/"+outfilename+"_"+number;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject jsonSentence = new JSONObject();
		jsonSentence.put("sentenceNumber", number);
		jsonSentence.put("originalText", text);
		jsonSentence.put("offset", start);
		jsonSentence.put("length", getLength());

		return jsonSentence;
	}
	
	public boolean matchesPlagSourceRef(PlagiarismReference ref) {
		int refLen = Integer.parseInt(ref.getSourceLength());
		int refOffset = Integer.parseInt(ref.getSourceOffset());
		
		return SentenceUtils.isAlmostEqual(refOffset, start) && SentenceUtils.isAlmostEqual(refLen, length) && ref.getSourceReference().equals(getFilename());
	}
	
	public boolean matchesPlagSuspiciousRef(PlagiarismReference ref) {
		int refLen = Integer.parseInt(ref.getLength());
		int refOffset = Integer.parseInt(ref.getOffset());
		
		return SentenceUtils.isAlmostEqual(refOffset, start) && SentenceUtils.isAlmostEqual(refLen, length) && ref.getFilename().equals(getFilename());
	}
}
