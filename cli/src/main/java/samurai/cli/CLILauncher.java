/**
 * 
 */
package samurai.cli;

import java.nio.file.Paths;
import java.io.File;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import samurai.html.HtmlRenderer;
import samurai.web.SamuraiVelocityLogger;

/**
 * @author erik.hess
 *
 */
public class CLILauncher {

	public static final String RELEASE_VERSION = "v3.1";
	// TODO Add more static string definitions here - these should probably define all components of the opts/args up top (or in their own class?) to avoid in-code edits later.
	private static final String OPTION_HELP = "h";
	// Later we may output to different formats but for now html is it. Commenting out "Format" options
	//private static final String OPTION_FORMAT = "-f";
	private static final String OPTION_INPUT = "i";
	private static final String OPTION_OUTPUT = "o";
	private static final String OPTION_PROGRESS = "p";
	private static final String OPTION_DEBUG_LONG = "debug";

	// Default logging not debug since this is mostly a completed version. Whew.
	public static int DEFAULT_LOG_LEVEL = 1;


	// TODO Add telemetry/metrics for Duration, Throughput, Saturation, Errors to all

	// TODO Redo CLI output possibly using a totally fresh class with its own main()

	// TODO Add more logging to this main() class!

	/**
	 *  out() = logger object
	 */
	public static SamuraiVelocityLogger out = new SamuraiVelocityLogger(DEFAULT_LOG_LEVEL);

	private static Options options = new Options();

	/**
	 * PSVM main() class for CLI Samurai interface
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		out.setLevel(DEFAULT_LOG_LEVEL);

		out.logInfo("------------------------------------------ Konichiwa! ----");
		out.logInfo("---- Samurai " + RELEASE_VERSION + " ---------------------------- Start! ----");
		out.logInfo("----------------------------------------------------------");

		out.logInfo("STARTUP:: LOGGING: " + out.getLevel());

		out.logInfo("STARTUP:: main() entrance!");

		/* 
		 * Set up to handle CLI args the Apache Commons-CLI way :)
		 * 
		 * Not done yet though... More to do...
		 * 
		 * TODO Move argument handling into its own class
		 * TODO Run through handling in the "we got args" if branch instead of EVERY TIME.
		 */

		/* Add to list of CLI options */
		/* TODO Break option assignment/constants out into its own class, either in Core or in a CLI-focused project */
		options.addOption(OPTION_HELP, "help", false, "Output html");
		// options.addOption("f", false, "Output Format (default: html");
		options.addOption(OPTION_INPUT, "input", true, "Thread dump file path");
		options.addOption(OPTION_OUTPUT, "output", true, "Output directory path");
		options.addOption(OPTION_PROGRESS, "progress", false, "Print progress");
		options.addOption(OPTION_DEBUG_LONG, false, "Enable debug & trace logging");

		/* Set up to parse in a try */
		CommandLineParser parser = new DefaultParser();

		out.logDebug("STARTUP:: CLI Handling start!");
		// Init vars before try
		String threadDumpPath = null;
		String outputPath = null;
		Boolean progressFlag = null;
		// Try to parse the CLI args, fail well if it doesn't work out
		try {
			CommandLine cmd = parser.parse(options, args);
			threadDumpPath = cmd.getOptionValue(OPTION_INPUT);
			outputPath = cmd.getOptionValue(OPTION_OUTPUT);
			progressFlag = cmd.hasOption(OPTION_PROGRESS);
			if (cmd.hasOption(OPTION_DEBUG_LONG)) {
				out.setLevel(-1);
			} else {
				out.setLevel(1);
			}
			if (cmd.hasOption(OPTION_HELP)) {
				printHelp(options);
			}
		} catch (ParseException e) {
			out.logError("CLI:: ParseException! [" + e.getMessage() + "]");
		} catch (Exception e) {
			out.logError("CLI:: Exception! [" + e.getMessage() + "]"); }
		out.logDebug("STARTUP:: CLI Handling complete!");

		if( args != null && args.length > 0 ) {
			// If we've actually got args, proceed to do the things. :D	

			out.logInfo("----------------------------------------------------------");
			out.logInfo("---- Samurai v3.1 ------------ CLI HTML Render Start! ----");
			out.logInfo("----------------------------------------------------------");

			out.logDebug("Main() if(args):: Got some args! args.length(): [" + args.length + "]");
			out.logDebug("Main() if(args):: Args: [" + Arrays.toString(args) + "]");
			out.logInfo("Main() if(args):: input:  [" + threadDumpPath + "]");
			out.logInfo("Main() if(args):: output: [" + outputPath + "]");
			out.logDebug("Main() if(args):: Handing off to parseAndGenerateHTML()");

			parseAndGenerateHTML(threadDumpPath, outputPath, progressFlag);
			out.logInfo("----------------------------------------------------------");
			out.logInfo("---- Samurai v3.1 --------- CLI HTML Render Complete! ----");
			out.logInfo("----------------------------------------------------------");
		} else {
			out.logDebug("Main() if(args):: Got zero args! args.length(): [" + args.length + "]");
			// If we don't have any args, that's fine, just chill out and fade away
			out.logInfo("----------------------------------------------------------");
			out.logInfo("---- Samurai v3.1 ----------------- No Args! Goodbye. ----");
			out.logInfo("----------------------------------------------------------");
			printHelp(options);
		}

		out.logInfo("Main(): Nothing left to do!");
		out.logInfo("----------------------------------------------------------");
		out.logInfo("---- Samurai v3.1 ------------------Samurai Complete! ----");
		out.logInfo("--------------------------------------- Domo arigato! ----");
		System.exit(0);
	}

	private static void parseAndGenerateHTML(String threadDumpPath, String outputPath, Boolean progressFlag){
		out.logDebug("Main().parseAndGenerateHTML():: parseAndGenerateHTML() Start!");
		out.logDebug("Main().parseAndGenerateHTML():: input:  [" + threadDumpPath + "]");
		out.logDebug("Main().parseAndGenerateHTML():: output: [" + outputPath + "]");
		out.logDebug("Main().parseAndGenerateHTML():: progressFlag: [" + progressFlag + "]");

		out.logDebug("Main().parseAndGenerateHTML():: Var check start!");
		// Check for null input path values since that won't work, lol
		if(threadDumpPath == null) {
			out.logWarn("Main().parseAndGenerateHTML():: Null path for input!");
			out.logWarn("Main().parseAndGenerateHTML():: input:  [" + threadDumpPath + "]");
			out.logWarn("Main().parseAndGenerateHTML():: output: [" + outputPath + "]");
			System.exit(1); 
		} else {
			// If our path ain't null, lets make it a real (absolute) path just in case its a dumb relative path.
			try { 
				threadDumpPath = Paths.get(threadDumpPath).toFile().getAbsolutePath(); 
			} catch (Exception e) {
				out.logError("Main().parseAndGenerateHTML():: Exception on getAbsolutePath() for input path! [" + e.getMessage() + "]");
				e.printStackTrace(); 
				System.exit(1);
			}
		}
		// Check for null output path string
		if(outputPath == null) {
			out.logWarn("Main().parseAndGenerateHTML():: Null path for output! Making output parent dir of original file!");
			// So we didn't get an output path. SAD. Lets just put it where the input's at, yeah?
			// TODO Maybe add more args so that we can auto-create target directories based on Jira ticket, date, server, etc?
			try {
				outputPath = Paths.get(threadDumpPath).toFile().getParentFile().getAbsolutePath() + "/html";
			} catch (Exception e) {
				out.logError("Main().parseAndGenerateHTML():: Exception in output path autocreate! [" + e + "]");
				e.printStackTrace();
				System.exit(1);
			} 
			out.logInfo("Main().parseAndGenerateHTML():: Output dir empty! Updated it with input file's parent dir. :)");
			out.logInfo("Main().parseAndGenerateHTML():: updated output: [" + outputPath + "]");
		} else {
			// If our path ain't null, lets make it a real (absolute) path just in case its a dumb relative path.
			try { outputPath = Paths.get(outputPath).toFile().getAbsolutePath(); 
			} catch (Exception e) {
				out.logError("Main().parseAndGenerateHTML():: Exception on getAbsolutePath() for output path! [" + e.getMessage() + "]");
				e.printStackTrace();
				System.exit(1);
			}
		}

		File threadDumpFile = new File(threadDumpPath);
		if(!threadDumpFile.exists() || !threadDumpFile.isFile()) {
			out.logWarn("Main().parseAndGenerateHTML():: No existence or not file for input!");
			out.logWarn("Main().parseAndGenerateHTML():: input:  [" + threadDumpPath + "]");
			out.logWarn("Main().parseAndGenerateHTML():: output: [" + outputPath + "]");
			System.exit(1); }

		// Check for output dir existence and, if it doesn't exist, create it
		File outputDir = new File(outputPath);
		if(!outputDir.exists()) {
			out.logInfo("Main().parseAndGenerateHTML():: mkdir() required for output path: [" + outputPath + "]");
			try {
				boolean dirMade = outputDir.mkdir();
				out.logDebug("Main().parseAndGenerateHTML():: mkdir() dirMade: [" + dirMade + "]");
			} catch (SecurityException e) {
				out.logError("Main().parseAndGenerateHTML():: SecurityException on mkdir()! Check writability!");
				out.logError("Main().parseAndGenerateHTML():: input:  [" + threadDumpPath + "]");
				out.logError("Main().parseAndGenerateHTML():: output: [" + outputPath + "]");
				out.logError("Main().parseAndGenerateHTML():: Exception message: [" + e.getMessage() + "]");
				System.exit(1);
			} catch (Exception e) {
				out.logError("Main().parseAndGenerateHTML():: Exception on mkdir()!");
				out.logError("Main().parseAndGenerateHTML():: input:  [" + threadDumpPath + "]");
				out.logError("Main().parseAndGenerateHTML():: output: [" + outputPath + "]");
				out.logError("Main().parseAndGenerateHTML():: Exception message: [" + e.getMessage() + "]");
				System.exit(1);
			}
		}

		// Check to make sure output directory is, like, actually there.
		if(!outputDir.exists()) {
			out.logError("Main().parseAndGenerateHTML():: exists() fail for output dir!");
			out.logError("Main().parseAndGenerateHTML():: input:  [" + threadDumpPath + "]");
			out.logError("Main().parseAndGenerateHTML():: output: [" + outputPath + "]");
			System.exit(1); }
		// ... and a directory.
		if(!outputDir.isDirectory()) {
			out.logError("Main().parseAndGenerateHTML():: isDirectory() fail for output dir!");
			out.logError("Main().parseAndGenerateHTML():: input:  [" + threadDumpPath + "]");
			out.logError("Main().parseAndGenerateHTML():: output: [" + outputPath + "]");
			System.exit(1); }

		out.logDebug("Main().parseAndGenerateHTML():: Var check success!");

		// Do that funky rendering we love so well.
		HtmlRenderer htmlRenderer = new HtmlRenderer();

		out.logDebug("Main().parseAndGenerateHTML():: calling htmlRender.extract!");

		try {
			htmlRenderer.extract(threadDumpFile, outputDir, progressFlag);
		} catch (Exception e) {
			out.logError("Main().parseAndGenerateHTML():: Exception on htmlRender()!");
			out.logError("Main().parseAndGenerateHTML():: input:  [" + threadDumpPath + "]");
			out.logError("Main().parseAndGenerateHTML():: output: [" + outputPath + "]");
			out.logError("Main().parseAndGenerateHTML():: Exception message: [" + e.getMessage() + "]");
			e.printStackTrace();
		}

		// end parseAndGenerateHTML(string, string, boolean)
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar samurai.jar [-i <inputfile> -o <outputfile>]", options );
		System.exit(0);
	}

}
