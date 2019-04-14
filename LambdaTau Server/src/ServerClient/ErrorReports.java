package ServerClient;

import java.io.*;

public class ErrorReports {
    File errors;
    FileWriter out;


    public ErrorReports() throws IOException{
        errors = new File("Error Report");
        out = new FileWriter(errors);
    }

    public void writeException(String exception) throws IOException {
        out.append(exception);
    }
}