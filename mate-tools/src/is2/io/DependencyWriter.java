package is2.io;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class DependencyWriter {

    protected BufferedWriter writer;
  
   

    public void startWriting (String file) throws IOException {
    	writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF8"));
        }

        public void finishWriting () throws IOException {
        	writer.flush();
        	writer.close();
        }

    

}
