package se.lth.cs.srl.util;

import java.io.File;

public class FileExistenceVerifier {
	
	/**
	 * Checks if a file can be exists, and if it can be read.
	 * @param files vararg number of files to check
	 * @return null if all is good, otherwise a String containing the error message.
	 */
	public static String verifyFiles(File... files){
		for(File f:files){
			if(!f.exists()){
				return "File "+f+" does not exist.";
			}
			if(!f.canRead()){
				return "File "+f+" can not be read.";
			}
		}
		return null;
	}
	
}
