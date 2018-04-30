package sample;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;

import static org.junit.Assert.*;

public class ReadLineLengthsTest{

    @Test
    public void ordinaryFileGoodPath(){
        ReadLineLengths test = new ReadLineLengths("resource/validWithLines.txt", 4);
        assertTrue("The path is valid", test.getFileToRead().toFile().exists());
        assertTrue("Lines are multiple", test.getNumberOfLinesToRead() > 1);
    }

    @Test
    public void emptyFileGoodPath(){
        ReadLineLengths test = new ReadLineLengths("resource/empty.txt", 0);
        assertTrue("The line number is 0", test.getFileToRead().toFile().length() == 0);
        assertTrue("The path is valid for empty file", test.getFileToRead().toFile().exists());
    }

    @Test(expected = InvalidPathException.class)
    public void fileDoesNotExist(){
        ReadLineLengths test = new ReadLineLengths("resource/doesNotExist.txt", 0);
    }

    @Test
    public void appropriateNumberOfLines(){
        ReadLineLengths test = new ReadLineLengths("resource/validWithLines.txt", 3);
        assertTrue(test.getFileToRead().toFile().length() >= test.getNumberOfLinesToRead());
    }

    @Test
    public void tooManyLines() throws IOException{
        ReadLineLengths test = new ReadLineLengths("resource/validWithLines.txt", 100);
        List<Integer> list = test.lineLengths();
        assertNotEquals(100, list.size());
    }

    @Test
    public void linesSpecZero() throws IOException{
        ReadLineLengths test = new ReadLineLengths("resource/validWithLines.txt", 0);
        List<Integer> list = test.lineLengths();
        assertEquals(0, list.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void linesSpecNegative() throws IOException{
        ReadLineLengths test = new ReadLineLengths("resource/validWithLines.txt", -2);
        test.lineLengths();
    }

}