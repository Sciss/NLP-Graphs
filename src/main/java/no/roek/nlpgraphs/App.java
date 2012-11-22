package no.roek.nlpgraphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {

	public static void main(String[] args) {
		PlagiarismSearch ps = new PlagiarismSearch();

		switch(getChoice()) {
			case 1: GED.main(args);
			case 2: ps.preprocess();
			case 3: ps.createIndex();
			case 4: ps.startPlagiarismSearch();
			case 5: ps.startPlagiarismSearchWithoutCandret();
		}
		if(ps.shouldPreprocess()) {
			ps.preprocess();
		}else if(ps.shouldCreateIndex()) {
			ps.createIndex();
		}else {
			ps.startPlagiarismSearch();
		}
	}

	public static int getChoice()  {
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		int choice = 0;

		try {
			System.out.println("Welcome to Graph Edit Distance plagiarism search");
			System.out.println("For the program to work, some preferences must be specified in the file app.properties. Have a look at app.properties.example for an example.");
			System.out.println("Please select action..");
			System.out.println("type <number> then enter");
			System.out.println("1: Graph edit distance calculation of two sentences");
			System.out.println("2: preprocess the data specified in DATA_DIR in app.properties");
			System.out.println("3: build index required for the candidate retrieval phase");
			System.out.println("4: start detailed analysis - graph edit distance plagiarism search. (both indexing and preprocessing have to be done)");
			System.out.println("5: start detailed analysis with candidate retrieval results written to file.");

			String action = in.readLine();
			choice = Integer.parseInt(action);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return choice;
	}
}
