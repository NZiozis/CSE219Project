import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class SaveDataTestSuite{


    private Path dataFilePath;

    private String loadData(Path dataFilePath) throws IOException{
        Scanner scanner = new Scanner(new File(dataFilePath.toString()));
        scanner.useDelimiter("\n");
        String resultString = "";
        while (scanner.hasNextLine()) resultString += scanner.nextLine() + "\n";
        return resultString;
    }

    private void writeData(Path dataFilePath) throws IOException{
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFilePath.toFile()));
        bufferedWriter.write("@Instance1\tlabel1\t1.5,2.2\n" + "@Instance2\tlabel1\t1.8,3\n" +
                             "@Instance3\tlabel1\t2.1,2.9\n");
        bufferedWriter.close();
    }


    @Test
    public void checkThatFileMatchesTextArea() throws IOException{
        dataFilePath = new File("Test/IOTester.tsd").toPath();
        writeData(dataFilePath);
        String savedData = loadData(dataFilePath);
        String dataFromTextArea =
                "@Instance1\tlabel1\t1.5,2.2\n" + "@Instance2\tlabel1\t1.8,3\n" + "@Instance3\tlabel1\t2.1,2.9\n";
        assertEquals(dataFromTextArea, savedData);
    }


}
