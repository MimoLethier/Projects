/*
*  Created by MIMO on April 28th, 2016
*
*  CONTEXT :   Easing access to JAVAFX development
*  PURPOSE :   Enable using a little more featured Application Context...
*  ROLE :	LIBRARY class proposing the Apps CONTEXT retieval tools, using PROPERTIES map
*
*	Technical Notes : Inspect the <User.Home>\AppData\LocalLow\FXApp\<AppIdentifier> folder in search of a "config.xml" text file
*	   This file must contains only Key-Value STRING pairs on separate lines.
*	   For instance, the user will save the configuration info for his/her application Barfoo in <User.Home>\AppData\LocalLow\FXApp\Barfoo\config.xml
*	   See a short config file sample (commented) herunder.
*/


package contextTools;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;



public class ConfigManager
 {
   //   Private Resources
   private final Properties appCtxtMap;
   private String osName, osType, userHomeDir, dirSep, lineSep, appCtxtDir;
   private boolean ctxtISLoaded;
   private String lastErrorMessage;




//wwwwwwwwwwwwwwwwwwwwwwwwwww
//		MAINPROGRAMSECTION  0 -  CONSTRUCTION, INITIALIZATION, and TERMINATION
//wwwwwwwwwwwwwwwwwwwwwwwwwww

   // CONSTRUCTION
   public ConfigManager ()
    {
	appCtxtMap = new Properties();
	ctxtISLoaded = false;
	lastErrorMessage = "";
   }



//wwwwwwwwwwwwwwwwwwwwwwwwwww
//		MAINPROGRAMSECTION  1 - CORE BUSINESS : USER SERVICES, and INTERACTIONS HANDLING
//wwwwwwwwwwwwwwwwwwwwwwwwwww

   // Retrieve the Properties related to the current User (user.home) and the given Application (theAppName) ; if appName == null, do NOT load any file, however
   public boolean loadDIDSucceed (String theAppName)
    {
	// FIRST, retrieve the GENERIC SYSTEM environment, if possible...
	osName = System.getProperty("os.name").toLowerCase();
	dirSep = System.getProperty("file.separator");
	if ( osName == null || dirSep == null || dirSep.length() != 1) {
	   lastErrorMessage = "OS Name or Separator NOT found; Unable to proceed.";
	   return false;
	}

	userHomeDir = System.getProperty("user.home");
	if ( userHomeDir == null ) {
	   lastErrorMessage = "User Home Directory NOT found; Unable to proceed.";
	   return false;
	}
	if ( !osName.contains("win") ) {
	   if ( !osName.contains("nux") ) {
		lastErrorMessage = "This OS is NOT supported; Unable to proceed.";
		return false;
	   } else {
		osType = "Linux";
		appCtxtDir = userHomeDir + dirSep
					+ ".MimoApps" + dirSep
					+ theAppName;
	   }
	} else {
	   osType = "Windows";
	   appCtxtDir = userHomeDir + dirSep
				   + "AppData" + dirSep
				   + "Roaming" + dirSep
				   + "MimoApps" + dirSep
				   + theAppName;
	}
	lineSep = System.getProperty("line.separator");

	// NEXT, look for NULL Application Identity, which means there is NO specific Property File - this is a pre-emptive choice
	if ( theAppName == null ) {
	   ctxtISLoaded = true;
	   return true;
	}

	// FINALLY, try to LOAD the Application-specific properties
	try (FileInputStream prefsFile = new FileInputStream(appCtxtDir + dirSep + "config.xml")) {
		appCtxtMap.loadFromXML(prefsFile);
		ctxtISLoaded = true;
		return true;

	} catch (IOException err) {
	   // If the Property file is NOT found, insert an "Limited-config" - Yes property, and proceed, leaving the error message, but not blocking the App
	   lastErrorMessage = "Error opening sysContext.xml file: " + err.getMessage();
	   appCtxtMap.put("ConfigFileFound", "NO");
	   return true;
	}
   }


   // Return the LAST ERROR Message
   public String getLastErrorMessage ()
    {
	return lastErrorMessage;
   }

   // Return load state
   public boolean isLoaded()
    {
	return ctxtISLoaded;
   }


   // Get another XML Property file, and adds its content to the current Context, if any
   //
   public boolean loadMoreDIDSucceed ( String theXMLFilePathName )
    {
	if ( ! ctxtISLoaded ) {
	   lastErrorMessage = "There is NO Context Property loaded yet! Unable to add more...";
	   return false;
	}

	try (FileInputStream prefsFile = new FileInputStream(theXMLFilePathName)) {
	   appCtxtMap.loadFromXML(prefsFile);
	   return true;

	} catch (IOException err) {
	   lastErrorMessage = "Error adding another .xml Property file: " + err.getMessage();
	   return false;
	}
   }


   // Add a key-value pair to the current Context, if any
   //
   public boolean loadThisOneDIDSucceed ( String theKey, String theValue )
    {
	if ( theKey == null || theValue == null ) {
	   lastErrorMessage = "Neither the Key nor the Value can be null! Unable to add this property...";
	   return false;
	}
	if ( appCtxtMap.containsKey(theKey) ) {
	   lastErrorMessage = "<?> KEY already used !";
	   return false;
	}
	// Add it...
	appCtxtMap.put(theKey, theValue);
	return true;
   }


   // Return value of usual properties : Those may be retrieved EVEN WHEN THE PROPERTY MAP is NOT (yet) loaded !!!
   //	   The Applications is responsible to NOT call those functions IF the load did throw an exception !
   public String getHomeDir ()
    {
	return userHomeDir;
   }

   public String getOSType ()
    {
	return osType;
   }

   public String getDirSep ()
    {
	return dirSep;
   }

   public String getEOLSep ()
    {
	return lineSep;
   }


   // Return the value associated with a specific KEY ; if the Map is not loaded, orif the Key is NOT found, return the given Default
   public String get (String theKey, String theDefault)
    {
	if ( appCtxtMap.containsKey(theKey) ) {
	   return appCtxtMap.getProperty(theKey);
	}
	return theDefault;
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