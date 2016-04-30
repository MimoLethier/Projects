/*
*  Created by MIMO on April 28th, 2016
*
*  CONTEXT :   Easing access to JAVAFX development
*  PURPOSE :   Enable using a little more appealing Application window framing than the basic Stage
*  ROLE :	LIBRARY class proposing a GENERIC POPUP Widget with minimal features, to be EXTENDED by specific ones.
*
*	Technical Notes : Use the NOTIFIER (one button) or the CHOOSER (two buttons) extentions, NOT this one...
*		The pain with such Popup is in selectin its DROP POINT: Users expects Popup to follow predictably its Application window. However, since this window
*		may be moved (on multiple screens, possibly) and quite largely outside the screens limits, we must take care of not dropping the Popup
*		outside User's visibility/reach... at the same time not embarking in kind of expert system development around screens geometry...
*		Here, the Popup will closely follow its App, as long as some "confort point" still belongs to a screen area... If not, the Popup will appear near the
*		top left corner of the Primary screen. The logic (!) is concentrated in a few Point2D/Dimension2D objects and a single Method, "computeDropPosition"...
*	Multithreading : This Popup will generate an error if another instance is spawned while another is still showing. In case multiple threads may generate
*		concurrent access to the same Popup, it is convenient to request its launch using Platform.runlater() mechanism, which ensure both serialization and
*		execution on the FX Application Thread...
*/


package popupTools;


import java.net.URL;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class GenPop extends AnchorPane
 {
   //   Private Resources
   protected Image infoPictBits;
   protected Stage mainStage, popupStage;
   private final URL styleScript;
   private final URL informIconLocation = getClass().getResource("Graphics/inform.png");
   private Label hintLBL;
   private TextArea messageAREA;
   private ImageView pictogram;
   private Button yesBTN, noBTN;
   private final double popupWidth = 620.0, popupHeigth = 135.0;
   // This may be used for tunning the screen selection hotspot...
   private final Point2D confortOffset = new Point2D(100.0, 30.0);
   // popupXYOffset gives the translation required to go from the STAGE top-left corner to the POPUP top-left corner in standard circumstances
   private final Point2D popupXYOffset = new Point2D(-20.0, -5.0);
   // popupXYLoozyOffset gives the translation required to go from the STAGE top-left corner to the POPUP key visibility Zone top-left corner in non standard cases
   private final Point2D popupXYLoozyOffset = (new Point2D(50.0, 5.0)).add(popupXYOffset);
   // popupWHLoozyDim gives the minimal visible zone within the popup in non-standard cases  (substract the decoration margins)
   private final Dimension2D popupWHLoozyDim = new Dimension2D(popupWidth - 50 - 10.0, popupHeigth - 5.0 - 5.0);
   private final ObservableList<Screen> screensList;
   private final String defaultHint = "Please, confirm...";
   private String defaultYesText = "YES", defaultNoText = "NO";
   protected int answerGiven;
   private double defaultDropSpot;
   private Point2D popupTLCorner, popupTLLoozyCorner;
   private Rectangle2D screenBounds, loozyPopupZone;


   // Public Resources
   public final static int yesAnswer = 1;
   public final static int noAnswer = -1;



// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						0 -  OVERALL  INITIALIZATION & TERMINATION
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   // Construction
   protected GenPop(final Stage parentStage)
    {
	mainStage = parentStage;

	// Prepare the bases to compute the Popup drop-point (to avoid hidden spots)
	screensList = Screen.getScreens();

	// Create a new Stage, independent from the "main" stage, though OWNED by it
	popupStage = new Stage();
	styleScript = this.getClass().getResource("PopupSTYLE.css");
	if ( styleScript == null ) {
	   System.out.println("Style Not found...");
	   System.exit(-1);
	}
	infoPictBits = new Image(informIconLocation.toExternalForm());

	// Set Popup Stage...
	popupStage.setX(20);
	popupStage.setY(20);
	popupStage.initStyle(StageStyle.UNDECORATED);
	popupStage.initModality(Modality.WINDOW_MODAL);
	popupStage.initOwner(mainStage);

	// generic Popup layout
	setPane();
	constructPictogram();
	constructHintLBL();
	constructMessageAREA();
	constructYesBTN();
	constructNoBTN();
	this.getChildren().addAll(hintLBL, pictogram, messageAREA, yesBTN, noBTN);

	// Set the Scene
	Scene scene = new Scene(this);
	scene.getStylesheets().add(styleScript.toExternalForm());
	this.applyCss();
	this.layout();
	popupStage.setScene(scene);

	// The rest is left to the specific EXTENDed  popup (Chooser, ...)
	   //	additional xxxPictBits, ...
	   //	setHintTo("blah"), ...
   }



// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						1 - BUSINESS LOGIC
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
   // This computes the position to be assigned to this Popup when the default (related to the current MainStage position) will hide too much of it
   //	   A bit tricky and achieving only kind of balance between user confort an development complexity ;-)))
   protected Point2D computeDropPosition ( double theStageX, double theStageY )
    {
	popupTLLoozyCorner = (new Point2D(theStageX, theStageY)).add(popupXYLoozyOffset);

	// Look for the Screen which could be the best target for the Popup dropPoint
	for (Screen aScreen : screensList) {
	   screenBounds = aScreen.getBounds();
	   // Look for the popup Top-Left loozy corner shifted by a customisable confort Offset...
	   if ( screenBounds.contains(popupTLLoozyCorner.add(confortOffset)) ) {
		// Found : Now compute the current minimal (loozy) zone acceptable for the Popup usability
		loozyPopupZone = new Rectangle2D(popupTLLoozyCorner.getX(), popupTLLoozyCorner.getY(),
									   popupWHLoozyDim.getWidth(), popupWHLoozyDim.getHeight());
		// Does the Screen contains this minimal zone ?
		if ( screenBounds.contains(loozyPopupZone) ) {
		   // If Yes, configure the dropPoint as usual, close to the App window Top-Left corner
		   return (new Point2D(theStageX, theStageY)).add(popupXYOffset);
		} else {
		   // If No, drop it near the Screen Top corner  at closest vertical border
		   defaultDropSpot = screenBounds.getMinX() + (screenBounds.getWidth() / 2);
		   if ( theStageX > defaultDropSpot ) {
			return (new Point2D(defaultDropSpot, screenBounds.getMinY()).add(confortOffset));
		   }
		   return (new Point2D(screenBounds.getMinX(), screenBounds.getMinY()).add(confortOffset));
		}
		// OK , done.
	   }
	   // No : then Continue looping
	}
	// Well... Nothing found... Drop near the Primary Screen Top-Left Corner
	return confortOffset;
   }


   // This one is called from the main stage Controller to OPEN the Popup with the Message to display, and the text to put in the two buttons
   public int postChoice(String theMessage, String NOKText, String OKText)
    {
	popupTLCorner = computeDropPosition(mainStage.getX(), mainStage.getY());
	yesBTN.setText(OKText);
	noBTN.setText(NOKText);
	noBTN.setVisible(true);
	messageAREA.setText(theMessage);
	popupStage.setX(popupTLCorner.getX());
	popupStage.setY(popupTLCorner.getY());

	popupStage.showAndWait();

	return answerGiven;
   }

   // This one is called from the main stage Controller to OPEN the Popup with the Message to display, using the current text in the two buttons
   public int postChoice(String theMessage)
    {
	popupTLCorner = computeDropPosition(mainStage.getX(), mainStage.getY());
	noBTN.setVisible(true);
	messageAREA.setText(theMessage);
	popupStage.setX(popupTLCorner.getX());
	popupStage.setY(popupTLCorner.getY());

	popupStage.showAndWait();

	return answerGiven;
   }

   // This one is called from the main stage Controller to OPEN the Popup with the Message to display, and the text to put in the single button
   //
   public int postNotice(String theMessage, String OKText)
    {
	popupTLCorner = computeDropPosition(mainStage.getX(), mainStage.getY());
	yesBTN.setText(OKText);
	noBTN.setVisible(false);
	messageAREA.setText(theMessage);
	popupStage.setX(popupTLCorner.getX());
	popupStage.setY(popupTLCorner.getY());

	popupStage.showAndWait();

	return answerGiven;
   }

   // Change the HINT
   public void resetHintTo (String theText)
    {
	hintLBL.setText(theText);
   }

   // Change the pictogram
   public void resetPictoTo (Image thePicto)
    {
	pictogram.setImage(thePicto);
   }

   // Change the current DefaultYesText to the given value, and modify the button Node string accordingly
   public void resetYesBtnTextToDefault( String value )
    {
	yesBTN.setText(defaultYesText);
   }

   public void resetNoBtnTextToDefault( String value )
    {
	noBTN.setText(defaultNoText);
   }


   // Change the current DefaultYes/NoText to the given value (NOT changing the Btn appearance !)
   public void setDefaultYesText( String value )
    {
	defaultYesText = value;
   }

   public void setDefaultNoText( String value )
    {
	defaultNoText = value;
   }



// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
//						2 - UTILITIES
// wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

   private void setPane ()
    {
	this.setPrefSize(popupWidth, popupHeigth);
	this.setId("popup-widget");
   }

   private void constructPictogram ()
    {
	pictogram = new ImageView();
	pictogram.setLayoutX(10.0);
	pictogram.setLayoutY(10.0);
	pictogram.setFitWidth(50.0);
	pictogram.setFitHeight(50.0);
	pictogram.setMouseTransparent(true);
	pictogram.setImage(infoPictBits);
   }

   private void constructMessageAREA ()
    {
	messageAREA = new TextArea();
	messageAREA.setLayoutX(80.0);
	messageAREA.setLayoutY(10.0);
	messageAREA.setPrefWidth(530.0);
	messageAREA.setPrefHeight(80.0);
	messageAREA.setScrollTop(30.0);
	messageAREA.setWrapText(true);
	messageAREA.setFont(Font.font("Calibri", FontWeight.NORMAL, 14.0));
	messageAREA.setEditable(false);
   }

   private void constructHintLBL ()
    {
	hintLBL = new Label();
	hintLBL.setLayoutX(80.0);
	hintLBL.setLayoutY(110.0);
	hintLBL.setPrefWidth(290.0);
	hintLBL.setStyle("-fx-text-fill: darkslateblue;");
	hintLBL.setTextAlignment(TextAlignment.LEFT);
	hintLBL.setFont(Font.font("Calibri Italic", FontWeight.NORMAL, 14.0));
	hintLBL.setMouseTransparent(true);
	hintLBL.setText(defaultHint);
   }

   private void constructYesBTN ()
    {
	yesBTN = new Button();
	yesBTN.setId("yesButton");
	yesBTN.setLayoutX(505.0);
	yesBTN.setLayoutY(97.0);
	yesBTN.setPrefWidth(100.0);
	yesBTN.setOnAction((ActionEvent event) -> {  answerGiven = yesAnswer;
								   popupStage.hide();
								});
	yesBTN.setText(defaultYesText);
   }

   private void constructNoBTN ()
    {
	noBTN = new Button();
	noBTN.setId("noButton");
	noBTN.setLayoutX(395.0);
	noBTN.setLayoutY(97.0);
	noBTN.setPrefWidth(100.0);
	noBTN.setOnAction((ActionEvent event) ->  {  answerGiven = noAnswer;
								   popupStage.hide();
								});
	noBTN.setText(defaultNoText);
   }
}
