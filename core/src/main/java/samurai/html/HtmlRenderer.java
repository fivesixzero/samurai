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
import samurai.web.ProgressListener;
import samurai.web.VelocityHtmlRenderer;

import java.io.File;
import java.io.IOException;

import static samurai.util.FileUtil.saveStreamAsFile;

/**
 * Output the HTML of a thread dump
 *
 */
public class HtmlRenderer {

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
     */
    public void extract(File threadDump, File targetDirectory, final boolean printProgress) {
        try {
            extractor.analyze(threadDump);
            VelocityHtmlRenderer renderer = new VelocityHtmlRenderer("samurai/web/outcss.vm", "../images/");
            renderer.saveTo(statistic, targetDirectory, new ProgressListener() {
                @Override
                public void notifyProgress(int completed, int max) {
                    if(printProgress) {
                        System.out.println("Completed/max: " + completed + " / " + max);
                    }
                }
            });
            File imageDir = new File(targetDirectory.getAbsolutePath() + "/images/");
            imageDir.mkdir();
            saveStreamAsFile(imageDir, "space.gif");
            saveStreamAsFile(imageDir, "same-v.gif");
            saveStreamAsFile(imageDir, "same-h.gif");
            saveStreamAsFile(imageDir, "deadlocked.gif");
            saveStreamAsFile(imageDir, "expandable_win.gif");
            saveStreamAsFile(imageDir, "shrinkable_win.gif");

            saveStreamAsFile(imageDir, "tableButton.gif");
            saveStreamAsFile(imageDir, "fullButton.gif");
            saveStreamAsFile(imageDir, "sequenceButton.gif");
        } catch (IOException e) {
            System.err.println("IOException occurred " + e.getMessage());
            e.printStackTrace();
        }
    }
}
