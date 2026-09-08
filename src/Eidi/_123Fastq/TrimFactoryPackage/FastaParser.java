package Eidi._123Fastq.TrimFactoryPackage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FastaParser {

    private BufferedReader reader;

    private String currentLine;
    private FastaRecord current;

    public FastaParser() {

    }

    public void parseOne() throws IOException //Line by line in the Adapter file
    {
        current = null;

        if (currentLine == null) {
            currentLine = reader.readLine();
        }

        while (currentLine != null && !currentLine.startsWith(">")) {
            currentLine = reader.readLine();
        }

        if (currentLine != null && currentLine.startsWith(">")) {
            String fullName = currentLine.substring(1).trim(); //seprate exact name of adapter.
            String tokens[] = fullName.split("[\\| ]"); //regex means AnyCharIn[ | ]
            String name = tokens[0]; //adapter name.

            StringBuilder builder = new StringBuilder(); //to fetch adapter sequence.

            currentLine = reader.readLine();
            while (currentLine != null && !currentLine.startsWith(">")) {
                if (!currentLine.startsWith(";")) {
                    builder.append(currentLine.trim()); //trim to out white spaces.
                }
                currentLine = reader.readLine(); //to the end of adapter seq.
            }

            current = new FastaRecord(name, builder.toString().trim(), fullName);
            //current is a Fasta record object to fetch adapter in other classes.
        }
    }

    public void parse(File file) throws IOException //parse adapter file to save adapters as a FastaRecord.
    {       //1000000 is buffered input size.
        reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file), 1000000)));
        parseOne();
    }

    public void close() throws IOException {
        reader.close();
    }

    public boolean hasNext() {
        return current != null;
    }

    public FastaRecord next() throws IOException {
        FastaRecord current = this.current;
        parseOne();
        return current;
    }
}
