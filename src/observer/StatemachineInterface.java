// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************
//
// This class provides the functions for the front-end of StateMachine Diagram.
// For Software to generate CCSL/Verilog Observers code from MARTE Model.
//
// @package		OBSERVER
// @class		StatemachineInterface
// @author  	Aamir M. Khan
// @version 	3.1
// @first		2016-02-02
// @current		2016-08-13 
//
// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************

package observer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;


import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

import org.eclipse.uml2.uml.*;


//************************************************************************************************
public class StatemachineInterface extends AbstractHandler implements IHandler {
	/**
	 * The constructor.
	 */
	//************************************************************************************************
	public StatemachineInterface() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	//************************************************************************************************
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection = PlatformUI.
				getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		Object selected = ((StructuredSelection)selection).getFirstElement();

		// The type should be guaranteed by the "isVisibleWhen"
		assert(selected instanceof StateMachine);
		
		
		StatemachineProcess SM = new StatemachineProcess();
		SM.doSwitch((StateMachine)selected);
		
		
		if(SM.msgError == null) SM.msgError = "CCSL/Verilog Observer Code Successfully Generated!";
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Graphical Property Analysis",
				SM.msgError
				);

		return null;
	}
}
