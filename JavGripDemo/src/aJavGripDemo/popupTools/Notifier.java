/*
*  Created by MIMO on April 28th, 2016
*
*  CONTEXT :   Easing access to JAVAFX development
*  PURPOSE :   Enable using a little more appealing Application window framing than the basic Stage
*  ROLE :	LIBRARY class proposing a GENERIC POPUP Widget with minimal features, to be EXTENDED by specific ones.
*
*	Technical Notes : Use this NOTIFIER for informational, alerting, and lethal messages, with one single confirmation button.
*		Does not use the PopupGoals, but separate methods instead... Use the Chooser for a somewhat more flexible/general dialog...
*/

package popupTools;


import java.net.URL;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Notifier extends GenPop
 {
   //   Private Resources - All inherited... except
   private final Image warningPictBits, lethalPictBits;
   private short pictType = 0;
   private final URL warningIconLocation = getClass().getResource("Graphics/warning.png");
   private final URL lethalIconLocation = getClass().getResource("Graphics/lethal.png");



// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						0 -  OVERALL  INITIALIZATION & TERMINATION
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   // Construction
   public Notifier(final Stage parentStage)
    {
	super ( parentStage );

	// finalizePopup layout
	warningPictBits = new Image(warningIconLocation.toExternalForm());
	lethalPictBits = new Image(lethalIconLocation.toExternalForm());
   }



// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						1 - BUSINESS LOGIC
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   //	Inherited... except

   // This one is called from the main stage Controller to OPEN the Popup with the Message to display, and the text to put in the button
   public int postInfo(String theMessage)
    {
	if ( pictType != 0 ) {
	   resetPictoTo(infoPictBits);
	   pictType = 0;
	}
	postNotice(theMessage, "Thanks !");
	return answerGiven;
   }

   // This one is called from the main stage Controller to OPEN the ALERT Popup with the Message, and the text to put in the button
   public int postAlert(String theMessage)
    {
	if ( pictType != 1 ) {
	   resetPictoTo(warningPictBits);
	   pictType = 1;
	}
	postNotice(theMessage, "Noted !");
	return answerGiven;
   }

   // This one is called from the main stage Controller to OPEN the LAST POST Popup with the Message, and the text to put in the button
   public int postLethal(String theMessage)
    {
	if ( pictType != 2 ) {
	   resetPictoTo(lethalPictBits);
	   pictType = 2;
	}
	postNotice(theMessage, "RIP...");
	return answerGiven;
   }

   // This one may be fully customized... if needed
   public int postSpecial(String theHint, Image thePict, String theMessage, String theBtnText)
    {
	resetHintTo(theHint);
	resetPictoTo(thePict);
	pictType = -1;
	postNotice(theMessage, theBtnText);
	return answerGiven;
   }

// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						2 - UTILITIES
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   // Inherited... except

}
