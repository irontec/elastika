/**
 * 
 */
package com.irontec;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 * @author axier
 *
 */
public class Elastika {

	private static String mElasticIndice = "";
	private static String mElasticType = "";
	private static String mFileName = "";
	private static String mHostname = "http://localhost";
	private static String mPort = "9200";

	// Tika options
	private static final String TIKA_OPTION_JSON_METADATA = "-j";
	private static final String TIKA_OPTION_PLAINTEXT_CONTENT = "-T";	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Options options = generateOptions();

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse( options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		readOptions(cmd, options);

		String extractTikaJsonMetadata = String.format("%s %s %s",
				"java -jar tika-app.jar", TIKA_OPTION_JSON_METADATA, mFileName);
		String extractTikaPlainTextContent = String.format("%s %s %s",
				"java -jar tika-app.jar", TIKA_OPTION_PLAINTEXT_CONTENT, mFileName);

		String jsonMetadata = "";
		String plainTextContent = "";
		try {
			jsonMetadata = executeRuntimeCommand(extractTikaJsonMetadata);
			plainTextContent = executeRuntimeCommand(extractTikaPlainTextContent);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		JSONObject jsonObj = new JSONObject(jsonMetadata);
		jsonObj.put("text", plainTextContent);
		
		String elasticResponse = "";
		try {
			elasticResponse = postDataToElastic(jsonObj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonElastic = new JSONObject(elasticResponse);
		System.out.println(jsonElastic.toString());
		
	}
	
	public static String postDataToElastic(String data) throws IOException {
		
		String elasticEndpoint = String.format("%s:%s/%s/%s/",
				mHostname, mPort, mElasticIndice, mElasticType);
				
		byte[] postData = data.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;
		URL url = new URL(elasticEndpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
		
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json"); 
		conn.setRequestProperty("charset", "utf-8");
		conn.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
		conn.setUseCaches(false);
		
		DataOutputStream wr = null;
		try {
			wr = new DataOutputStream(conn.getOutputStream());
			wr.write( postData );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wr.flush ();
		    wr.close ();
		}
		
		InputStream is = conn.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer(); 
		while((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		return response.toString();
	}

	public static String executeRuntimeCommand(String command) throws IOException, InterruptedException {

		System.out.println("Executing: " + command);

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);
		proc.waitFor();

		BufferedReader stdInput = new BufferedReader(new 
				InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new 
				InputStreamReader(proc.getErrorStream()));

		String error = IOUtils.toString(stdError);
		if (!error.isEmpty()) {
			// We have errors and thus we must exit
			System.out.print(error);
			System.exit(0);
		}

		String result = IOUtils.toString(stdInput);

		return result;
	}

	public static Options generateOptions() {
		Options options = new Options();

		options.addOption("i","indice", true, "(Required) Elastic indice name.");

		options.addOption("t", "type", true, "(Required) Elastic indice type name.");

		options.addOption("f", "file", true, "(Required) The document to be parsed and sent to Elastic.");

		options.addOption("host", true, "(Optional) Elastic REST Endpoint hostname. Default http://localhost.");

		options.addOption("p", "port", true, "(Optional) Elastic REST Endpoint port. Default 9200.");

		options.addOption("h", "help", false, "Print this usage message");

		return options;
	}

	public static void readOptions(CommandLine cmd, Options options) {
		if (cmd != null) {
			if (cmd.hasOption("h") || cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "elastika", options);
				System.exit(0);
			}
			if (cmd.hasOption("i")) {
				mElasticIndice = cmd.getOptionValue("i");
			}
			if(cmd.hasOption("indice")) {
				mElasticIndice = cmd.getOptionValue("indice");
			}
			if(mElasticIndice.isEmpty()) {
				System.out.print("Missing required parameter Elastic indice. Try executing the program with -i or --indice options and the name of the indice");
				System.exit(0);
			}
			if (cmd.hasOption("t")) {
				mElasticType = cmd.getOptionValue("t");
			}
			if(cmd.hasOption("type")) {
				mElasticType = cmd.getOptionValue("type");
			}
			if(mElasticType.isEmpty()) {
				System.out.print("Missing required parameter Elastic indice type. Try executing the program with -t or --type options and the name of the indice type");
				System.exit(0);
			}
			if (cmd.hasOption("f")) {
				mFileName = cmd.getOptionValue("p");
			}
			if(cmd.hasOption("file")) {
				mFileName = cmd.getOptionValue("file");
			}
			if(mFileName.isEmpty()) {
				System.out.print("Missing required parameter local file name. Try executing the program with -f or --file options and the name of the file");
				System.exit(0);
			}
			if (cmd.hasOption("host")) {
				mHostname = cmd.getOptionValue("host");
			}
			if (cmd.hasOption("p")) {
				mPort = cmd.getOptionValue("p");
			}
			if(cmd.hasOption("port")) {
				mPort = cmd.getOptionValue("port");
			}
		}
	}
}
