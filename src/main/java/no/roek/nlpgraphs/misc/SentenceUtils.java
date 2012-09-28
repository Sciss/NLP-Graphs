package no.roek.nlpgraphs.misc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.semantics.Compare;
import no.roek.nlpgraphs.document.NLPSentence;
import no.roek.nlpgraphs.document.TextPair;
import edu.stanford.nlp.ling.Word;

public class SentenceUtils {

	public static List<TextPair> getSimilarSentences(String testFile, String trainFile) {
		List<TextPair> textPairs = new ArrayList<>();

		for(NLPSentence testSentence : getSentences(testFile)) {
			for(NLPSentence trainSentence : getSentences(trainFile)) {
				if(isSimilar(testSentence, trainSentence)) {
					textPairs.add(new TextPair(testFile, trainFile, testSentence, trainSentence));
				}
			}
		}

		return textPairs;
	}

	private static boolean isSimilar(NLPSentence querySentence, NLPSentence sentence) {
		Compare cmp = new Compare(querySentence.getText(), sentence.getText());
		return cmp.getResult() > 0.01;
	}

	public static List<NLPSentence> getSentences(String file) {
		try {
			String filename = Paths.get(file).getFileName().toString();
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			StringBuilder wordBuilder = new StringBuilder();
			StringBuilder sentenceBuilder = new StringBuilder();
			List<NLPSentence> sentences = new ArrayList<NLPSentence>();
			List<Word> words = new ArrayList<>();

			int character = 1, offset = 0, sentenceNumber = 1, sentenceStart = 0;
			while((character = reader.read()) != -1) {
				char c = (char) character;

//				sentenceBuilder = stripWhitespaceBeforeText(sentenceBuilder, offset);

				if(isWordDelimiter(c)) {
					wordBuilder = createWord(wordBuilder, words, offset);
				}

				if(!isNewLine(c)) {
					wordBuilder.append((char) c);
					sentenceBuilder.append((char) c);
				}else {
					if(!isSentenceDelimiter(c)) {
						wordBuilder = createWord(wordBuilder, words, offset);
						sentenceBuilder.append(" ");
					}
				}

				if(isSentenceDelimiter(c)) {
					String previousWord;
					if(words.size()== 0) {
						previousWord = "placeholder";
					}else {
						previousWord = words.get(words.size()-1).word();
					}
					if((sentenceBuilder.toString().trim().length() > 1) && !isWordWithPunctation(previousWord)) {
						int sentenceLength = offset - sentenceStart;
						sentences.add(new NLPSentence(filename, sentenceNumber, sentenceStart, sentenceLength, sentenceBuilder.toString(), words));
					}
					createWord(wordBuilder, words, offset);
					words = new ArrayList<Word>();
					sentenceBuilder = new StringBuilder();
					wordBuilder = new StringBuilder();
					sentenceNumber++;
					sentenceStart = offset;
				}
				offset++;
			}
			if(wordBuilder.toString().trim().length()>0) {
				words.add(new Word(wordBuilder.toString()));
				int sentenceLength = offset - sentenceStart;
				sentences.add(new NLPSentence(filename, sentenceNumber, offset, sentenceLength, sentenceBuilder.toString(), words));
				sentenceNumber++;
			}
			reader.close();

			return sentences;
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	private static StringBuilder stripWhitespaceBeforeText(StringBuilder sentenceBuilder, int currentOffset) {
//		if(sentenceBuilder.toString().length() > 0) {
//			if(sentenceBuilder.toString().trim().length() == 0) {
//				return new StringBuilder();
//			}
//		}
//		return sentenceBuilder;
//	}

	private static StringBuilder createWord(StringBuilder wordBuilder, List<Word> words, int offset) {
		if(wordBuilder.toString().trim().length() > 0) {
			words.add(new Word(wordBuilder.toString().trim(), offset-wordBuilder.length(), offset));
			return new StringBuilder();
		}

		return wordBuilder;
	}



	private static boolean isNewLine(char c) {
		return (c == '\n' || c == '\r');
	}

	private static boolean isSentenceDelimiter(char c) {
		return c == '.' || c == '!' || c == '?';
	}

	private static boolean isWordDelimiter(char c ) {
		return !Character.isLetter(c) && !Character.isDigit(c) || (c == 10) || (c == 13);
	}

	//	private static boolean isPartOfWord(char c) {
	//		return Character.isLetter(c) || Character.isDigit(c);
	//	}

	private static boolean isWordWithPunctation(String s) {
		return s.equalsIgnoreCase("Mr") || s.equalsIgnoreCase("Mrs") || s.equalsIgnoreCase("ca");
	}
	
	
	public static boolean isAlmostEqual(int a, int b) {
		return Math.abs(a-b)< 3;
	}
}
