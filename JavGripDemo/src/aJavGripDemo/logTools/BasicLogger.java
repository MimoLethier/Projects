/*
*  Created by MIMO on April 28th, 2016
*
*  CONTEXT :   Easing access to JAVAFX development
*  PURPOSE :  Enabling basic LOGGING functionalities...
*  ROLE :	LIBRARY class proposing a somewhat uncomplicated LOG service
*	   Note :	   This crude logger will open a standard textfile in the User Home folder and dub-folder specified in the Config file under Key "Log_Location"
*			, using as file name specified in the Config file under Key "Log_FileName" . So, for instance, if the Config file contains the property pairs
*			as found hereunder in last comment, then the log started at DateTime will be found in
*			<UserHome>\JavGrip\ZZZLOGS\Run on DateTime.log (by default, if there is no config: <UserHome>\Documents\Logging at DateTime.log)
*			Note also that the SubFolder MUST be created manually beforehand (Documents exist by default on Windows).
*/

package logTools;


// Imported packages
import contextTools.ConfigManager;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;




public class BasicLogger
 {
   // Private Resources
   private PrintWriter logWriter;
   private boolean logISOpen;
   private String lastErrorMessage;




// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//	0 -  OVERALL  INITIALIZATION & TERMINATION
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   // Construction :
   public BasicLogger()
    {
	logWriter = null;
	logISOpen = false;
   }



// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//	1 -  BUSINESS LOGIC
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   // CLEVER OPEN, based on a Property MAP to build the log FILE PATH NAME
   //
   public boolean openDIDSucceed(ConfigManager theCtxt)
    {
	if ( ! logISOpen ) {
	   try {
		Path logPath = Paths.get(theCtxt.getHomeDir()
							, theCtxt.get("Log_Location", "Documents")
							   , theCtxt.get("Log_FileName", "Logging at ")
										+ Instant.now().toString().replace(":", "_") + ".log");
		logWriter = new PrintWriter(logPath.toFile(), "ISO-8859-1");
		logISOpen = true;

	   } catch (FileNotFoundException | UnsupportedEncodingException err) {
		logWriter = null;
		lastErrorMessage = "Error opening the Logger: " + err.getMessage();
		return false;
	   }
	}  // else ignore this redundant Open request...
	return true;
   }


   // STANDARD OPEN, based on the given log FILE PATH NAME
   //
   public boolean openDIDSucceed(String theLogPathName)
    {
	if ( ! logISOpen ) {
	   try {
		Path logPath = Paths.get(theLogPathName);
		logWriter = new PrintWriter(logPath.toFile(), "ISO-8859-1");
		logISOpen = true;

	   } catch (FileNotFoundException | UnsupportedEncodingException err) {
		logWriter = null;
		lastErrorMessage = "Error opening the Log File: " + err.getMessage();
		return false;
	   }
	}  // else ignore this redundant Open request...
	return true;
   }


   // Close the Logger
   //
   public void close()
    {
	if ( logISOpen ) {
	   if (logWriter != null) {
		logWriter.println();
		logWriter.println("_____________");
		logWriter.println("Log closed at: " + Instant.now().toString().replace(":", "_") + ".log");
		logWriter.close(); }
	   logWriter = null;
	   logISOpen = false;
	}
   }


   // Return the LAST ERROR Message
   public String getLastErrorMessage ()
    {
	return lastErrorMessage;
   }


   // Most basic write function ; return silently if the Log is not open...
   //
   public synchronized void add (String logLine)
    {
	if ( logISOpen ) {
	   logWriter.println(logLine);
	}
   }

   // Basic "empty" linefeed (s)
   //
   public synchronized void addNL(int count)
    {
	if ( logISOpen ) {
	   for ( int i = 0 ; i < count ; i++ ) {
		logWriter.println();
	   }
	}
   }

   // Flush to disk
   public void writeDisk()
    {
	if ( logISOpen ) {
	   logWriter.flush();
	}
   }
}


// Short sample of a config.xml file
//==========================

/*
-------------------------------------------------------------------------------------------------

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">

<!--	Configuration Properties for the Application JavGripDemo
	...
-->
<properties>
   <!-- Entries relative to Logging -->
	<entry key="Log_Location">JavGrip\ZZZLOGS</entry>
	<entry key="Log_FileName">Run on </entry>

   <!-- Entries relative to ... -->

</properties>

-------------------------------------------------------------------------------------------------
*/