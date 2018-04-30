package sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ritwik Banerjee
 */
public class ReadLineLengths{

    private Path fileToRead;
    private int  numberOfLinesToRead;

    public ReadLineLengths(String path, int numberOfLinesToRead){
        setFileToRead(path);
        setNumberOfLinesToRead(numberOfLinesToRead);

    }

    public int getNumberOfLinesToRead(){
        return numberOfLinesToRead;
    }

    public void setNumberOfLinesToRead(int numberOfLinesToRead){
        this.numberOfLinesToRead = numberOfLinesToRead;
    }

    public Path getFileToRead(){
        return fileToRead;
    }

    public void setFileToRead(String fileToRead) throws InvalidPathException{
        this.fileToRead = Paths.get(fileToRead);
        if (!this.fileToRead.toFile().exists()){
            throw new InvalidPathException(fileToRead, "File doesn't exist.");
        }

    }

    public List<Integer> lineLengths() throws IOException{
        return Files.lines(fileToRead).limit(numberOfLinesToRead).map(line -> line.toCharArray().length)
                    .collect(Collectors.toList());
    }
}
