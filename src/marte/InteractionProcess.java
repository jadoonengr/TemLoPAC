// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************
//
// This class provides the functions to extract information from UML Sequence Diagram.
// For Software to transform UML Model into MARTE Model.
// Applies the MARTE stereotypes as needed. 
// CCSL Specification is also inserted to the Model.
//
// @package		MARTE
// @class		InteractionProcess
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


//************************************************************************************************
public class InteractionProcess extends UMLSwitch<Boolean> {
	
	// Important Variables
	private Package pkg;
	
	private BehaviorExecutionSpecification stateA;
	private MessageOccurrenceSpecification MsgEnd;
	private Boolean duplicateStateA, duplicateMsgEnd;
	private String strSQ, strA;
	private Stereotype steSQ, steA, steStartA, steFinishA, steClk, steMsg, steConstraint;
	private Event evtStartA, evtFinishA, evtClk;
	private Constraint constraint;
	private Behavior bhvA;
	
	@SuppressWarnings("unused")
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
		System.out.println("\ncase MARTE Interaction: ");
		
		
		// ------------------------------------------------------------------------------------------------
		// Requirements: Check Elements + Stereotypes
		// GaAnalysisContext
		// ------------------------------------------------------------------------------------------------	
		
		pkg = SQ.getPackage();
		strSQ=SQ.getName();
		
		if(SQ.getAppliedStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext")==null) {
			System.out.println("\nGaAnalysisContext Now Applied");
			steSQ = SQ.getApplicableStereotype("MARTE::MARTE_AnalysisModel::GQAM::GaAnalysisContext");
			SQ.applyStereotype(steSQ);
		} else {
			System.out.println("\nGaAnalysisContext Already Applied");
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
			limit1 = msg.getReceiveEvent();
		}

		
		// ------------------------------------------------------------------------------------------------
		// Observation Profile
		// ------------------------------------------------------------------------------------------------
		
		// Pattern Stereotype
		if((msg.getAppliedStereotype("MODEVES::UntimedEventPattern")==null) &&
		   (msg.getAppliedStereotype("MODEVES::TimedEventPattern")==null)) {
			propertyError("Error in Timed/Untimed Pattern Stereotype.");
			return false;
		}
		else if((msg.getAppliedStereotype("MODEVES::UntimedEventPattern")!=null) &&
				(msg.getAppliedStereotype("MODEVES::TimedEventPattern")!=null)) { 
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
			
			System.out.println("\nUntimed Event Pattern Present");
			System.out.println("\nType: "+patternType);
			System.out.println("\nKind: "+patternKind);
			
		}
		
		// Timed Pattern
		else if(msg.getAppliedStereotype("MODEVES::TimedEventPattern")!=null){
			steMsg=msg.getAppliedStereotype("MODEVES::TimedEventPattern");
			patternType=PatternEvent.Timed;
			patternKind=(String)( ( (EnumerationLiteral)msg.getValue(steMsg, "kind") ).getName() );
			evtClk=(Event)msg.getValue(steMsg, "on");
			
			if(evtClk.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null){
				steClk = evtClk.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
				evtClk.applyStereotype(steClk);
			} 
			else {
				steClk = evtClk.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Already Present.");
			}
				
			
			System.out.println("\nTimed Event Pattern Present");
			System.out.println("\nType: "+patternType);
			System.out.println("\nKind: "+patternKind);
			System.out.println("\nOn: "+evtClk.getName());
			

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

		// Behavior_A
		if(stateA.getBehavior()==null){
			System.out.println("\nBehavior Now Applied");
			bhvA = (Behavior) pkg.createPackagedElement("behavior_"+strA, UMLPackage.eINSTANCE.getOpaqueBehavior());
			stateA.setBehavior(bhvA);
		} 
		else {
			bhvA = stateA.getBehavior();
			System.out.println("\nBehavior Already Present.");
		}
		
		// TimedProcessing
		if(bhvA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing")==null) {
			System.out.println("\nTimedProcessing Now Applied");
			steA = bhvA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			bhvA.applyStereotype(steA);
		} 
		else {
			steA = bhvA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::TimedProcessing");
			System.out.println("\nTimedProcessing Already Present.");
		}
		
		// Tagged Value Start A
		if(bhvA.getValue(steA, "start")==null){
			System.out.println("\nClock Start A Now Applied");
			evtStartA = (Event) pkg.createPackagedElement("start_"+strA, UMLPackage.eINSTANCE.getChangeEvent());
			bhvA.setValue(steA, "start", evtStartA);
			
			steStartA = evtStartA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
			evtStartA.applyStereotype(steStartA);
		
		} 
		else {
			evtStartA = (Event) stateA.getBehavior().getValue(steA, "start");
			if(evtStartA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) { 
				steStartA = evtStartA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
				evtStartA.applyStereotype(steStartA);
			} 
			else {
				steStartA = evtStartA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Start A Already Present.");
			}		
		}
		
		// Tagged Value Finish A
		if(bhvA.getValue(steA, "finish")==null){
			System.out.println("\nClock Finish A Now Applied");
			evtFinishA = (Event) pkg.createPackagedElement("finish_"+strA, UMLPackage.eINSTANCE.getChangeEvent());
			bhvA.setValue(steA, "finish", evtFinishA);
			
			steFinishA = evtFinishA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
			evtFinishA.applyStereotype(steFinishA);
			
		} 
		else {
			evtFinishA = (Event) bhvA.getValue(steA, "finish");
			if(evtFinishA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock")==null) {
				steFinishA = evtFinishA.getApplicableStereotype("MARTE::MARTE_Foundations::Time::Clock");
				evtFinishA.applyStereotype(steFinishA);
			}
			else {
				steFinishA = evtFinishA.getAppliedStereotype("MARTE::MARTE_Foundations::Time::Clock");
				System.out.println("\nClock Finish A Already Present.");
			}
		}	
		

		// Tagged Value On: Start A, Finish A
		if(((EList<?>)bhvA.getValue(steA, "on")).size() == 0){
			System.out.println("\nClock On Now Applied");
			
			bhvA.setValue(steA, "on[0]", evtStartA.getStereotypeApplication(steStartA));
			bhvA.setValue(steA, "on[1]", evtFinishA.getStereotypeApplication(steFinishA));
		} 
		else {
			System.out.println("\nClock On Already Applied");
		}
		
		// ------------------------------------------------------------------------------------------------		
		// Property Constraint
		// ------------------------------------------------------------------------------------------------		

		// Constraint
		if(SQ.getOwnedRules().size()==0){
			System.out.println("\nConstraint Now Created");
			constraint = SQ.createOwnedRule("constraint_"+strSQ, UMLPackage.eINSTANCE.getConstraint());
		} 
		else if(SQ.getOwnedRules().size()==1){
			if(!(SQ.getOwnedRules().get(0) instanceof DurationConstraint)){
				System.out.println("\nConstraint Already Present");
				constraint = SQ.getOwnedRules().get(0);
			}
			else {
				System.out.println("\nConstraint Now Created");
				constraint = SQ.createOwnedRule("constraint_"+strSQ, UMLPackage.eINSTANCE.getConstraint());
			}
		}
		else if(SQ.getOwnedRules().size()==2){
			if(!(SQ.getOwnedRules().get(0) instanceof DurationConstraint)){
				System.out.println("\nConstraint Already Present");
				constraint = SQ.getOwnedRules().get(0);
			}
			else if(!(SQ.getOwnedRules().get(1) instanceof DurationConstraint)){
				System.out.println("\nConstraint Already Present");
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
		
		if(patternType==PatternEvent.Timed){
			constraint.setValue(steConstraint, "on[2]", evtClk.getStereotypeApplication(steClk));
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
	
		if(patternType==PatternEvent.Timed){
			switch(patternKind){
			case "Triggers":
				CCSLSpec = "e delayedFor min on clk precedes as,\nas precedes e delayedFor max on clk,\nas alternatesWith af";
				break;
			case "Terminates":
				CCSLSpec = "e delayedFor min on clk precedes af,\naf precedes e delayedFor max on clk,\nas alternatesWith af";
				break;
			default:
				propertyError("Error: Invalid Timed Pattern");
			}
		}
		
		else if(patternType==PatternEvent.Untimed){
			switch(patternKind){
			case "Triggers":
				CCSLSpec = "Untimed Triggers";
				break;
			case "Terminates":
				CCSLSpec = "Untimed Terminates";
				break;
			case "Forbids":
				CCSLSpec = "e excludes as,\nas alternatesWith af";
				break;
			case "Contains":
				CCSLSpec = "Untimed Contains";
				break;
			default:
				propertyError("Error: Invalid Untimed Pattern");
			}			
		}
		else propertyError("Error: Invalid Pattern Type");
		
		return CCSLSpec;
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


