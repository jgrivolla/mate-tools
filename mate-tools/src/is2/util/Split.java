package is2.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.StringTokenizer;

public class Split {

	/**
	 * Splits a tokenized sentences into one word per line format:
	 *
	 * Input
	 * > I am an text .
	 * > Sentence two ...
	 * 
	 * Output:
	 * I	_	_	_ 	...
	 * am	_	_	_ 	...
	 * ...
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {

		if (args.length!=1) {
			System.out.println("Please provide a file name.");
			System.exit(0);
		}
		
		String filename = args[0]; 
//		Charset charset = Charset.forName("UTF-8");		

		FileInputStream in = new FileInputStream(filename);
		FileChannel channel = in.getChannel();
		CharsetDecoder decoder = Charset.defaultCharset().newDecoder();//charset.newDecoder();
		Reader infile = Channels.newReader(channel , decoder, 16*1024);
		BufferedReader bInfile = new BufferedReader(infile);
		
//		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(options.modelName)));

	
		String s;
		while ((s = bInfile.readLine()) != null) {
			StringTokenizer t = new StringTokenizer(s);
			int i=1;
			while(t.hasMoreTokens()) {
				String tk =t.nextToken();
				if (tk.contains("=")) continue;
				System.out.print(i+"\t");
				System.out.print(tk);
				System.out.println("\t_\t_\t_\t_\t_\t_\t_\t_\t_\t_\t_\t_\t_");
				i++;
			}
			System.out.println();
			
		}
		bInfile.close();

		
		
			
	}
	
	
}
