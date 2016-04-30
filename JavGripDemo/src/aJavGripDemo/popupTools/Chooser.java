/*
*  Created by MIMO on April 28th, 2016
*
*  CONTEXT :   Easing access to JAVAFX development
*  PURPOSE :   Enable using a little more appealing Application window equipment than the basic Stage
*  ROLE :	LIBRARY class proposing a GENERIC POPUP Widget with minimal features, to be EXTENDED by specific ones.
*
*	Technical Notes : Use this CHOOSER for info, alert or lethal messages (show one single confirmation button) AND for selction message (show a second button)
*/

package popupTools;


import java.net.URL;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public class Chooser extends GenPop
 {
   //   Private Resources - All inherited... except
   private final Image warningPictBits, lethalPictBits, askingPictBits;
   private short pictType = 0;
   private final URL warningIconLocation = getClass().getResource("Graphics/warning.png");
   private final URL lethalIconLocation = getClass().getResource("Graphics/lethal.png");
   private final URL askingIconLocation = getClass().getResource("Graphics/asking.png");




// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						0 -  OVERALL  INITIALIZATION & TERMINATION
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   // Construction
   public Chooser(final Stage parentStage)
    {
	super ( parentStage );

	// finalize Popup layout
	warningPictBits = new Image(warningIconLocation.toExternalForm());
	lethalPictBits = new Image(lethalIconLocation.toExternalForm());
	askingPictBits = new Image(askingIconLocation.toExternalForm());
   }



// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						1 - BUSINESS LOGIC
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   // Swiss Knife : Depending on the GOAL, will pop the requested Dialog...
   //
   public int pausedPost ( PopupGoals theGoal, String theMessage )
    {
	switch ( theGoal ) {
	   case pg_INFORM : {
		if ( pictType != 0 ) {
		   resetPictoTo(infoPictBits);
		   resetHintTo("Please, confirm...");
		   pictType = 0;
		}
		postNotice(theMessage, "Thanks !");
		break;
	   }
	   case pg_ALERT : {
		if ( pictType != 1 ) {
		   resetPictoTo(warningPictBits);
		   resetHintTo("Please, confirm...");
		   pictType = 1;
		}
		postNotice(theMessage, "Noted !");
		break;
	   }
	   case pg_LETHAL : {
		if ( pictType != 2 ) {
		   resetPictoTo(lethalPictBits);
		   resetHintTo("Please, confirm...");
		   pictType = 2;
		}
		postNotice(theMessage, "RIP...");
		break;
	   }
	   case pg_SETTLE : {
		if ( pictType != 3 ) {
		   resetPictoTo(askingPictBits);
		   resetHintTo("Please, select an option...");
		   pictType = 3;
		}
		postChoice(theMessage, "No ?", "Yes ?");
		break;
	   }
	}
	return answerGiven;
   }


   // Ad Hoc methods...
   // This one is called from the main stage Controller to OPEN the basic INFORM Popup with the Message, and a standard text to put in the button
   public int postInfo(String theMessage)
    {
	if ( pictType != 0 ) {
	   resetPictoTo(infoPictBits);
	   resetHintTo("Please, confirm...");
	   pictType = 0;
	}
	postNotice(theMessage, "Thanks !");
	return answerGiven;
   }

   // This one is called from the main stage Controller to OPEN the ALERT Popup with the Message, and a standard text to put in the button
   public int postAlert(String theMessage)
    {
	if ( pictType != 1 ) {
	   resetPictoTo(warningPictBits);
	   resetHintTo("Please, confirm...");
	   pictType = 1;
	}
	postNotice(theMessage, "Noted !");
	return answerGiven;
   }

   // This one is called from the main stage Controller to OPEN the LETHAL Popup with the Message, and a standard text to put in the button
   public int postLethal(String theMessage)
    {
	if ( pictType != 2 ) {
	   resetPictoTo(lethalPictBits);
	   resetHintTo("Please, confirm...");
	   pictType = 2;
	}
	postNotice(theMessage, "RIP...");
	return answerGiven;
   }

   // This one is called from the main stage Controller to OPEN the CHOICE Popup with the Message only (using default button texts)
   public int postAskingChoice(String theMessage)
    {
	if ( pictType != 3 ) {
	   resetPictoTo(askingPictBits);
	   resetHintTo("Please, select an option...");
	   pictType = 3;
	}
	postChoice(theMessage, "No ?", "Yes ?");
	return answerGiven;
   }

   // This one is called from the main stage Controller to OPEN the CHOICE Popup with the Message, and the text to put in the button
   public int postAskingChoice(String theMessage, String theYesText, String theNoText)
    {
	if ( pictType != 3 ) {
	   resetPictoTo(askingPictBits);
	   resetHintTo("Please, select an option...");
	   pictType = 3;
	}
	postChoice(theMessage, theNoText, theYesText);
	return answerGiven;
   }

   // This one may be fully customized... if needed
   public int postAskingSpecial(String theHint, Image thePict, String theMessage, String theOKText, String theNOText)
    {
	resetHintTo(theHint);
	resetPictoTo(thePict);
	pictType = -1;
	postChoice(theMessage, theOKText, theNOText);
	return answerGiven;
   }

// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						2 - UTILITIES
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   // Inherited, except...
}
