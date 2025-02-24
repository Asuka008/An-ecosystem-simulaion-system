package consoleVersion1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TextFile class for handling file operations related to text files.
 * @author shsmchlr
 */
public class TextFile {

    private String extension;          // extension of files that are opened/create, e.g., txt
    private String extDescription;     // description used for these, e.g., Text Files
    private String nameOfFile = "";    // name of file used in last operation
    private BufferedReader inBuffer;   // buffer used for reading files
    private BufferedWriter outBuffer;  // buffer used for writing files
    private FileReader reader;         // used for reading files
    private String fileLine;           // string containing latest line from file
    private static final String FILE_PATH = "C:/Users/Henry/Desktop/progamming/JAVA_DATA/Learning_Java/src/consoleVersion1/status.txt"; 

    /**
     * Constructs a TextFile object with specified file type description and extension.
     * @param ftypeD description of the file type
     * @param fExtension file extension
     */
    public TextFile(String ftypeD, String fExtension) {
        extDescription = ftypeD;  // remember these arguments
        extension = fExtension;
    }

    /**
     * Reports the name of the file used for reading/writing - empty string if open/create file failed.
     * @return name of the file used
     */
    public String usedFileName() {
        return nameOfFile;
    }

    /**
     * Opens the fixed 'status.txt' file for reading.
     * @return true if the file is successfully opened, false otherwise
     */
    public boolean openFile() {
        nameOfFile = FILE_PATH;  // Use the fixed path for loading
        File selFile = new File(nameOfFile); // Create a File object for the fixed file path
        if (selFile.isFile()) {  // Check if the file exists
            try {
                reader = new FileReader(selFile);  // Set up reader for the file
                inBuffer = new BufferedReader(reader);  // Set up buffer for reading lines from file
            } catch (IOException e) {
                e.printStackTrace();  // If there's an issue, print the error
            }
        }
        return nameOfFile.length() > 0;  // Return true if the file is found
    }

    /**
     * Closes the file which has been read.
     */
    void closeFile() {
        try {
            inBuffer.close();  // Close the file
        } catch (IOException e) {
            e.printStackTrace();  // Report error if this didn't work
        }
    }

    /**
     * Gets the next line from the text file.
     * @return true if a line is successfully read, false otherwise
     */
    boolean getNextline() {
        boolean ok = false;  // Assume false
        try {
            fileLine = inBuffer.readLine();  // Read a line from inBuffer
            if (fileLine != null) ok = true;  // If not null, then the line is there
        } catch (IOException e) {
            e.printStackTrace();  // Report any error
        }
        return ok;  // Return true if successful
    }

    /**
     * Returns the last line that was read successfully by getNextline.
     * @return the last read line
     */
    String nextLine() {
        return fileLine;  // Just return fileLine
    }

    /**
     * Reads all of the file, returning one string; each line separated by \n.
     * @return all content of the file as a single string
     */
    public String readAllFile() {
        String ans = "";
        while (getNextline()) {  // While there's a line to read
            ans = ans + nextLine() + "\n";  // Get it and add to the answer
        }
        closeFile();  // Close the file
        return ans;
    }

    /**
     * Creates a new file for writing (always saves to fixed location 'status.txt').
     * @return true if the file is created successfully, false otherwise
     */
    public boolean createFile() {
        nameOfFile = FILE_PATH;  // Use the fixed path for saving
        File wFile = new File(nameOfFile);  // Create a File object for the fixed file path
        try {
            outBuffer = new BufferedWriter(new FileWriter(wFile));  // Set up the output buffer
        } catch (IOException e) {
            e.printStackTrace();  // If file creation fails, print the error
        }
        return nameOfFile.length() > 0;  // Return true if file is created successfully
    }

    /**
     * Writes a string then newline to the file.
     * @param s the string to write
     */
    void putNextLine(String s) {
        try {
            outBuffer.write(s);  // Write string
            outBuffer.newLine();  // And newline
        } catch (IOException e) {
            e.printStackTrace();  // Handle any error
        }
    }

    /**
     * Closes the file that has been written to.
     */
    void closeWriteFile() {
        try {
            outBuffer.close();  // Close it
        } catch (IOException e) {
            e.printStackTrace();  // Print error
        }
    }

    /**
     * Writes data to created file; data is a series of strings separated by \n.
     * @param data the data to write to the file
     */
    public void writeAllFile(String data) {
        String[] manyStrings = data.split("\n");  // Split data into lines
        for (int ct = 0; ct < manyStrings.length; ct++) {  // For each line
            putNextLine(manyStrings[ct]);  // Write it to the file
        }
        closeWriteFile();  // Close the file after writing
    }

    /**
     * Main method for testing the TextFile class.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        TextFile tf = new TextFile("Text files", "txt");  // Create object looking for *.txt Text files

        if (tf.openFile()) {  // Open file
            System.out.println("Reading from " + tf.usedFileName());
            System.out.println(tf.readAllFile());  // Read whole file into str and print to console
        } else {
            System.out.println("No read file selected");
        }

        if (tf.createFile()) {  // Create file to be written to
            System.out.println("Writing to " + tf.usedFileName());
            tf.writeAllFile("30 10" + "\n" + "0 5 6 EAST" + "\n" + "1 2 7 SOUTH");
        } else {
            System.out.println("No write file selected");
        }
    }
}
