package se.lth.cs.srl.test;

import java.io.File;

import org.junit.Test;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.languages.English;
import se.lth.cs.srl.languages.German;
import se.lth.cs.srl.languages.Language.L;
import se.lth.cs.srl.languages.Language;

public class TestCALS extends TestCaseOutput{

    private final static String RESULT_DIR = "test/result/";
    private final static String RESULT_EXTENSION = ".cals";
	
	@Test
	public void engCALS_0_0(){
		assertCorrectOutput("eng_0_0",0,0,new English());
	}
	
	@Test
	public void engCALS_1_0(){
		assertCorrectOutput("eng_1_0",1,0,new English());
	}
	
	@Test
	public void engCALS_1_3(){
		assertCorrectOutput("eng_1_3",1,3,new English());
	}
	
	@Test
	public void gerCALS_2_0(){
		assertCorrectOutput("ger_2_0",2,0,new German());
	}
	
	@Test
	public void gerCALS_3_0(){
		assertCorrectOutput("ger_3_0",3,0,new German());
	}
	
	@Test
	public void engCALS_4_1(){
		assertCorrectOutput("eng_4_1",4,1,new English());
	}
	
	private void assertCorrectOutput(String testName,int senIndex,int predIndex,Language lang){
		Predicate pred=sentences.get(senIndex).getPredicates().get(predIndex);
		dumpCALS(pred,lang);
		assertOutput(new File(RESULT_DIR + testName + RESULT_EXTENSION));
	}
	
	private static void dumpCALS(Predicate pred,Language language){
		System.out.println(language.getCoreArgumentLabelSequence(pred, pred.getArgMap()));
	}
	
	public static void main(String[] args){
		if(args.length<3){
			System.out.println("Not enough parameters, aborting.");
			System.exit(1);
		}
		Language.setLanguage(L.valueOf(args[0]));
		Sentence sen=sentences.get(Integer.valueOf(args[1]));
		Predicate pred=sen.getPredicates().get(Integer.valueOf(args[2]));
		dumpCALS(pred,Language.getLanguage());
	}
}
