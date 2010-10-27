package is2.lemmatizer;

import is2.data.FV;
import is2.data.SentenceData09;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ParametersDouble extends Parameters {

	public double[] parameters;
	public double[] total;
	

	public ParametersDouble(int size) { 	
		super();
		parameters = new double[size];
		total = new double[size];
		for(int i = 0; i < parameters.length; i++) {
			parameters[i] = 0.0f;
			total[i] = 0.0f;
		}
	}
	
	
	public void averageParams(double avVal) {
		for(int j = 0; j < total.length; j++) total[j] *= 1.0/(avVal);		
		parameters = total;
	}
	
	protected double hildreth(FV a, double b) {


		//boolean is_computed = false;

		double A = a.dotProduct(a);

		if (A<=0.0000000000000001) return 0.0;
	//	System.out.print(" A " +A+" b "+F+" b/A"+F/A);
		return b/A;
		//return A;
	}

	public void updateParamsMIRA(SentenceData09 inst, Object[][] d, double upd, Pipe p) {

		for(int k = 0; k < d.length; k++) {
			
		//	FV f = new FV();
		//	p.addCoreFeatures(inst,k,null,null,f);
			double lam_dist = getScore(inst.m_fvs[k])- getScore((FV)d[k][0]);
			String op = Pipe.getOperation(inst, k);
			
			double b =  (op.equals(d[k][1])) ?0.0: 1.0;
			if (b==0.0) continue;
			b -= lam_dist;
			FV dist = inst.m_fvs[k].getDistVector((FV)d[k][0]);	 

			//	FV dist = inst.m_fvs[k].getDistVector((FV)d[k][0]);	 
			//		System.out.println("op "+op+" result "+d[k][1]+" ld "+lam_dist+" "+inst.m_fvs[k].size()+" "+((FV)d[k][0]).size()+" dist "+dist.size());
			
			dist.update(parameters, total, hildreth(dist,b), upd,false); //0.05

		}

		
	}
	
	@Override
	public double getScore(FV fv) {
		return fv.getScore(parameters,false);

	}
	
	final public void write(DataOutputStream dos, int k, double max) throws IOException{
	
		dos.writeInt(k);
		boolean i=true;
	
		for(double d : parameters) {
			//if (d!=0.0|| i) dos.writeDouble(d);
			if (d>max || d<-max || i){
				dos.writeDouble(d);
			}
			i=false;
		}
		
	}
	final public void write(DataOutputStream dos) throws IOException{
		
		dos.writeInt(parameters.length);
		for(double d : parameters) {
			dos.writeDouble(d);
		}
		
	}
	
	@Override
	public void read(DataInputStream dis ) throws IOException{
	
		int l = dis.readInt();
		parameters = new double[l];
		for(int i=0;i<parameters.length;i++) {
			
			parameters[i]=dis.readDouble();
		//	DB.println("read"+parameters[i]);
		}
		
	}





}
