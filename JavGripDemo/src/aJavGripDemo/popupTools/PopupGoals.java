/*
*  Created by MIMO on April 28th, 2016
*
*  CONTEXT :   Easing access to JAVAFX development
*  PURPOSE :   Enable using a little more appealing Application window framing than the basic Stage
*  ROLE :	GENERIC POPUP GOALS Enumeration type(Information, Alert (recoverable error), Lethal (unrecoverable error) and Alternative choice)
*
*/

package popupTools;



public enum PopupGoals
 {
   pg_INFORM,	   // For information messages, no severity; just acknowledge button; green icon
   pg_ALERT,	   // For "recoverable" error, medium severity; just acknowledge button; orange icon
   pg_LETHAL,	   // For "unrecoverable" error, high severity; just acknowledge button; red icon
   pg_SETTLE	   // For settling an alternative; two buttons (yes/no, OK/NOK...); blue icon
}
