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
package samurai.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class NullVelocityLogger implements LogChute {
    
    // Constant: Limit classnames to 24 chars
    static int CHARLIMIT_CLASS = 24;
    
    // Default: logLevel INFO (no TRACE/DEBUG)
    private int logLevel = INFO_ID;
    
    public NullVelocityLogger() {
    }
    
    public void init(RuntimeServices runtimeServices) throws Exception {
    }

    public void logVelocityMessage(int level, String msg) {
        String levelString = null;
        switch (level) {
            case TRACE_ID: levelString = "TRACE";
                break;
            case DEBUG_ID: levelString = "DEBUG";
                break;
            case INFO_ID: levelString  = "INFO ";
                break;
            case WARN_ID: levelString  = "WARN ";
                break;
            case ERROR_ID: levelString = "ERROR";
                break;
            default:       levelString = "INFO ";
                break;
        }
        
        DateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateTimeString  = dateFormat.format(new Date());
        String classNameString = this.getClass().getCanonicalName();
        
        // logging to System.out for now, if we even log anything...
        if (level >= logLevel) {
            System.out.printf("[%s %s %s] %s\n",dateTimeString,levelString,classNameString.substring(Math.max(0, classNameString.length() - CHARLIMIT_CLASS)),msg);
        }
    }

    public boolean isLevelEnabled(int level) {
        if (level == logLevel) {
            return true;
        }
        return false;
    }
    
    public boolean setLevel(int level) {
        if (level <= ERROR_ID && level >= TRACE_ID) {
            logLevel = level;
            return true;
        } else {
            return false;
        }
    }
    
    public int getLevel(int level) {
        return level;
    }

    public void log(int level, String msg) {
        logVelocityMessage(level, msg);
    }

    public void log(int level, String msg, Throwable e) {
        logVelocityMessage(level, msg + " EXCEPTION: " + e);
    }
    
    public void logTrace(String msg) {
        logVelocityMessage(TRACE_ID, msg);   
    }

    public void logDebug(String msg) {
        logVelocityMessage(DEBUG_ID, msg);   
    }
    
    public void logInfo(String msg) {
        logVelocityMessage(INFO_ID, msg);   
    }
    
    public void logWarn(String msg) {
        logVelocityMessage(WARN_ID, msg);   
    }
    
    public void logError(String msg) {
        logVelocityMessage(ERROR_ID, msg);   
    }

}
