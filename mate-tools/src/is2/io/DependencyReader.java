package is2.io;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class DependencyReader {

    private static final String NUMBER = "[0-9]+|[0-9]+\\.[0-9]+|[0-9]+[0-9,]+";
	private static final String NUM = "<num>";
	protected BufferedReader inputReader;

  
    

    public void startReading (String file) throws IOException {
 		inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"),32768);
		
    }

    protected static  String normalize (String s) {
    	if(s.matches(NUMBER))  return NUM;

    	return s;
    }	

}
