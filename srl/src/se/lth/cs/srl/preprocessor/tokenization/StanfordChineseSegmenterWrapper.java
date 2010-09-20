package se.lth.cs.srl.preprocessor.tokenization;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.GoldAnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel.GenericAnnotation;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter;

public class StanfordChineseSegmenterWrapper implements Tokenizer {

	public StanfordChineseSegmenterWrapper(File dataDir){
		
	}
	
	@Override
	public String[] tokenize(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws Exception{
		//TODO This isn't working. I dont get it.
		String testFile="chi-desegmented.out";
		   Properties props = new Properties();
		    props.setProperty("sighanCorporaDict", "/home/anders/Download/stanford-chinese-segmenter-2008-05-21/data");
		    // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
		    // props.setProperty("normTableEncoding", "UTF-8");
		    // below is needed because CTBSegDocumentIteratorFactory accesses it
		    props.setProperty("serDictionary","/home/anders/Download/stanford-chinese-segmenter-2008-05-21/data/dict-chris6.ser.gz");
		    props.setProperty("testFile", testFile);
		    props.setProperty("inputEncoding", "UTF-8");
		    props.setProperty("sighanPostProcessing", "true");
		    //props.setProperty("keepEnglishWhitespaces", "true");
		    CRFClassifier classifier = new CRFClassifier(props);
		    classifier.loadClassifierNoExceptions("/home/anders/Download/stanford-chinese-segmenter-2008-05-21/data/ctb.gz", props);
		    // flags must be re-set after data is loaded
		    classifier.flags.setProperties(props);
		    //classifier.flags.keepEnglishWhitespaces=true;
		    //classifier.writeAnswers(classifier.test(args[0]));
		    //classifier.testAndWriteAnswers(args[0]);
		    //ObjectBank<List<CoreLabel>> documents=classifier.makeObjectBank("上海浦东开发与法制建设同步", false);
		    
		    ObjectBank<List<CoreLabel>> documents = classifier.makeObjectBank(testFile);
		    Sighan2005DocumentReaderAndWriter sighan2005DocumentReaderAndWriter=new Sighan2005DocumentReaderAndWriter();
		    ByteArrayOutputStream baos=new ByteArrayOutputStream();
		    PrintWriter pw=new PrintWriter(baos);
		    for(List<CoreLabel> document:documents){
		    	System.out.println("testing...");
		    	classifier.test(document);
		    	System.out.println("done.");
		    	sighan2005DocumentReaderAndWriter.printAnswers(document, pw);
		    	System.out.println(baos.toString("UTF-8"));
		    }

	}
	
}
