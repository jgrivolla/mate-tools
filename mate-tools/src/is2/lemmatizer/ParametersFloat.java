package is2.lemmatizer;

import is2.data.FV;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ParametersFloat  {

	public float[] parameters;
	public float[] total;

	public ParametersFloat(int size) { 	
		parameters = new float[size];
		total = new float[size];
		for(int i = 0; i < parameters.length; i++) {
			parameters[i] = 0.0f;
			total[i] = 0.0f;
		}
	}

	public void averageParams(double avVal) {
		for(int j = 0; j < total.length; j++) total[j] *= 1.0/(avVal);		
		parameters = total;
	}

	
	public double getScore(FV fv) {
		return fv.getScore(parameters,false);

	}
	
	final public void write(DataOutputStream dos) throws IOException {
		
		dos.writeInt(parameters.length);
		for(double d : parameters) {
			dos.writeDouble(d);
		}
		
	}
	
	final public void read(DataInputStream dis ) throws IOException{
	
		parameters = new float[dis.readInt()];
		for(int i=0;i<parameters.length;i++) parameters[i]=(float)dis.readDouble();
		
	}

}
