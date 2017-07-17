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


public class Samurai {
        

    // TODO Add telemetry/metrics for Duration, Throughput, Saturation, Errors to all
    
    // TODO Redo CLI output possibly using a totally fresh class with its own main()
    private static final String OPTION_HTML = "-html";
    private static final String OPTION_THREAD_DUMP = "-td";
    private static final String OPTION_OUTPUT = "-o";

    // TODO Add logging to this main() class!
    private SamuraiVelocityLogger out = new SamuraiVelocityLogger();
    
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();

    public static void main(String[] args) throws Exception {
        if(args!=null && args.length > 0 && args[0].equals(OPTION_HTML)) {
            parseAndGenerateHTML(args);
        } else {
            startGUI();
        }
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

    private static void parseAndGenerateHTML(String[] args){
        Options options = new Options();
        options.addOption("html", false, "Output html");
        options.addOption("td", "threaddump", true, "Thread dump file path");
        options.addOption("o", "output", true, "Output directory path");
        options.addOption("p", "progress", false, "Print progress");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String threadDumpPath = cmd.getOptionValue("td");
            String outputPath = cmd.getOptionValue("o");
            boolean printProgress = cmd.hasOption("p");
            if(threadDumpPath == null || outputPath == null) {
                printHelp(options);
                return;
            }
            File threadDumpFile = new File(threadDumpPath);
            if(!threadDumpFile.exists() || !threadDumpFile.isFile()) {
                System.err.println(threadDumpPath + " is not a file");
                printHelp(options);
                return;
            }
            File outputDir = new File(outputPath);
            if(!outputDir.exists()) {
                outputDir.mkdir();
            }
            if(!outputDir.exists() || !outputDir.isDirectory()) {
                System.err.println(outputPath + " does not exist");
                printHelp(options);
                return;
            }
            HtmlRenderer htmlRenderer = new HtmlRenderer();
            htmlRenderer.extract(threadDumpFile, outputDir, printProgress);
        } catch (ParseException e) {
            printHelp(options);
        }
    }

    private static void printHelp(Options options) {
        System.out.println("Bad arguments");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "java -jar samurai.jar", options );
    }
}
