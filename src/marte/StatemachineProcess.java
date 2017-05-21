// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************
//
// This class provides the functions to extract information from UML StateMachine Diagram.
// For Software to transform UML Model into MARTE Model.
// Applies the MARTE stereotypes as needed. 
// CCSL Specification is also inserted to the Model.
//
// @package		MARTE
// @class		StatemachineProcess
// @author  	Aamir M. Khan
// @version 	3.1
// @first		2016-02-02
// @current		2016-08-13 
//
// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************

package marte;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.*;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.util.UMLSwitch;


public class StatemachineProcess extends UMLSwitch<Boolean> {
	
	// Important Variables	
	private Package pkg;
	private State stateA, stateB;
	private String strSM, strA, strB;
	private Event evtStartA, evtFinishA, evtStartB, evtFinishB, evtClk;
	@SuppressWarnings("unused")
	private Stereotype steClkType, steClk, steA, steStartA, steFinishA, steB, steStartB, steFinishB;
	private Stereotype steSM, steTransition, steConstraint;
	private Transition transition;
	private Constraint constraint;

	private enum PatternState {Timed,Untimed};
	PatternState patternType;
	private String patternKind;
	private int min,max;
	
	public String msgError;


// ************************************************************************************************
	public Boolean caseStateMachine(StateMachine SM) {
		
		System.out.println("\n-------------------------------------------------------------------------");
		System.out.println("\ncase MARTE StateMachine");	

		
		// ------------------------------------------------------------------------------------------------
		// Requirements: Check Elements + Stereotypes
		// GaAnalysisContext
		// ------------------------------------------------------------------------------------------------	

		if(SM.getAppliedStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext")==null) {
			System.out.println("\nGaAnalysisContext Now Applied");
			steSM = SM.getApplicableStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext");
			SM.applyStereotype(steSM);
			
		} else System.out.println("\nGaAnalysisContext Already Applied");
		
		pkg = SM.getPackage();
		strSM=SM.getName();
		steSM=SM.getAppliedStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext");
		
		
		// ------------------------------------------------------------------------------------------------
		// Activation State
		// ------------------------------------------------------------------------------------------------		
		
		if(SM.getRegions().size() != 1) {
			propertyError("Error in State Machine Region.");
			return false;
		}
		else if(SM.getRegions().get(0).getSubvertices().size() != 2) {
			propertyError("Error in State Machine Vertices.");
			return false;
		}
		else {
			for(Vertex vx : SM.getRegions().get(0).getSubvertices()) doSwitch(vx);
		}
		
		// Behavior_A
		if(stateA==null) {
			propertyError("Error in Activation State.");
			return false;
		}
		else if(stateA.getDoActivity()==null){
			System.out.println("\nBehavior A Now Applied");
			stateA.createDoActivity("behavior_"+strA, UMLPackage.eINSTANCE.getOpaqueBehavior());
		} 
		else System.out.println("\nBehavior A Already Present.");
		
		// TimedProcessing A
		if(stateA.getDoActivity().getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing")==null){
			System.out.println("\nTimedProcessing A Now Applied");
			steA = stateA.getDoActivity().getApplicableStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			stateA.getDoActivity().applyStereotype(steA);
		} 
		else {
			steA = stateA.getDoActivity().getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			System.out.println("\nTimedProcessing A Already Present.");
		}
		
		// Tagged Value Start A
		if(stateA.getDoActivity().getValue(steA, "start")==null){
			System.out.println("\nClock A Start Now Applied");
			evtStartA = (Event) pkg.createPackagedElement("start_"+strA, UMLPackage.eINSTANCE.getChangeEvent());
			stateA.getDoActivity().setValue(steA, "start", evtStartA);
			
			steStartA = evtStartA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
			evtStartA.applyStereotype(steStartA);
		} 
		else {
			evtStartA = (Event) stateA.getDoActivity().getValue(steA, "start");
			if(evtStartA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) {
				steStartA = evtStartA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
				evtStartA.applyStereotype(steStartA);
			}
			else {
				steStartA = evtStartA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock A Start Already Present.");		
			}		
		}
		
		// Tagged Value Finish A
		if(stateA.getDoActivity().getValue(steA, "finish")==null){
			System.out.println("\nClock A Finish Now Applied");
			evtFinishA = (Event) pkg.createPackagedElement("finish_"+strA, UMLPackage.eINSTANCE.getChangeEvent());
			stateA.getDoActivity().setValue(steA, "finish", evtFinishA);
			
			steFinishA = evtFinishA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
			evtFinishA.applyStereotype(steFinishA);
		} 
		else {
			evtFinishA = (Event) stateA.getDoActivity().getValue(steA, "finish");
			if(evtFinishA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) {
				steFinishA = evtFinishA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
				evtFinishA.applyStereotype(steFinishA);
			}
			else {
				steFinishA = evtFinishA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock A Finish Already Present.");
			}
		}	
		
		// Tagged Value On: start A, finish A
		if(((EList<?>)stateA.getDoActivity().getValue(steA, "on")).size() == 0){
			System.out.println("\nClock A On Now Applied");
			stateA.getDoActivity().setValue(steA, "on[0]", evtStartA.getStereotypeApplication(steStartA));
			stateA.getDoActivity().setValue(steA, "on[1]", evtFinishA.getStereotypeApplication(steFinishA));
		} else {
			System.out.println("\nClock A On Already Applied");
		}
		
		// ------------------------------------------------------------------------------------------------
		// Transition
		// ------------------------------------------------------------------------------------------------		
		
		if(SM.getRegions().get(0).getTransitions().size() != 1) {
			propertyError("Error in State Transition.");
			return false;
		}
		else if(!SM.getRegions().get(0).getTransitions().get(0).getSource().equals(stateA)) {
			propertyError("Error in State Transition.");
			return false;
		}
		else transition = SM.getRegions().get(0).getTransitions().get(0);
		
		// Pattern Stereotype
		if((transition.getAppliedStereotype("MODEVES::UntimedStatePattern")==null) &&
		   (transition.getAppliedStereotype("MODEVES::TimedStatePattern")==null)) {
			propertyError("Error in Timed/Untimed Pattern Stereotype.");
			return false;
		}
		else if((transition.getAppliedStereotype("MODEVES::UntimedStatePattern")!=null) &&
				(transition.getAppliedStereotype("MODEVES::TimedStatePattern")!=null)) {
			propertyError("Error in Timed/Untimed Pattern Stereotype.");
			return false;
		}
		
		// Untimed Pattern
		else if(transition.getAppliedStereotype("MODEVES::UntimedStatePattern")!=null) {
			steTransition=transition.getAppliedStereotype("MODEVES::UntimedStatePattern");
			patternType=PatternState.Untimed;
			patternKind=(String)( ( (EnumerationLiteral)transition.getValue(steTransition, "kind") ).getName() );
			System.out.println("\nUntimed State Pattern Present");
			System.out.println("\nType: "+patternType);
			System.out.println("\nKind: "+patternKind);
		}
		
		// Timed Pattern
		else if(transition.getAppliedStereotype("MODEVES::TimedStatePattern")!=null){
			steTransition=transition.getAppliedStereotype("MODEVES::TimedStatePattern");
			patternType=PatternState.Timed;
			patternKind=(String)( ( (EnumerationLiteral)transition.getValue(steTransition, "kind") ).getName() );
			min=(Integer)transition.getValue(steTransition, "min"); 
			max=(Integer)transition.getValue(steTransition, "max");
			evtClk=(Event)transition.getValue(steTransition, "on");
			
			if(evtClk.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null){
				steClk = evtClk.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
				evtClk.applyStereotype(steClk);
			} else {
				steClk = evtClk.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Already Present.");
			}
			
			System.out.println("\nTimed State Pattern Present");
			System.out.println("\nType: "+patternType);
			System.out.println("\nKind: "+patternKind);
			System.out.println("\nMin: "+min);
			System.out.println("\nMax: "+max);
			System.out.println("\nOn: "+evtClk.getName());
		}
		
		
		// ------------------------------------------------------------------------------------------------		
		// State B
		// ------------------------------------------------------------------------------------------------		
		
		if(transition.getTarget()==null) {
			propertyError("Error in State Machine Transition or Vertices.");
			return false;
		}
		else {
			stateB=(State)transition.getTarget();
			strB=stateB.getName();
		}

		// Behavior_B
		if(stateB.getDoActivity()==null){
			System.out.println("\nBehavior B Now Applied");
			stateB.createDoActivity("behavior_"+strB, UMLPackage.eINSTANCE.getOpaqueBehavior());
		} 
		else System.out.println("\nBehavior B Already Present.");
		
		// TimedProcessing
		if(stateB.getDoActivity().getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing")==null){
			System.out.println("\nTimedProcessing B Now Applied");
			steB = stateB.getDoActivity().getApplicableStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			stateB.getDoActivity().applyStereotype(steB);
		} 
		else {
			steB = stateB.getDoActivity().getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			System.out.println("\nTimedProcessing B Already Present.");
		}

		// Event Start B
		if(stateB.getDoActivity().getValue(steB, "start")==null){
			System.out.println("\nClock B Start Now Applied");
			evtStartB = (Event) pkg.createPackagedElement("start_"+strB, UMLPackage.eINSTANCE.getChangeEvent());
			stateB.getDoActivity().setValue(steB, "start", evtStartB);
			
			steStartB = evtStartB.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
			evtStartB.applyStereotype(steStartB);
		} 
		else {
			evtStartB = (Event) stateB.getDoActivity().getValue(steB, "start");
			if(evtStartB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) {
				steStartB = evtStartB.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
				evtStartB.applyStereotype(steStartB);
			}
			else {
				steStartB = evtStartB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock B Start Already Present.");
			}
		}

		// Event Finish B
		if(stateB.getDoActivity().getValue(steB, "finish")==null){
			System.out.println("\nClock B Finish Now Applied");
			evtFinishB = (Event) pkg.createPackagedElement("finish_"+strB, UMLPackage.eINSTANCE.getChangeEvent());
			stateB.getDoActivity().setValue(steB, "finish", evtFinishB);
			
			steFinishB = evtFinishB.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
			evtFinishB.applyStereotype(steFinishB);
		} 
		else {
			evtFinishB = (Event) stateB.getDoActivity().getValue(steB, "finish");
			if(evtFinishB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) {
				steFinishB = evtFinishB.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
				evtFinishB.applyStereotype(steFinishB);
			}
			else {
				steFinishB = evtFinishB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock B Finish Already Present.");
			}
		}	
		
		// Tagged Value On: start B, finish B
		if(((EList<?>)stateB.getDoActivity().getValue(steB, "on")).size() == 0) {
			System.out.println("\nClock B On Now Applied");
			stateB.getDoActivity().setValue(steB, "on[0]", evtStartB.getStereotypeApplication(steStartB));
			stateB.getDoActivity().setValue(steB, "on[1]", evtFinishB.getStereotypeApplication(steFinishB));
		} 
		else {
			System.out.println("\nClock B On Already Applied");
		}
		
		
		// ------------------------------------------------------------------------------------------------		
		// Property Constraint
		// ------------------------------------------------------------------------------------------------		

		// Constraint
		if(SM.getOwnedRules().size()==0){
			System.out.println("\nConstraint Now Created");
			constraint = SM.createOwnedRule("constraint_"+strSM, UMLPackage.eINSTANCE.getConstraint());
		} 
		else if(SM.getOwnedRules().size()==1){
			System.out.println("\nConstraint Already Present");
			constraint = SM.getOwnedRules().get(0);
		}
		else {
			propertyError("Error in Property Constraint.");
			return false;
		}
		
		
		// CCSL Specification
		if(constraint.getSpecification()==null){
			System.out.println("\nCCSL Specification Now Created");
			constraint.createSpecification(null,null,UMLPackage.eINSTANCE.getLiteralString());
			constraint.getSpecification().setName("CCSL");
		} 
		else {
			System.out.println("\nCCSL Specification Already Present");
		}
		
		String CCSLSpec = getCCSLSpec();
		((LiteralString)constraint.getSpecification()).setValue(CCSLSpec);
		
		
		// TimedConstraint
		if(constraint.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedConstraint")==null){
			System.out.println("\nTimedConstraint Now Applied");
			steConstraint = constraint.getApplicableStereotype("MARTE::MARTE_Foundations::Time::TimedConstraint");
			constraint.applyStereotype(steConstraint);
		} 
		else {
			steConstraint = constraint.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedConstraint");
			System.out.println("\nTimedConstraint Already Present.");
		}
	
		constraint.setValue(steConstraint, "on[0]", evtStartA.getStereotypeApplication(steStartA));
		constraint.setValue(steConstraint, "on[1]", evtFinishA.getStereotypeApplication(steFinishA));
		constraint.setValue(steConstraint, "on[2]", evtStartB.getStereotypeApplication(steStartB));
		constraint.setValue(steConstraint, "on[3]", evtFinishB.getStereotypeApplication(steFinishB));
		
		if(patternType==PatternState.Timed){
			constraint.setValue(steConstraint, "on[4]", evtClk.getStereotypeApplication(steClk));
		}
		
		System.out.println("\n-------------------------------------------------------------------------");
		
		return true;
	}		
		
		
	// ************************************************************************************************		
	// ************************************************************************************************	
	// Generate the CCSL Specification
	// ************************************************************************************************	
	public String getCCSLSpec() {
		
		String CCSLSpec = null;
	
		if(patternType==PatternState.Timed){
			switch(patternKind){
			case "Precedes":
				CCSLSpec = "af delayedFor min on clk precedes bs,\nbs precedes af delayedFor max on clk,\nas alternatesWith af,\nbs alternatesWith bf";
				break;
			case "Starts":
				CCSLSpec = "as delayedFor min on clk precedes bs,\nbs precedes as delayedFor max on clk,\nas alternatesWith af,\nbs alternatesWith bf";
				break;
			case "Finishes":
				CCSLSpec = "af delayedFor min on clk precedes bf,\nbf precedes af delayedFor max on clk,\nas alternatesWith af,\nbs alternatesWith bf";
				break;
			default:
				propertyError("Error: Invalid Timed Pattern");
			}
		}
		else if(patternType==PatternState.Untimed){
			switch(patternKind){
			case "Precedes":
				CCSLSpec = "af precedes bs,\nas alternatesWith af,\nbs alternatesWith bf";
				break;
			case "Starts":
				CCSLSpec = "Untimed Starts";
				break;
			case "Finishes":
				CCSLSpec = "Untimed Finishes";
				break;
			case "Causes":
				CCSLSpec = "Untimed Causes";
				break;
			case "Excludes":
				CCSLSpec = "bs sampledOn as precedes af,\nas alternatesWith af";
				break;
			case "Forbids":
				CCSLSpec = "af exclusiveWith bs,\nas alternatesWith af,\nbs alternatesWith bf";
				break;
			case "Contains":
				CCSLSpec = "as precedes e_prime,\ne_prime precedes af,\ne isSubsetOf e_prime,\nas alternatesWith af";
				break;
			case "Implies":
				CCSLSpec = "Untimed Implies";
				break;
			default:
				propertyError("Error: Invalid Untimed Pattern");
			}			
		}
		else propertyError("Error: Invalid Pattern Type");
		
		return CCSLSpec;
	}
	
	
	// ************************************************************************************************
	public Boolean caseRegion(Region rg) {
		//System.out.println("caseRegion \n");	
		for(Vertex vertex : rg.getSubvertices()) doSwitch(vertex);
		for(Transition transition : rg.getTransitions()) doSwitch(transition);
		return true;
	}

	
	// ************************************************************************************************
	public Boolean caseState(State st) {
		//System.out.println("caseState \n");	
		if(st.getAppliedStereotype("MODEVES::ActivationState") != null) {
			stateA = st;	
			strA = stateA.getName();
		}		
		return true;
	}
	

	// ************************************************************************************************	
	public void propertyError(String msg){
		System.out.println("\nIncorrect Property: "+msg);
		if(msg==null) msgError = "Error Occurred!";
		else msgError = msg;
	}

}

