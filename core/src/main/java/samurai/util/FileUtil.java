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
package samurai.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    /**
     * Copy an image from resources into the specified directory
     *
     * @param parentDir Directory to write to
     * @param fileName Name of the resource file
     * @throws IOException
     */
    public static void saveStreamAsFile(File parentDir, String fileName) throws IOException {
        InputStream is = FileUtil.class.getResourceAsStream("/samurai/web/images/" + fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(parentDir.getAbsolutePath() + "/" + fileName);
            byte[] buf = new byte[256];
            int count;
            while (-1 != (count = is.read(buf))) {
                fos.write(buf, 0, count);
            }
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException ignore) {
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
    
    public static long getFileCount(Path dir) throws IOException, SecurityException { 
    	long count = 0;
    	try {
    		count = Files.walk(dir)
    				.parallel()
    				.filter(p -> !p.toFile().isDirectory())
    				.count();
    	} catch (SecurityException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace(); }
    	return count;
      }

}