
/*
 * *
 *  Copyright 2015 Yusuke Yamamoto
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  Distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package samurai.remotedump;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import sun.tools.attach.HotSpotVirtualMachine;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class ThreadDumpUtil {

    public static byte[] getThreadDump(int pid) throws AttachNotSupportedException, IOException {
        HotSpotVirtualMachine virtualMachine = null;
        try {
            virtualMachine = (HotSpotVirtualMachine) VirtualMachine.attach(String.valueOf(pid));
            try (InputStream in = virtualMachine.remoteDataDump()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int count;
                byte[] buf = new byte[256];
                while ((count = in.read(buf)) != -1) {
                    baos.write(buf, 0, count);
                }
                return baos.toByteArray();
            }
        } finally {
            if (virtualMachine != null) {
                try {
                    virtualMachine.detach();
                } catch (IOException ignore) {
                }
            }
        }

    }
    public static String getThreadDumpAsString(int pid) throws AttachNotSupportedException, IOException {
        return new String(getThreadDump(pid), Charset.forName("UTF-8"));
    }
}