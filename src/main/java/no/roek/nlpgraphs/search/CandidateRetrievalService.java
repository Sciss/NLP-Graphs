package no.roek.nlpgraphs.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.roek.nlpgraphs.document.DocumentFile;
import no.roek.nlpgraphs.misc.Fileutils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similar.MoreLikeThis;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;


public class CandidateRetrievalService {

	//	private RAMDirectory index;
	private Directory index;
	private IndexWriterConfig indexWriterConfig;
	private IndexWriter writer;
	private DocumentFile[] documents;
	private StandardAnalyzer analyzer;
	private static final String INDEX_DIR = "lucene/";
	private Map<String, Integer> documentDict = new HashMap<>();

	public CandidateRetrievalService(Path dir) throws CorruptIndexException, IOException {
		analyzer = new StandardAnalyzer(Version.LUCENE_36);
		createIndex(dir);
	}

	public void createIndex(Path dir) throws CorruptIndexException, IOException {
		Path temp = Paths.get(INDEX_DIR+dir.getFileName().toString());
		index = new MMapDirectory(temp.toFile());

		indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, new StandardAnalyzer(Version.LUCENE_36));
		writer = new IndexWriter(index, indexWriterConfig);
		documents = Fileutils.getFileList(dir);
		int i = 0;
		for (DocumentFile file: documents) {
			Document doc = getDocument(file.getPath());
			writer.addDocument(doc);
			this.documentDict.put(doc.get("filename"), i);
			if(documentDict.get(file.getRelPath().toString()) == null) {
				System.out.println(file.getRelPath().toString());
			}
			
			i++;
		}
		writer.close();
	}

	public Document getDocument(Path file) {
		Document doc = new Document();
		doc.add(new Field("text", Fileutils.getText(file), org.apache.lucene.document.Field.Store.NO, org.apache.lucene.document.Field.Index.ANALYZED, org.apache.lucene.document.Field.TermVector.YES));
		doc.add(new Field("filename", file.getFileName().toString(), org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.ANALYZED));
		return doc;
	}

//	public List<String> getSimilarDocuments(String filename, int recall) throws IOException {
//		String queryText = Fileutils.getText(Paths.get(filename));
//		IndexReader ir = IndexReader.open(index);
//		IndexSearcher is = new IndexSearcher(ir);
//		List<String> simDocs = new ArrayList<String>();
//
//		TopScoreDocCollector collector = TopScoreDocCollector.create(recall, true);
//		Query query;
//		try {
//			query = new QueryParser(Version.LUCENE_36, "text", analyzer).parse(QueryParser.escape(queryText));
//			is.search(query, collector);
//			ScoreDoc[] hits = collector.topDocs().scoreDocs;
//
//
//			for (ScoreDoc scoreDoc : hits) {
//				Document doc = is.doc(scoreDoc.doc);
//				simDocs.add(doc.get("filename"));
//			}
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return simDocs;
//	}


		public List<String> getSimilarDocuments(String filename, int recall) throws CorruptIndexException, IOException {
			IndexReader ir = IndexReader.open(index);
			IndexSearcher is = new IndexSearcher(ir);
	
			MoreLikeThis mlt = new MoreLikeThis(ir);
			mlt.setFieldNames(new String[] {"text"});
			Reader reader = new BufferedReader(new FileReader(filename));
			Query query = mlt.like(reader, "text");
			
			ScoreDoc[] hits = is.search(query, recall).scoreDocs;
	
			List<String> simDocs = new ArrayList<String>();
			for (ScoreDoc scoreDoc : hits) {
				Document doc = is.doc(scoreDoc.doc);
				simDocs.add(doc.get("filename"));
			}
	
			return simDocs;
		}
}