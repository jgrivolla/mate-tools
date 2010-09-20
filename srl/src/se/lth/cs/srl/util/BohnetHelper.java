package se.lth.cs.srl.util;

import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag3.Tagger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BohnetHelper {

	public static Lemmatizer getLemmatizer(File modelFile) throws FileNotFoundException, IOException{
		String[] argsL={"-model",modelFile.toString()};
		return new Lemmatizer(new is2.lemmatizer.Options(argsL));
	}
	
	public static Tagger getTagger(File modelFile) {
		String[] argsT={"-model",modelFile.toString()};
		return new Tagger(new is2.tag3.Options(argsT));
	}
	
	public static is2.mtag.Main getMTagger(File modelFile) throws IOException{
		String[] argsMT={"-model",modelFile.toString()};
		return new is2.mtag.Main(new is2.mtag.Options(argsMT));
	}
	
	public static Parser getParser(File modelFile){
		String[] argsDP={"-model",modelFile.toString()};
		return new Parser(new is2.parser.Options(argsDP));
	}
	
}
