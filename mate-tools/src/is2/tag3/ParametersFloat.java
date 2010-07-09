package is2.tag3;

import is2.data.FV;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


final public class ParametersFloat  {

	public float[] parameters;
	public float[] total;

	public ParametersFloat(int size) { 	
		super();
		parameters = new float[size];
		total = new float[size];
		for(int i = 0; i < parameters.length; i++) {
			parameters[i] = 0.0F;
			total[i] = 0.0F;
		}
	}
	
	
	public void averageParams(double avVal) {
		for(int j = 0; j < total.length; j++) total[j] *= 1.0/(avVal);		
		parameters = total;
	}
		

	public void update(FV pred, FV act,  double upd, double err) {

		
			double lam_dist = act.getScore(parameters,false)- pred.getScore(parameters,false);
			double b =err - lam_dist;

			FV dist = act.getDistVector(pred);	 

			double alpha;
			double A = dist.dotProduct(dist);
			if (A<=0.0000000000000001)  alpha=0.0;
			else alpha= b/A;
			
			dist.update(parameters, total, alpha, upd,false); //0.05
		
	}
	
	
	final public void write(DataOutputStream dos) throws IOException{
		
		dos.writeInt(parameters.length);
		for(float d : parameters)  dos.writeFloat(d);
		
		
	}
	
	public void read(DataInputStream dis ) throws IOException{
	
		int l = dis.readInt();
		parameters = new float[l];
		for(int i=0;i<parameters.length;i++) parameters[i]=dis.readFloat();
		
		
	}

}
