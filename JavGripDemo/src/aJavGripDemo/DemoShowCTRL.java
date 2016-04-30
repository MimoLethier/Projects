/*
*  Created by MIMO on April 28th, 2016
*
*  CONTEXT :   Easing access to JAVAFX development
*  PURPOSE :  Provide a Demo and initial template for building a JAVAFX Application with a few basic tools (scene frame decoration, log, popup, config)
*  ROLE :	CONTROLER Class, managing the users interactions with the Business Logic.
*/


package aJavGripDemo;

import contextTools.ConfigManager;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import logTools.BasicLogger;
import popupTools.Chooser;
import static popupTools.PopupGoals.*;



public class DemoShowCTRL implements Initializable
 {
   //   Private Resources
   private static ConfigManager configClerc;
   private static BasicLogger logClerc;

   //   GUI Resources - Declare here the .fxml objects ( using their fx:id ) which you will explicitely refer to in your code ; the loader will instantiate them.
   private Stage mainStage;
   @FXML private  TabPane laScene;
   @FXML private  Label headLine1, headLine2, footLine;
   @FXML private  Tab firstTAB, secondTAB;

   //   MessageBox Popup...
   private Chooser notifier;



// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//			MAINPROGRAMSECTION  0 -  OVERALL  INITIALIZATION & TERMINATION
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
   // Global INITIALIZATION
   //   1. Preparing for Application "static" bases - THIS HAPPENS BEFORE THE GUI IS READY !!!
   //
   @Override
   public void initialize( URL url, ResourceBundle rb )
    {
	// 0. Instantiate the Configuration context and Log services
	configClerc = new ConfigManager();
	logClerc = new BasicLogger();

	// 2. Initialize here Application-specific items... which do NOT depend on GUI objects
	// ...
   }



   // Global INITIALIZATION - GET ALIVE - This happens after the GUI is ready
   //  2. Receives from the Main (nnnGUI) an handle to the Notifier Popup and get the real business alive
   //
   public void getAlive(Chooser theAcknowledger, Stage theStage)
    {
	// 1. Get access to the Notification GUI AND FileChooser (Stage may possibly not been used here... ; just ine case, thus)
	notifier = theAcknowledger;
	mainStage = theStage;

	// 2. Retrieve the Application Context "config file" and load the info
	if ( ! configClerc.loadDIDSucceed("JavGripDemoShow") ) {
	   lethalPost("Fatal Error opening the CONTEXT file:\n... Aborting this task !");
	   // No LOG opened yet : don't use CleanQuit !
	   Platform.exit();
	   System.exit(0);
	}

	// 3. Open the Log File
	if ( ! logClerc.openDIDSucceed(configClerc) ) {
	   if (logClerc != null) { logClerc.close();   }
	   lethalPost("Fatal Error opening the Log file:\n... Aborting this task !");
	   Platform.exit();
	   System.exit(0);
	}

	logIt("JAVGrip DEMO Session launched on: " + Instant.now().toString());
	logClerc.addNL(1);	// ... or logIt("");

	// Other initializations...
	headLine1.setText("Hello !!! WELCOME to the Demo !");


	logIt("   >>> Initialization completed!");
	logIt("");
   }



   // Preparing for Application CLOSE
   //
   public void cleanQuit()
    {
	logIt("");
	logIt("99-Preparing to Exit on: " + Instant.now().toString());

	// CLOSE hereafter outstanding resources (Files, Tables, Connections...)


	// Closing the Log file itself
	logIt("");
	logIt("Now closing the LOG file : See You later...");
	if (logClerc != null) { logClerc.close(); }

	// Shuting the application down
	Platform.exit();
	System.exit(0);
   }

   // A simple Guard protecting CleanQuit against EXIT requests detrimental to the Application logic state
   //
   public void onExitAsked ()
    {
	if ( questionPost("Really willing to Exit this application ?") == notifier.noAnswer ) {
	   return;
	}
	informPost("Ok - Exiting now...");
	cleanQuit();
   }





// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//			MAINPROGRAMSECTION  1 - GENERIC EVENTS HANDLING code
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
   // Receive TABPANE mouse clicks to setup the just selected TAB...
   //
   @FXML
   private void onTabSelection( MouseEvent event )
    {
	if ( laScene.getSelectionModel().getSelectedItem() == firstTAB) {
	   headLine2.setText("Entering the FIRST Tab...");
	   return;
	}

	if ( laScene.getSelectionModel().getSelectedItem() == secondTAB) {
	   headLine2.setText("Entering the Second Tab...");
	   return;
	}
   }





// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//			MAINPROGRAMSECTION  2 -  IMPLEMENTING THE BACKGROUND FUNCTIONS
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww






// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//			MAINPROGRAMSECTION  3 - GENERIC UTILITIES code
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
   // Logger utility
   //
   private void logIt(String theMessage)
    {
	logClerc.add(theMessage);
   }

   private void logFlush()
    {
	logClerc.writeDisk();
   }


   //  A utility to display just one message without pause
   //
   private void postIt(String theMessage)
    {
	footLine.setText(theMessage);
   }

   //  Utilities to display a warning with pause
   //
   private void informPost(String theMessage)
    {
	notifier.pausedPost(pg_INFORM, theMessage);
   }

   private void alertPost(String theMessage)
    {
	notifier.pausedPost(pg_ALERT, theMessage);
   }

   private void lethalPost(String theMessage)
    {
	notifier.pausedPost(pg_LETHAL, theMessage);
   }

   private int questionPost(String theMessage)
    {
	return notifier.pausedPost(pg_SETTLE, theMessage);
   }

}
