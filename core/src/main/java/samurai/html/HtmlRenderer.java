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
package samurai.html;

import samurai.core.ThreadDumpExtractor;
import samurai.core.ThreadStatistic;
import samurai.web.SamuraiVelocityLogger;
import samurai.web.ProgressListener;
import samurai.web.VelocityHtmlRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import com.sun.tools.javac.util.Log;

import static samurai.util.FileUtil.saveStreamAsFile;

/**
 * Output the HTML of a thread dump
 *
 */
public class HtmlRenderer {
	
	private static int LOG_LEVEL = -1; 
    
    SamuraiVelocityLogger out = new SamuraiVelocityLogger(LOG_LEVEL);
    
    ThreadDumpExtractor extractor;
    ThreadStatistic statistic;

    public HtmlRenderer() {
        statistic = new ThreadStatistic();
        extractor = new ThreadDumpExtractor(statistic);
    }
    
    // TODO Assure that relative paths can work by making the strings .getAbsolutePath()!

    /**
     * Extract the dump file into a html file
     *
     * @param threadDump The thread dump file
     * @param targetDirectory Target directory for the output
     * @param printProgress true if you want progress to be printed
     * @throws IOException 
     * 
     * TODO Add logging like this in all other important classes
     */
    public void extract(File threadDump, File targetDirectory, final boolean printProgress) throws IOException, IndexOutOfBoundsException {
        out.logDebug("HTMLExtract:: START: Reading:   " + threadDump.toString());
        out.logDebug("HTMLExtract:: START: Reading:   Size in bytes = " + Paths.get(threadDump.toString()).toFile().length());
        out.logDebug("HTMLExtract:: START: Reading:   File Exists = " + Paths.get(threadDump.toString()).toFile().exists());
        try {
        	out.logDebug("HTMLExtract:: extractor.analyze() Start!");
            try {
                extractor.analyze(threadDump);
                out.logInfo("HTMLExtract:: Analyzed:  Thread Dump Count = " + statistic.getFullThreadDumpCount());
            } catch (Exception e) {
                out.logError("HTMLExtract:: EXCEPTION while analyzing! [" + e + "]");
                throw e;
            }
        	out.logDebug("HTMLExtract:: extractor.analyze() Complete!");
            out.logDebug("HTMLExtract:: VelocityHtmlRenderer() Start!");
            VelocityHtmlRenderer renderer;
            try {
                renderer = new VelocityHtmlRenderer("samurai/web/outcss.vm", "../images/");
            } catch (Exception e) {
                out.logError("HTMLExtract:: EXCEPTION while rendering! [" + e + "]");
                throw e;
            }
            out.logDebug("HTMLExtract:: VelocityHtmlRenderer() Complete!!");
            
            out.logDebug("HTMLExtract:: Writing:   " + targetDirectory.toString());
            out.logDebug("HTMLExtract:: Reading:   Dir Exists = " + Paths.get(targetDirectory.toString()).toFile().exists());
        	out.logDebug("HTMLExtract:: renderer.saveTo() Start!");
            renderer.saveTo(statistic, targetDirectory, new ProgressListener() {
                @Override
                public void notifyProgress(int completed, int max) {
                    if(printProgress) {
                        out.logInfo("Completed/max: " + completed + " / " + max);
                    }
                }
            });
        	out.logDebug("HTMLR:: renderer.saveTo() Complete!");
        	out.logDebug("HTMLR:: renderer.saveTo() TargetDirectory contains " + targetDirectory.list().length + " files after op.");
            
            File imageDir = new File(targetDirectory.getAbsolutePath() + "/images/");
            out.logDebug("HTMLR:: mkdir(imageDir):   [" + imageDir.toString() + "]");
            try {
                boolean result = imageDir.mkdir();
                out.logTrace("HTMLR:: mkdir(imageDir) result [" + result + "]");
            } catch (Exception e) {
                out.logError("HTMLR:: mkdir(imageDir): EXCEPTION while mkdir()! [" + e + "]");
                throw e;
            }
        	out.logDebug("HTMLR:: mkdir(imageDir): Complete!");
            out.logDebug("HTMLR:: imageDir Images Copy: Start!");
            try {
                out.logTrace("HTMLR:: imageDir Images Copy saveStreamAsFile() Try: Start!");
            	saveStreamAsFile(imageDir, "space.gif");
            	saveStreamAsFile(imageDir, "same-v.gif");
            	saveStreamAsFile(imageDir, "same-h.gif");
            	saveStreamAsFile(imageDir, "deadlocked.gif");
            	saveStreamAsFile(imageDir, "expandable_win.gif");
            	saveStreamAsFile(imageDir, "shrinkable_win.gif");
            	saveStreamAsFile(imageDir, "tableButton.gif");
            	saveStreamAsFile(imageDir, "fullButton.gif");
            	saveStreamAsFile(imageDir, "sequenceButton.gif");
                out.logTrace("HTMLR:: imageDir Images Copy saveStreamAsFile() Try: Complete!");
            } catch (IOException e) {
                out.logError("HTMLR:: imageDir Images Copy Try: I/O Exception in saveStreamAsFile()! [" + e + "]");
                throw e;
            } catch (Exception e) {
                out.logError("HTMLR:: imageDir Images Copy Try: Exception in saveStreamAsFile()! [" + e + "]");
                throw e;
            }
            out.logDebug("HTMLR:: imageDir Images Copy: Complete!");

        } catch (IOException e) {
            out.logError("HTMLR:: IO Exception while trying to complete HTML Render! [" + e + "]");
            throw e;
        } catch (IndexOutOfBoundsException e) {
            out.logError("HTMLR:: IndexOutOfBounds while trying to complete HTML Render! [" + e + "]");
            throw e;
        } catch (Exception e) {
            out.logError("HTMLR:: Unspecific Exceptoin while trying to complete HTML Render! [" + e + "]");
            e.printStackTrace();
            return;
        }
    }
}
