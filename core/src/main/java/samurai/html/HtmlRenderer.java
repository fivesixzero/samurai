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

import static samurai.util.FileUtil.saveStreamAsFile;
import static samurai.util.FileUtil.getFileCount;

/**
 * Output the HTML of a thread dump
 *
 */
public class HtmlRenderer {
	
	private static int LOG_LEVEL = 1; 
    
    SamuraiVelocityLogger out = new SamuraiVelocityLogger(LOG_LEVEL);
    
    ThreadDumpExtractor extractor;
    ThreadStatistic statistic;

    public HtmlRenderer() {
        statistic = new ThreadStatistic();
        extractor = new ThreadDumpExtractor(statistic);
    }

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
        	out.logInfo("HTMLExtract:: extractor.analyze() Start!");
            try {
                extractor.analyze(threadDump);
                out.logInfo("HTMLExtract:: STATS: Analyzed: Unique Thread Dump Count    = " + statistic.getFullThreadDumpCount());
                out.logInfo("HTMLExtract:: STATS: Analyzed: Avg Threads per Thread Dump = " + statistic.getAvgThreadsPerThreadDump());
                out.logInfo("HTMLExtract:: STATS: Analyzed: Total Threads Analyzed      = " + statistic.getTotalThreads());
            } catch (Exception e) {
                out.logError("HTMLExtract:: EXCEPTION while analyzing! [" + e + "]");
                throw e;
            }
        	out.logInfo("HTMLExtract:: extractor.analyze() Complete!");
            out.logInfo("HTMLExtract:: VelocityHtmlRenderer() Start!");
            VelocityHtmlRenderer renderer;
            try {
                renderer = new VelocityHtmlRenderer("samurai/web/outcss.vm", "../images/");
            } catch (Exception e) {
                out.logError("HTMLExtract:: EXCEPTION while rendering! [" + e + "]");
                throw e;
            }
            out.logInfo("HTMLExtract:: VelocityHtmlRenderer() Complete!!");
            
            out.logDebug("HTMLExtract:: Writing: " + targetDirectory.toString());
            out.logDebug("HTMLExtract:: Reading: Dir Exists = " + Paths.get(targetDirectory.toString()).toFile().exists());
        	out.logInfo("HTMLExtract:: renderer.saveTo() Start!");
            renderer.saveTo(statistic, targetDirectory, new ProgressListener() {
                @Override
                public void notifyProgress(int completed, int max) {
                    if(printProgress) {
                        out.logInfo("Completed/max: " + completed + " / " + max);
                    }
                }
            });
        	out.logInfo("HTMLWrite:: renderer.saveTo() Complete!");
        	out.logInfo("HTMLWrite:: renderer.saveTo() TargetDirectory contains " + getFileCount(targetDirectory.toPath()) + " files after op.");
            
            File imageDir = new File(targetDirectory.getAbsolutePath() + "/images/");
            out.logDebug("HTMLWriteImgs:: mkdir(imageDir): [" + imageDir.toString() + "]");
            try {
                boolean result = imageDir.mkdir();
                out.logTrace("HTMLWriteImgs:: mkdir(imageDir): Did we make a new dir for output? [" + result + "]");
            } catch (Exception e) {
                out.logError("HTMLWriteImgs:: mkdir(imageDir): EXCEPTION while mkdir()! [" + e + "]");
                throw e;
            }
        	out.logDebug("HTMLWriteImgs:: mkdir(imageDir): Complete!");
            out.logDebug("HTMLWriteImgs:: imageDir Images Copy: Start!");
            try {
                out.logTrace("HTMLWriteImgs:: imageDir Images Copy saveStreamAsFile() Try: Start!");
            	saveStreamAsFile(imageDir, "space.gif");
            	saveStreamAsFile(imageDir, "same-v.gif");
            	saveStreamAsFile(imageDir, "same-h.gif");
            	saveStreamAsFile(imageDir, "deadlocked.gif");
            	saveStreamAsFile(imageDir, "expandable_win.gif");
            	saveStreamAsFile(imageDir, "shrinkable_win.gif");
            	saveStreamAsFile(imageDir, "tableButton.gif");
            	saveStreamAsFile(imageDir, "fullButton.gif");
            	saveStreamAsFile(imageDir, "sequenceButton.gif");
                out.logTrace("HTMLWriteImgs:: imageDir Images Copy saveStreamAsFile() Try: Complete!");
            } catch (IOException e) {
                out.logError("HTMLWriteImgs:: imageDir Images Copy Try: I/O Exception in saveStreamAsFile()! [" + e + "]");
                throw e;
            } catch (Exception e) {
                out.logError("HTMLWriteImgs:: imageDir Images Copy Try: Exception in saveStreamAsFile()! [" + e + "]");
                throw e;
            }
            out.logDebug("HTMLWriteImgs:: imageDir Images Copy: Complete!");

        } catch (IOException e) {
            out.logError("HTMLExtract:: IO Exception while trying to complete HTML Render! [" + e + "]");
            throw e;
        } catch (IndexOutOfBoundsException e) {
            out.logError("HTMLExtract:: IndexOutOfBounds while trying to complete HTML Render! [" + e + "]");
            throw e;
        } catch (Exception e) {
            out.logError("HTMLExtract:: Unspecific Exception while trying to complete HTML Render! [" + e + "]");
            e.printStackTrace();
            return;
        }
    	out.logInfo("HTMLExtract:: extractor.analyze() Complete!");        
    }
} 
    





