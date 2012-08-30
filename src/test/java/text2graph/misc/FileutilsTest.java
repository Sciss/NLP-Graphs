package text2graph.misc;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import text2graph.models.POSFile;

public class FileutilsTest {

	private Path dir;
	
	@Before
	public void setup() {
		dir = Paths.get("src/test/resources/documents");
	}
	
	@Test
	public void shouldSetRelativePath() {
		Path lol = Paths.get("src/test/resources/documents/lol.txt");
		assertEquals(lol.getFileName().toString(), "lol.txt");
		assertEquals(dir.relativize(lol), lol.getFileName());
		
		POSFile file = new POSFile(Paths.get("src/test/resources/documents/test/omg.txt"), dir);
		assertEquals("test/omg.txt", file.getRelPath().toString());
	}
	
	@Test
	public void shouldGetFiles() {
		File[] files = Fileutils.getFiles(dir);
		assertEquals(18, files.length);
	}
}
