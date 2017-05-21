// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************
//
// This class provides the functions to extract information from UML Sequence Diagram.
// For Software to generate CCSL/Verilog Observers code from MARTE Model.
//
// @package		OBSERVER
// @class		InteractionProcess
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
public class InteractionProcess extends UMLSwitch<Boolean> {
	
	// Important Variables
	private BehaviorExecutionSpecification stateA;
	private MessageOccurrenceSpecification MsgEnd;
	private Boolean duplicateStateA, duplicateMsgEnd;
	private String strSQ, strA;
	private String as,af,e,clk;
	private Stereotype steSQ, steA, steStartA, steFinishA, steClk, steMsg, steConstraint;
	private Event evtStartA, evtFinishA, evtClk;
	private Constraint constraint;
	private Behavior bhvA;
	
	private Lifeline lifeline1, lifeline2;
	private Message msg;
	private MessageEnd limit1;
	private ExecutionOccurrenceSpecification limit2;
	private DurationConstraint duration1;
	private Duration durationMin, durationMax;
	
	private enum PatternEvent {Timed,Untimed};
	PatternEvent patternType;
	private String patternKind;
	private int min,max;
	
	public String msgError;


	// ************************************************************************************************
	public Boolean caseInteraction(Interaction SQ) {
		
		System.out.println("\n-------------------------------------------------------------------------");
		System.out.println("\ncase Observer Interaction: ");
		
		
		// ------------------------------------------------------------------------------------------------
		// Requirements: Check Elements + Stereotypes
		// GaAnalysisContext
		// ------------------------------------------------------------------------------------------------
		
		if(SQ.getAppliedStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext")==null) {
			propertyError("Error: GaAnalysisContext Stereotype Not Applied.");
			return false;
		}
		else {
			System.out.println("\nGaAnalysisContext Applied");
			strSQ=SQ.getName();
			steSQ=SQ.getAppliedStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext");
		}

		// Get Lifelines
		if(SQ.getLifelines().size() != 2) {
			propertyError("Error in Interaction Lifelines.");
			return false;
		}
		else {
			lifeline1 = SQ.getLifelines().get(0);
			lifeline2 = SQ.getLifelines().get(1);
		}

		// Message e
		
		if(SQ.getMessages().size() != 1) {
			propertyError("Error in Interaction Message.");
			return false;
		}
		else {
			msg = SQ.getMessages().get(0);
			e = msg.getName();
			limit1 = msg.getReceiveEvent();
		}

		
		// ------------------------------------------------------------------------------------------------
		// Observation Profile
		// ------------------------------------------------------------------------------------------------
		
		// Pattern Stereotype
		if((msg.getAppliedStereotype("MODEVES::UntimedEventPattern")==null) &&
		   (msg.getAppliedStereotype("MODEVES::TimedEventPattern")==null)){
			propertyError("Error in Timed/Untimed Pattern Stereotype.");
			return false;
		}
		else if((msg.getAppliedStereotype("MODEVES::UntimedEventPattern")!=null) &&
				(msg.getAppliedStereotype("MODEVES::TimedEventPattern")!=null)){ 
			propertyError("Error in Timed/Untimed Pattern Stereotype.");
			return false;
		}
		
		// Untimed Pattern
		else if(msg.getAppliedStereotype("MODEVES::UntimedEventPattern")!=null){
			steMsg=msg.getAppliedStereotype("MODEVES::UntimedEventPattern");
			patternType=PatternEvent.Untimed;
			patternKind=(String)( ( (EnumerationLiteral)msg.getValue(steMsg, "kind") ).getName() );

			if(lifeline2.getCoveredBys().size() < 4) {
				propertyError("Error in Interaction Message or Behavior Execution Specification Structure.");
				return false;
			}
			else {
				duplicateStateA = false; duplicateMsgEnd = false;
				for(InteractionFragment os: lifeline2.getCoveredBys()) doSwitch(os);
			}
			
			if(MsgEnd == null || stateA == null) {
				propertyError("Error in Interaction Message or Behavior Execution Specification Structure.");
				return false;
			}
			else if((lifeline2.getCoveredBys().indexOf(MsgEnd) <= lifeline2.getCoveredBys().indexOf(stateA.getStart())) ||
					(lifeline2.getCoveredBys().indexOf(MsgEnd) >= lifeline2.getCoveredBys().indexOf(stateA.getFinish()))) {
				propertyError("Error in Interaction Message or Behavior Structure.");
				return false;
			}
			
			for(Constraint c: SQ.getOwnedRules())
				if(c instanceof DurationConstraint) {
					propertyError("Error regarding Duration Constraint.");
					return false;
				}
			
			System.out.println("\nUntimed Event Pattern Applied");
			System.out.println("\nType: "+patternType);
			System.out.println("\nKind: "+patternKind);
		
		}
		
		// Timed Pattern
		else if(msg.getAppliedStereotype("MODEVES::TimedEventPattern")!=null){
			steMsg=msg.getAppliedStereotype("MODEVES::TimedEventPattern");
			patternType=PatternEvent.Timed;
			patternKind=(String)( ( (EnumerationLiteral)msg.getValue(steMsg, "kind") ).getName() );
			evtClk=(Event)msg.getValue(steMsg, "on");
			clk=evtClk.getName();
			
			if(evtClk.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null){
				propertyError("Error in Timed Pattern Stereotype.");
				return false;
			} 
			else {
				steClk = evtClk.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Present.");
			}
			
			System.out.println("\nTimed Event Pattern Applied");
			System.out.println("\nType: "+patternType);
			System.out.println("\nKind: "+patternKind);
			System.out.println("\nOn: "+clk);
			
			
			// Duration
			Boolean duplicate = false;
			for(Constraint c: SQ.getOwnedRules()) {
				if(c instanceof DurationConstraint) {
					if((c.getConstrainedElements().get(0)==limit1)||
					   (c.getConstrainedElements().get(1)==limit1)){
						if(duplicate==true) {
							propertyError("Error: Multiple Duration Constraints.");
							return false;
						}
						else {
							duplicate = true;
							duration1 = (DurationConstraint) c;
							durationMin = (Duration) ((DurationInterval)duration1.getSpecification()).getMin();
							min = ((LiteralInteger) durationMin.getExpr()).getValue();
							durationMax = (Duration) ((DurationInterval)duration1.getSpecification()).getMax();
							max = ((LiteralInteger) durationMax.getExpr()).getValue();
							System.out.println("\nmin: "+min);
							System.out.println("\nmax: "+max);
						}
					}
				}
			}
			
	
			// State A
			if(duration1.getConstrainedElements().get(0)==limit1)
				limit2 = (ExecutionOccurrenceSpecification) duration1.getConstrainedElements().get(1);
			else if (duration1.getConstrainedElements().get(1)==limit1)
				limit2 = (ExecutionOccurrenceSpecification) duration1.getConstrainedElements().get(0);
			
			stateA = (BehaviorExecutionSpecification) limit2.getExecution();
			strA = stateA.getName();
		}
		
		
		// ------------------------------------------------------------------------------------------------
		// MARTE Profile
		// ------------------------------------------------------------------------------------------------

		// Behavior A
		if(stateA==null){
			propertyError("Error in Execution Specification.");
			return false;
		} 
		else if(stateA.getBehavior()==null){
			propertyError("Error in Execution Specification Behavior.");
			return false;
		} 
		else {
			bhvA = stateA.getBehavior();
			System.out.println("\nBehavior Present.");
		}		
		
		// TimedProcessing A
		if(bhvA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing")==null) {
			propertyError("Error: TimedProcessing Stereotype Not Applied.");
			return false;
		} 
		else {
			steA = bhvA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			System.out.println("\nTimedProcessing Present.");
		}

		// Tagged Value Start A
		if(bhvA.getValue(steA, "start")==null){
			propertyError("Error in TimedProcessing Stereotype.");
			return false;
		} 
		else {
			evtStartA = (Event) stateA.getBehavior().getValue(steA, "start");
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
		// Property Constraint
		// ------------------------------------------------------------------------------------------------		

		// Constraint
		if(SQ.getOwnedRules().size()==1){
			if(!(SQ.getOwnedRules().get(0) instanceof DurationConstraint)){
				System.out.println("\nConstraint Present");
				constraint = SQ.getOwnedRules().get(0);
			}
			else {
				propertyError("Error in Property Constraint.");
				return false;
			}
		}
		else if(SQ.getOwnedRules().size()==2){
			if(!(SQ.getOwnedRules().get(0) instanceof DurationConstraint)){
				System.out.println("\nConstraint Present");
				constraint = SQ.getOwnedRules().get(0);
			}
			else if(!(SQ.getOwnedRules().get(1) instanceof DurationConstraint)){
				System.out.println("\nConstraint Present");
				constraint = SQ.getOwnedRules().get(1);
			}
			else {
				propertyError("Error in Property Constraint.");
				return false;
			}
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
		
		System.out.println("\n-------------------------------------------------------------------------");
		
		
		// ------------------------------------------------------------------------------------------------
		// ------------------------------------------------------------------------------------------------
		// Generate the Observer Code
		// ------------------------------------------------------------------------------------------------
		
		EventObserver EO = new EventObserver();
		
		if(patternType==PatternEvent.Timed){
			switch(patternKind){
			case "Triggers":
				EO.generateTimedTriggers(strA, as, af, e, clk, min, max);
				break;
			case "Terminates":
				EO.generateTimedTerminates(strA, as, af, e, clk, min, max);
				break;
			default:
				propertyError("Error: Invalid Timed Pattern");
			}
		}
		else if(patternType==PatternEvent.Untimed){
			switch(patternKind){
			case "Triggers":
				EO.generateTriggers(strA, as, af, e);
				break;
			case "Terminates":
				EO.generateTerminates(strA, as, af, e);
				break;
			case "Forbids":
				EO.generateForbids(strA, as, af, e);
				break;
			case "Contains":
				EO.generateContains(strA, as, af, e);
				break;
			default:
				propertyError("Error: Invalid Untimed Pattern");
			}			
		}
		else propertyError("Error: Invalid Pattern Type");
		

		// ------------------------------------------------------------------------------------------------
		
		EO.pw1.close();
		EO.pw2.close();
		EO.pw3.close();
		
		return true;
	}	
		

	//************************************************************************************************
	public Boolean caseBehaviorExecutionSpecification(BehaviorExecutionSpecification bes) {
		if(duplicateStateA == true) propertyError("Error: Duplicate Behavior Execution Specification");
		else {
			duplicateStateA = true;
			stateA = bes;
			strA = bes.getName();		
		}
		return true;
	}
	
	
	// ************************************************************************************************
	public Boolean caseMessageOccurrenceSpecification(MessageOccurrenceSpecification mos) {
		if(duplicateMsgEnd == true) propertyError("Error: Duplicate Message Occurrence Specification");
		else {			
			duplicateMsgEnd = true;
			MsgEnd = mos;
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

