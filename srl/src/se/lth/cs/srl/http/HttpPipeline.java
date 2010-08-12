package se.lth.cs.srl.http;

import java.net.InetSocketAddress;
import java.util.Date;

import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.options.HttpOptions;

import com.sun.net.httpserver.HttpServer;

public class HttpPipeline {

	private static boolean ready=false;

	private static HttpServer server;
	private static ParseRequestHandler parseHandler;
	private static ParserStatusHandler statusHandler;
	private static DefaultHandler defaultHandler;
	public static Date serverStart;
	
	public static void main(String[] args) {
		try {
			serverStart=new Date(System.currentTimeMillis());
			HttpOptions options=new HttpOptions(args);
			defaultHandler=new DefaultHandler();
			server=HttpServer.create(new InetSocketAddress(options.port),0);
			server.createContext("/",defaultHandler);
			server.start();
			System.out.println("Server up and listening on port "+options.port);
			System.out.println("Setting up pipeline");
			CompletePipeline pipeline=new CompletePipeline(options);
			parseHandler=new ParseRequestHandler(defaultHandler, pipeline);
			server.createContext("/parse",parseHandler);
			statusHandler=new ParserStatusHandler(pipeline);
			server.createContext("/status",statusHandler);
			ready=true;
			System.out.println("Server loaded successfully, ready to parse!");
		} catch (Exception e) {
			System.out.println("Caught exception while setting up server, exiting.");
			e.printStackTrace(System.out);
			System.out.println();
			if(server!=null){
				server.stop(0);
			}
		}
	}


	public static boolean isReady() {
		return ready;
	}
	
	
	
}
