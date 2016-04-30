/*
*  Created by MIMO on April 28th, 2016
*
*  CONTEXT :   Easing access to JAVAFX development
*  PURPOSE :  Provide a Demo and initial template for building a JAVAFX Application with a few basic tools (scene frame decoration, log, popup, config)
*  ROLE :	MAIN Class, rendering and installing the GUI context.
*/


package aJavGripDemo;


import java.net.URL;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import popupTools.Chooser;
import sceneTools.StyledJuniorSceneFramer;



public class DemoShowMAIN extends Application
 {
   //   Private Resources
   private Pane mainDecor;
   private Stage mainStage;
   private FXMLLoader mainLoader;
   private DemoShowCTRL mainController;
   private StyledJuniorSceneFramer sceneFramer;
   private Chooser notifier;
   private final URL decorScript = getClass().getResource("DemoShowGUI.fxml");
   private final URL styleScript = getClass().getResource("DemoShowSTYLE.css");
   private double stageLaunchX = 400, stageLaunchY = 100;



   @Override
   public void start( final Stage primaryStage ) throws Exception
    {
	mainStage = primaryStage;

	// Load the USER APPLICATION SCENE CONTENT
	mainLoader = new FXMLLoader(decorScript);
	mainDecor = (AnchorPane) mainLoader.load();
	mainController = mainLoader.getController();

	// Decorate the STAGE and SCENE FRAME
	mainStage.setX(stageLaunchX);
	mainStage.setY(stageLaunchY);
	sceneFramer = new StyledJuniorSceneFramer(mainStage, mainDecor);
	// Following decorations are optional...
	sceneFramer.allowNamePlate("JAVGrip DEMO SHOW");
	sceneFramer.allowIconifyability();
	// Take care of EXIT requests (never fired if the Exit request is not allowed, of course)
	sceneFramer.allowExitRequest();
	sceneFramer.exitTrigger.setOnAction( (ActionEvent evt) -> { mainController.onExitAsked(); });

	// Get the resulting DECORATED SCENE and apply the CSS styles
	Scene scene = sceneFramer.getDecoratedScene();
	scene.getStylesheets().add(styleScript.toExternalForm());

	// Let's the Show start...
	mainStage.setScene(scene);
	mainStage.show();

	//   Popup notifier creation
	notifier = new Chooser(mainStage);

	// Rendez-vous with the real business...
	mainController.getAlive(notifier, mainStage);
   }


   // mainStage accessor for other modules, if needed
   public Stage getHomeStage()
    {
	return mainStage;
   }


   // Legacy...
   public static void main( String[] args )
    {
	launch(args);
   }

}
