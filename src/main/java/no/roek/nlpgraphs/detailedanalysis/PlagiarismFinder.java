package no.roek.nlpgraphs.detailedanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.roek.nlpgraphs.document.PlagiarismPassage;
import no.roek.nlpgraphs.graph.Graph;
import no.roek.nlpgraphs.misc.ConfigService;
import no.roek.nlpgraphs.misc.DatabaseService;
import no.roek.nlpgraphs.misc.EditWeightService;
import no.roek.nlpgraphs.misc.GraphUtils;
import no.roek.nlpgraphs.misc.XMLUtils;

public class PlagiarismFinder {

	private String parsedDir, testDir, trainDir, resultsDir;
	private double plagiarismThreshold;
	private DatabaseService db;
	private Map<String, Double> posEditWeights, deprelEditWeights;

	public PlagiarismFinder(DatabaseService db) {
		this.db = db;
		ConfigService cs = new ConfigService();
		parsedDir = cs.getParsedFilesDir();
		testDir =cs.getTestDir();
		trainDir = cs.getTrainDir();
		resultsDir = cs.getResultsDir();
		plagiarismThreshold = cs.getPlagiarismThreshold();
		posEditWeights = EditWeightService.getEditWeights(cs.getPosSubFile(), cs.getPosInsdelFile());
		deprelEditWeights = EditWeightService.getInsDelCosts(cs.getDeprelInsdelFile());
	}

	public List<PlagiarismReference> findPlagiarism(PlagiarismJob job) {
		List<PlagiarismReference> plagReferences = new ArrayList<>();

		for(PlagiarismPassage passage : job.getTextPairs()) {
			PlagiarismReference ref = getPlagiarism(passage.getTrainFile(), passage.getTrainSentence(), passage.getTestFile(), passage.getTestSentence());
			if(ref != null) {
//				findAdjacentPlagiarism(ref, passage.getTrainSentence(), passage.getTestSentence(), false);
//				findAdjacentPlagiarism(ref, passage.getTrainSentence(), passage.getTestSentence(), true);
				plagReferences.add(ref);
			}
		}

		return plagReferences;
	}

	public PlagiarismReference getPlagiarism(String sourceFile, int sourceSentence, String suspiciousFile, int suspiciousSentence) {
		/**
		 * Checks the given sentence pair for plagiarism with the graph edit distance algorithm
		 */
		try {
			Graph source = GraphUtils.getGraph(db.getSentence(sourceFile, sourceSentence));
			Graph suspicious = GraphUtils.getGraph(db.getSentence(suspiciousFile, suspiciousSentence));
			if(source.getSize() > 80 || suspicious.getSize() > 80) {
				return null;
			}
			
			GraphEditDistance ged = new GraphEditDistance(suspicious, source, posEditWeights, deprelEditWeights);
			double dist = ged.getNormalizedDistance();
			if(dist < plagiarismThreshold) {
				return XMLUtils.getPlagiarismReference(source, suspicious, true);
			}else {
				return null;
			}
		}catch(NullPointerException e) {
			return null;
		}
	}
	

//	public void findAdjacentPlagiarism(PlagiarismReference ref, int sourceSentence, int suspiciousSentence, boolean ascending) {
//		int i = ascending ? 1 : -1;
//		PlagiarismReference adjRef = getPlagiarism(ref.getSourceReference(), sourceSentence+i, ref.getFilename(), suspiciousSentence+i);
//		if(adjRef != null) {
//			ref.setOffset(adjRef.getOffset());
//			ref.setLength(getNewLength(ref.getOffset(), ref.getLength(), adjRef.getOffset(), i));
//			ref.setSourceOffset(adjRef.getSourceOffset());
//			ref.setSourceLength(getNewLength(ref.getSourceOffset(), ref.getSourceLength(), adjRef.getSourceOffset(), i));
//			findAdjacentPlagiarism(ref, sourceSentence+i*2, suspiciousSentence+i*2, ascending);
//		}
//	}

//	public String getNewLength(String offsetString, String lengthString, String newOffsetString, int ascending) {
//		int offset = Integer.parseInt(offsetString);
//		int len = Integer.parseInt(lengthString);
//		int newOffset = Integer.parseInt(newOffsetString);
//
//		int newLen =  len + ((offset - newOffset) * ascending);
//		return Integer.toString(newLen);
//	}


	public List<PlagiarismReference> listCandidateReferences(PlagiarismJob job) {
		/**
		 * Only returns the plagiarism references from candidate retrieval.
		 * Use this for measuring the candidate retrieval phase.
		 */
		List<PlagiarismReference> plagReferences = new ArrayList<>();
		for (PlagiarismPassage pair : job.getTextPairs()) {
			Graph suspicious = GraphUtils.getGraph(db.getSentence(pair.getTestFile(), pair.getTestSentence()));
			Graph source = GraphUtils.getGraph(db.getSentence(pair.getTrainFile(), pair.getTrainSentence()));

			plagReferences.add(XMLUtils.getPlagiarismReference(source, suspicious, false));
		}

		return plagReferences;
	}
}
