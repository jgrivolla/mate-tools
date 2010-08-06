package se.lth.cs.srl.options;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import se.lth.cs.srl.Learn;
import se.lth.cs.srl.pipeline.Step;

public class LearnOptions extends Options {
	public static final Map<Step,String> featureFileNames;
	static {
		Map<Step,String> map=new HashMap<Step,String>();
		map.put(Step.pi,"pi.feats");
		map.put(Step.pd,"pd.feats");
		map.put(Step.ai,"ai.feats");
		map.put(Step.ac,"ac.feats");
		featureFileNames=Collections.unmodifiableMap(map);
	}
	
	public File liblinearBinary;
	public File tempDir;
	private File featureFileDir=new File("features");
	
	public boolean skipNonMatchingPredicates=false;
	public boolean trainReranker=false;
	public boolean deleteTrainFiles=true;
	
	public boolean global_insertGoldMapForTrain=true;
	public int global_numberOfCrossTrain=5;
	
	private Map<Step,File> featureFiles;

	public LearnOptions(String[] args) {
		superParseCmdLine(args);
	}
	@Override
	int parseCmdLine(String[] args, int ai) {
		if(args[ai].equals("-fdir")){
			ai++;
			featureFileDir=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-llbinary")){
			ai++;
			liblinearBinary=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-reranker")){
			ai++;
			trainReranker=true;
		} else if(args[ai].equals("-partitions")){
			ai++;
			global_numberOfCrossTrain=Integer.parseInt(args[ai]);
			ai++;
		} else if(args[ai].equals("-dontInsertGold")){
			ai++;
			global_insertGoldMapForTrain=false;
		} else if(args[ai].equals("-skipUnknownPredicates")){
			ai++;
			skipNonMatchingPredicates=true;
		} else if(args[ai].equals("-dontDeleteTrainData")){
			ai++;
			deleteTrainFiles=false;
		}
		return ai;		
	}
	@Override
	void usage() {
		System.err.println("Usage:");
		System.err.println(" java -cp <classpath> "+Learn.class.getName()+" <lang> <input-corpus> <model-file> [options]");
		System.err.println();
		System.err.println("Example:");
		System.err.println(" java -cp srl.jar:lib/liblinear-1.51-with-deps.jar "+Learn.class.getName()+" eng ~/corpora/eng/CoNLL2009-ST-English-train.txt eng-srl.mdl -reranker -fdir ~/features/eng -llbinary ~/liblinear-1.6/train");
		System.err.println();
		System.err.println(" trains a complete pipeline and reranker based on the corpus and saves it to eng-srl.mdl");
		System.err.println();
		super.printUsageLanguages(System.err);
		System.err.println();
		super.printUsageOptions(System.err);
		System.err.println("");
		System.err.println("Learning-specific options:");
		System.err.println(" -reranker               trains a reranker also (not done by default)");
		System.err.println(" -llbinary <file>        a reference to a precompiled version of liblinear,");
		System.err.println("                         makes training much faster than the java version.");
		System.err.println(" -partitions <int>       number of partitions used for the reranker");
		System.err.println(" -dontInsertGold         don't insert the gold standard proposition during");
		System.err.println("                         training of the reranker.");
		System.err.println(" -skipUnknownPredicates  skips predicates not matching any POS-tags from");
		System.err.println("                         the feature files.");
		System.err.println(" -dontDeleteTrainData    doesn't delete the temporary files from training");
		System.err.println("                         on exit. (For debug purposes)");
	}
	@Override
	boolean verifyArguments() {
		verifyFeatureFiles();
		if(liblinearBinary!=null && (!liblinearBinary.exists() || !liblinearBinary.canExecute())){
			System.err.println("The provided liblinear binary does not exists or can not be executed. Aborting.");
			System.exit(1);
		}
		setupTempDir();
		return true;
	}
	private void setupTempDir() {
		String curDateTime=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String tempDirPath=System.getProperty("java.io.tmpdir")+File.separator+"srl_"+curDateTime;
		tempDir=new File(tempDirPath);
		if(tempDir.exists()){
			throw new Error("Temporary dir "+tempDir+" already exists. Look into this.");
		} else { 
			if(!tempDir.mkdir()){
				throw new Error("Failed to create temporary dir "+tempDir);
			}
			System.out.println("Using temporary directory "+tempDir);
		}
		tempDir.deleteOnExit();
	}
	private void verifyFeatureFiles() {
		if(!featureFileDir.exists() || !featureFileDir.canRead()){
			System.err.println("Feature file dir "+featureFileDir+" does not exist or can not be read. Aborting.");
			System.exit(1);
		}
		featureFiles=new HashMap<Step,File>();
		for(Step s:Step.values()){
			File f=new File(featureFileDir,featureFileNames.get(s));
			if(!f.exists() || !f.canRead()){
				System.out.println("Feature file "+f+" does not exist or can not be read, aborting.");
				System.exit(1);
			}
			featureFiles.put(s, f);
		}
	}
	public Map<Step, File> getFeatureFiles() {
		return featureFiles;
	}
	
	
}
