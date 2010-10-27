package is2.lemmatizer;

import is2.data.FV;

import java.io.DataInputStream;
import java.io.IOException;



public abstract class Parameters {
	public String lossType = "punc";

	public Parameters() { 	
	}




	abstract public double  getScore(FV fv);
	



	
	abstract public void read(DataInputStream dis) throws IOException;

}
