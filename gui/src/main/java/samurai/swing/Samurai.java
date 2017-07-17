/*
 * Copyright 2003-2012 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samurai.swing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import samurai.html.HtmlRenderer;
import samurai.util.GUIResourceBundle;
import samurai.util.OSDetector;
import samurai.web.SamuraiVelocityLogger;

import java.io.File;
import java.util.Arrays;

public class Samurai {
	

	private static final String OPTION_HELP = "h";
	// Later we may output to different formats but for now html is it. Commenting out "Format" options
	//private static final String OPTION_FORMAT = "-f";
	private static final String OPTION_INPUT = "i";
	private static final String OPTION_OUTPUT = "o";
	private static final String OPTION_PROGRESS = "p";

	public static int LOG_LEVEL = -1;
	
    // TODO Add telemetry/metrics for Duration, Throughput, Saturation, Errors to all
    
    // TODO Redo CLI output possibly using a totally fresh class with its own main()
    
    // TODO Add more logging to this main() class!
    private static SamuraiVelocityLogger out = new SamuraiVelocityLogger(LOG_LEVEL);
    
    private static Options options = new Options();
    
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();

    public static void main(String[] args) throws Exception {
    	out.logInfo("----------------------------------------------------------");
    	out.logInfo("---- Samurai v3.1 ---------------------------- Start! ----");
    	out.logInfo("----------------------------------------------------------");
    	
    	// TODO Disable debug logging before releasing...
    	out.setLevel(-1);
    	
    	out.logDebug("LOGGING: Debug is turned on!");
    
    	out.logInfo("STARTUP:: main() entrance!");
    	
    	/* set up to handle CLI args the Apache Commons-CLI way :) */

        /* Add to list of CLI options */
        /* TODO Break option assignment/constants out into its own class, either in Core or in a CLI-focused project */
        options.addOption(OPTION_HELP, "help", false, "Output html");
        // options.addOption("f", false, "Output Format (default: html");
        options.addOption(OPTION_INPUT, "input", true, "Thread dump file path");
        options.addOption(OPTION_OUTPUT, "output", true, "Output directory path");
        options.addOption(OPTION_PROGRESS, "progress", false, "Print progress");
    	
        /* Set up to parse in a try */
        CommandLineParser parser = new DefaultParser();

    	out.logDebug("STARTUP:: CLI Handling start!");
    	// Init vars before try
    	String threadDumpPath = null;
    	String outputPath = null;
    	Boolean progressFlag = null;
    	// Try to parse the CLI, fail well if it doesn't work out
        try {
            CommandLine cmd = parser.parse(options, args);
            threadDumpPath = cmd.getOptionValue(OPTION_INPUT);
            outputPath = cmd.getOptionValue(OPTION_OUTPUT);
            progressFlag = cmd.hasOption(OPTION_PROGRESS);
        } catch (ParseException e) {
        	out.logError("CLI:: ParseException! [" + e.getMessage() + "]");
        } catch (Exception e) {
        	out.logError("CLI:: Exception! [" + e.getMessage() + "]"); }
        out.logDebug("STARTUP:: CLI Handling complete!");
        
        // If we've actually got any args, proceed to generate that HTML. :D
        if(args!=null && args.length > 0) {
        	out.logInfo("----------------------------------------------------------");
        	out.logInfo("---- Samurai v3.1 ------------ CLI HTML Render Start! ----");
        	out.logInfo("----------------------------------------------------------");
        	
        	out.logDebug("ARGSCHECK:: Got args! args.length(): [" + args.length + "]");
        	out.logDebug("ARGSCHECK:: Args: [" + Arrays.toString(args) + "]");
        	out.logDebug("ARGSCHECK:: input:  [" + threadDumpPath + "]");
        	out.logDebug("ARGSCHECK:: output: [" + outputPath + "]");
        	out.logDebug("ARGSCHECK:: Handing off to parseAndGenerateHTML()");
        	
            parseAndGenerateHTML(threadDumpPath, outputPath, progressFlag);
        // If we don't have any args, that's fine, just fire up the old GUI and be sloth-like
        } else {
        	out.logInfo("----------------------------------------------------------");
        	out.logInfo("---- Samurai v3.1 ------------------------ GUI Start! ----");
        	out.logInfo("----------------------------------------------------------");
            startGUI();
        }

    	out.logInfo("----------------------------------------------------------");
    	out.logInfo("---- Samurai v3.1 ------------------------- Complete! ----");
    	out.logInfo("----------------------------------------------------------");
    	
    }
    
    private static void parseAndGenerateHTML(String threadDumpPath, String outputPath, Boolean progressFlag){
    	out.logInfo("HTML:: parseAndGenerateHTML() Start!");
        out.logDebug("HTML:: input:  [" + threadDumpPath + "]");
        out.logDebug("HTML:: output: [" + outputPath + "]");
        out.logDebug("HTML:: progressFlag: [" + progressFlag + "]");

        out.logDebug("HTML:: Var check start!");
        // Check for null in/out path values since that won't work, lol
        if(threadDumpPath == null) {
        	out.logWarn("HTML:: Null path for input!");
        	out.logWarn("HTML:: input:  [" + threadDumpPath + "]");
        	out.logWarn("HTML:: output: [" + outputPath + "]");
    		System.exit(1); }
        if(outputPath == null) {
        	out.logWarn("HTML:: Null path for output!");
        	out.logWarn("HTML:: input:  [" + threadDumpPath + "]");
        	out.logWarn("HTML:: output: [" + outputPath + "]");
    		System.exit(1); }
        // Check for actual file existence since we might need one.
        File threadDumpFile = new File(threadDumpPath);
        if(!threadDumpFile.exists() || !threadDumpFile.isFile()) {
        	out.logWarn("HTML:: No existence or not file for input!");
        	out.logWarn("HTML:: input:  [" + threadDumpPath + "]");
        	out.logWarn("HTML:: output: [" + outputPath + "]");
    		System.exit(1); }
        
        // Check for output dir existence and, if it doesn't exist, create it
        File outputDir = new File(outputPath);
        if(!outputDir.exists()) {
        	out.logInfo("HTML:: mkdir() required for output path: [" + outputPath + "]");
        	try {
        		boolean dirMade = outputDir.mkdir();
            	out.logDebug("HTML:: mkdir() dirMade: [" + dirMade + "]");
        	} catch (SecurityException e) {
        		out.logError("HTML:: SecurityException on mkdir()! Check writability!");
        		out.logError("HTML:: input:  [" + threadDumpPath + "]");
        		out.logError("HTML:: output: [" + outputPath + "]");
        		out.logError("HTML:: Exception message: [" + e.getMessage() + "]");
        		System.exit(1);
        	} catch (Exception e) {
        		out.logError("HTML:: Exception on mkdir()!");
        		out.logError("HTML:: input:  [" + threadDumpPath + "]");
        		out.logError("HTML:: output: [" + outputPath + "]");
        		out.logError("HTML:: Exception message: [" + e.getMessage() + "]");
        		System.exit(1);
        	}
        }
        
        // Check to make sure output directory is, like, actually there.
        if(!outputDir.exists()) {
        	out.logError("HTML:: exists() fail for output dir!");
    		out.logError("HTML:: input:  [" + threadDumpPath + "]");
    		out.logError("HTML:: output: [" + outputPath + "]");
    		System.exit(1); }
        // ... and a directory.
        if(!outputDir.isDirectory()) {
        	out.logError("HTML:: isDirectory() fail for output dir!");
    		out.logError("HTML:: input:  [" + threadDumpPath + "]");
    		out.logError("HTML:: output: [" + outputPath + "]");
    		System.exit(1); }

        out.logDebug("HTML:: Var check success!");

        // Do that funky rendering we love so well.
        HtmlRenderer htmlRenderer = new HtmlRenderer();
        out.logDebug("HTML:: calling htmlRender.extract!");
        try {
        	htmlRenderer.extract(threadDumpFile, outputDir, progressFlag);
        } catch (Exception e) {
    		out.logError("HTML:: Exception on htmlRender()!");
    		out.logError("HTML:: input:  [" + threadDumpPath + "]");
    		out.logError("HTML:: output: [" + outputPath + "]");
    		out.logError("HTML:: Exception message: [" + e.getMessage() + "]");
    		e.printStackTrace();
    		System.exit(1); }

    // end parseAndGenerateHTML(string, string, boolean)
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar samurai.jar [-i <inputfile> -o <outputfile>]", options );
    }
    
    private static void startGUI() {
        if (OSDetector.isMac()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            //    System.setProperty("apple.awt.brushMetalLook", "true");
            System.setProperty("com.apple.macos.useSmallTabs", "true");
            System.setProperty("apple.awt.textantialiasing", "true");
            System.setProperty("com.apple.mrj.application.live-resize", "false");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                resources.getMessage("Samurai"));
        }
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            javax.swing.JFrame frame = new MainFrame();
            frame.validate();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
