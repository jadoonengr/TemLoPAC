// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************
//
// This class provides the functions to extract information from UML StateMachine Diagram.
// For Software to generate CCSL/Verilog Observers code from MARTE Model.
//
// @package		OBSERVER
// @class		StatemachineProcess
// @author  	Aamir M. Khan
// @version 	3.1
// @first		2016-02-02
// @current		2016-08-13 
//
// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************

package observer;


import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.*;
import org.eclipse.uml2.uml.util.UMLSwitch;


//************************************************************************************************
public class StatemachineProcess extends UMLSwitch<Boolean> {
	
	// Important Variables	
	private State stateA, stateB;
	private OpaqueBehavior bhvA, bhvB;
	private String strSM, strA, strB;
	private String as,af,bs,bf,clk;
	private Stereotype steSM, steA, steStartA, steFinishA, steB, steStartB, steFinishB, steClk, steTransition, steConstraint;
	private Event evtStartA, evtFinishA, evtStartB, evtFinishB, evtClk;
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
		System.out.println("\ncaseStateMachine");	
		
		StateObserver SO = new StateObserver();		
		
		// ------------------------------------------------------------------------------------------------
		// Requirements: Check Elements + Stereotypes
		// GaAnalysisContext
		// ------------------------------------------------------------------------------------------------	

		if(SM.getAppliedStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext")==null) {
			propertyError("Error: GaAnalysisContext Stereotype Not Applied.");
			return false;
		}
		else {
			System.out.println("\nGaAnalysisContext Present");
			strSM=SM.getName();
			steSM=SM.getAppliedStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext");
		}
		
		
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
		else if(stateA.getDoActivity()==null) {
			propertyError("Error in Activation State Behavior.");
			return false;
		} 
		else {
			bhvA = (OpaqueBehavior) stateA.getDoActivity();
			System.out.println("\nBehavior A Present.");
		}
		
		// TimedProcessing A
		if(bhvA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing")==null){
			propertyError("Error: TimedProcessing Stereotype Not Applied.");
			return false;
		} 
		else {
			steA = bhvA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			System.out.println("\nTimedProcessing A Present.");
		}

		// Tagged Value Start A
		if(bhvA.getValue(steA, "start")==null){
			propertyError("Error in TimedProcessing Stereotype.");
			return false;
		} 
		else {
			evtStartA = (Event) bhvA.getValue(steA, "start");
			as = evtStartA.getName();
			if(evtStartA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) { 
				propertyError("Error in Clock Stereotype.");
				return false;
			} 
			else {
				steStartA = evtStartA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Start A Present.");
			}		
		}
		
		// Tagged Value Finish A
		if(bhvA.getValue(steA, "finish")==null){
			propertyError("Error in TimedProcessing Stereotype.");
			return false;
		} 
		else {
			evtFinishA = (Event) bhvA.getValue(steA, "finish");
			af = evtFinishA.getName();
			if(evtFinishA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) {
				propertyError("Error in Clock Stereotype.");
				return false;
			}
			else {
				steFinishA = evtFinishA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Finish A Present.");
			}
		}	
		
		// Tagged Value On: Start A, Finish A
		if(((EList<?>)bhvA.getValue(steA, "on")).size() == 0){
			propertyError("Error in TimedProcessing Stereotype.");
			return false;
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
			System.out.println("\nTransition Pattern Applied");
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
			clk=evtClk.getName();
			
			if(evtClk.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null){
				propertyError("Error in Timed Pattern Stereotype.");
				return false;
			} else {
				steClk = evtClk.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Present.");
			}
			
			System.out.println("\nTimed State Pattern Applied");
			System.out.println("\nType: "+patternType);
			System.out.println("\nKind: "+patternKind);
			System.out.println("\nMin: "+min);
			System.out.println("\nMax: "+max);
			System.out.println("\nOn: "+clk);
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

		// Behavior B
		if(stateB.getDoActivity()==null){
			propertyError("Error in State Behavior.");
			return false;
		} 
		else {
			bhvB = (OpaqueBehavior) stateB.getDoActivity();
			System.out.println("\nBehavior B Present.");
		}
		
		// TimedProcessing
		if(bhvB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing")==null){
			propertyError("Error: TimedProcessing Stereotype Not Applied.");
			return false;
		} 
		else {
			steB = bhvB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			System.out.println("\nTimedProcessing B Present.");
		}

		// Tagged Value Start B
		if(bhvB.getValue(steB, "start")==null){
			propertyError("Error in TimedProcessing Stereotype.");
			return false;
		} 
		else {
			evtStartB = (Event) bhvB.getValue(steB, "start");
			bs = evtStartB.getName();
			if(evtStartB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) { 
				propertyError("Error in Clock Stereotype.");
				return false;
			} 
			else {
				steStartB = evtStartB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Start B Present.");
			}		
		}
		
		// Tagged Value Finish B
		if(bhvB.getValue(steB, "finish")==null){
			propertyError("Error in TimedProcessing Stereotype.");
			return false;
		} 
		else {
			evtFinishB = (Event) bhvB.getValue(steB, "finish");
			bf = evtFinishB.getName();
			if(evtFinishB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) {
				propertyError("Error in Clock Stereotype.");
				return false;
			}
			else {
				steFinishB = evtFinishB.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Finish B Present.");
			}
		}	
		
		// Tagged Value On: Start B, Finish B
		if(((EList<?>)bhvB.getValue(steB, "on")).size() == 0){
			propertyError("Error in TimedProcessing Stereotype.");
			return false;
		}
		

		// ------------------------------------------------------------------------------------------------		
		// Property Constraint
		// ------------------------------------------------------------------------------------------------		

		// Constraint
		if(SM.getOwnedRules().size()==1){
			System.out.println("\nConstraint Present");
			constraint = SM.getOwnedRules().get(0);
		}
		else {
			propertyError("Error in Property Constraint.");
			return false;
		}
		
		
		// CCSL Specification
		if(constraint.getSpecification()==null){
			propertyError("Error in Constraint Specification.");
			return false;
		} 

		// TimedConstraint
		if(constraint.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedConstraint")==null){
			propertyError("Error in Timed Constraint Stereotype.");
			return false;
		} 
		else {
			steConstraint = constraint.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedConstraint");
			System.out.println("\nTimedConstraint Present.");
		}

		
		// ------------------------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------
		// Generate the Observer Code
		// ------------------------------------------------------------------------------------------------

		
		if(patternType==PatternState.Timed){
			switch(patternKind){
			case "Precedes":
				SO.generateTimedPrecedes(strA, strB, as, af, bs, bf, clk, min, max);
				break;
			case "Starts":
				SO.generateTimedStarts(strA, strB, as, af, bs, bf, clk, min, max);
				break;
			case "Finishes":
				SO.generateTimedFinishes(strA, strB, as, af, bs, bf, clk, min, max);
				break;
			default:
				propertyError("Error: Invalid Timed Pattern");
			}
		}
		else if(patternType==PatternState.Untimed){
			switch(patternKind){
			case "Precedes":
				SO.generatePrecedes(strA, strB, as, af, bs, bf);
				break;
			case "Starts":
				SO.generateStarts(strA, strB, as, af, bs, bf);
				break;
			case "Finishes":
				SO.generateFinishes(strA, strB, as, af, bs, bf);
				break;
			case "Causes":
				SO.generateCauses(strA, strB, as, af, bs, bf);
				break;
			case "Contains":
				SO.generateContains(strA, strB, as, af, bs, bf);
				break;			
			case "Implies":
				SO.generateImplies(strA, strB, as, af, bs, bf);
				break;
			case "Forbids":
				SO.generateForbids(strA, strB, as, af, bs, bf);
				break;
			case "Excludes":
				SO.generateExcludes(strA, strB, as, af, bs, bf);
				break;
			default:
				propertyError("Error: Invalid Untimed Pattern");
			}			
		}
		else propertyError("Error: Invalid Pattern Type");
		
		System.out.println("\n-------------------------------------------------------------------------");

		SO.pw1.close();			
		SO.pw2.close();
		SO.pw3.close();
		
		return true;
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
