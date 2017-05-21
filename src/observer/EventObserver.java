// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************
//
// This class provides the CCSL and Verilog Observers generation facility for State-Event Relations
//
// @package		OBSERVER
// @class		EventObserver
// @author  	Aamir M. Khan
// @version 	3.1
// @first		2016-02-02
// @current		2016-08-13 
//
// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************

package observer;


import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

//************************************************************************************************
public class EventObserver {

	// Important Variables
	public PrintWriter pw1,pw2,pw3;
	
	
	// ************************************************************************************************
	// Constructor
	public EventObserver(){
		// create three files: CCSLPlus.txt, CCSL.txt, SystemVerilog.sv
		try {
			System.out.println("\nFile Output Initialization ");		
			pw1 = new PrintWriter( new OutputStreamWriter(
					new FileOutputStream("CCSLPlus.txt"),"UTF-8"));
			pw2 = new PrintWriter( new OutputStreamWriter(
					new FileOutputStream("CCSL.txt"),"UTF-8"));
			pw3 = new PrintWriter( new OutputStreamWriter(
					new FileOutputStream("SystemVerilog.sv"),"UTF-8"));
			
		} catch (Exception e) {	
			pw1.print("ERROR OCCURED"); 
			pw2.print("ERROR OCCURED"); 
			pw3.print("ERROR OCCURED"); 
			}
	}
	

	// ************************************************************************************************
	public void generateTimedTriggers(String strA,String as,String af,String e,String clk,int min,int max) {
		System.out.println("\ngenerateTimedTriggers");
		
		// CCSL+
		// e triggers A after [m,n] on clk
		pw1.print(e);
		pw1.print(" triggers ");
		pw1.print(strA);
		pw1.print(" after [" + min + "," + max + "] on " + clk);

		
		// CCSL
		// e delayedFor min on clk precedes as
		pw2.print(e + " delayedFor " + min + " on " + clk + " precedes " + as);
		pw2.println("");
		
		// as precedes e delayedFor max on clk
		pw2.print(as + " precedes " + e + " delayedFor " + max + " on " + clk);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		
		// SystemVerilog
		// ---------------------------------------
		pw3.println("// @Generated");
		pw3.println("// " + e + " triggers " + strA + " after [" + min + "," + max + "] on " + clk);
		pw3.println("");
		pw3.println("module Triggers (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + e + ",");
		pw3.println("  input " + clk + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  parameter min=" + min + ",max=" + max + ";");
		pw3.println("  ");
		pw3.println("  reg valid;");
		pw3.println("  int unsigned d[max-1];");
		pw3.println("  int unsigned ds;");
		pw3.println("  int unsigned df;");
		pw3.println("  int unsigned i;");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + e + " or " + clk + ")");
		pw3.println("	begin ");
		pw3.println("    case(FSM)");
		pw3.println("      // --------------------------------------------------------");
		pw3.println("		  0: // State A'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0 && " + clk + "==1'b0) FSM=0; // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0 && " + clk + "==1'b1)   // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=0;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<(max-1))");
		pw3.println("		      begin");
		pw3.println("		        FSM=0;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1 && " + clk + "==1'b0)   // " + e);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1 && " + clk + "==1'b1)   // " + e + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + clk + "==1'b0)              // " + as);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--;FSM=1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + clk + "==1'b1)              // " + as + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;valid=1'b1;FSM=1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end		");    
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State A");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0 && " + clk + "==1'b0) FSM=1;  // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0 && " + clk + "==1'b1)    // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=1;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<(max-1))");
		pw3.println("		      begin");
		pw3.println("		        FSM=1;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1 && " + clk + "==1'b0)    // " + e);
		pw3.println("		    begin");
		pw3.println("		      FSM=1;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1 && " + clk + "==1'b1)    // " + e + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      FSM=1;");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + clk + "==1'b0)               // " + af);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + clk + "==1'b1)               // " + af + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end		");    
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default: // Violation");
		pw3.println("      begin");
		pw3.println("        FSM=2;v=1'b1;");
		pw3.println("      end");
		pw3.println("    endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("        ");
		pw3.println("  initial ");
		pw3.println("  begin");
		pw3.println("    valid=1'b1;");
		pw3.println("    ds=0;");
		pw3.println("    df=max-1;");
		pw3.println("    d[max-1]=0;");
		pw3.println("    i=0;");
		pw3.println("    FSM = 0;");
		pw3.println("    v = 0;");
		pw3.println("  end");
		pw3.println("endmodule");
	}

	// ************************************************************************************************
	public void generateTimedTerminates(String strA,String as,String af,String e,String clk,int min,int max) {
		System.out.println("\ngenerateTimedTerminates");
		
		// CCSL+
		// e terminates A after [m,n] on clk
		pw1.print(e);			
		pw1.print(" terminates ");			
		pw1.print(strA);
		pw1.print(" after [" + min + "," + max + "] on " + clk);
	
		
		// CCSL
		// e delayedFor min on clk precedes af
		pw2.print(e + " delayedFor " + min + " on " + clk + " precedes " + af);
		pw2.println("");
		
		// af precedes e delayedFor max on clk
		pw2.print(af + " precedes " + e + " delayedFor " + max + " on " + clk);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");
		
		
		// SystemVerilog
		// ---------------------------------------
		pw3.println("// @Generated");
		pw3.println("// " + e + " terminates " + strA + " after [" + min + "," + max + "] on " + clk);
		pw3.println("");
		pw3.println("module Terminates (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + e + ",");
		pw3.println("  input " + clk + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  parameter min=" + min + ",max=" + max + ";");
		pw3.println("  ");
		pw3.println("  reg valid;");
		pw3.println("  int unsigned d[max-1];");
		pw3.println("  int unsigned ds;");
		pw3.println("  int unsigned df;");
		pw3.println("  int unsigned i;");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + e + " or " + clk + ")");
		pw3.println("	begin ");
		pw3.println("    case(FSM)");
		pw3.println("      // --------------------------------------------------------");
		pw3.println("		  0: // State A'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0 && " + clk + "==1'b0) FSM=0; // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0 && " + clk + "==1'b1)   // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=0;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<(max-1))");
		pw3.println("		      begin");
		pw3.println("		        FSM=0;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1 && " + clk + "==1'b0)   // " + e);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1 && " + clk + "==1'b1)   // " + e + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + clk + "==1'b0)              // " + as);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--;FSM=1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + clk + "==1'b1)              // " + as + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;valid=1'b1;FSM=1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end		");    
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State A");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0 && " + clk + "==1'b0) FSM=1;  // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0 && " + clk + "==1'b1)    // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=1;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<(max-1))");
		pw3.println("		      begin");
		pw3.println("		        FSM=1;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1 && " + clk + "==1'b0)    // " + e);
		pw3.println("		    begin");
		pw3.println("		      FSM=1;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1 && " + clk + "==1'b1)    // " + e + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      FSM=1;");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + clk + "==1'b0)               // " + af);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + clk + "==1'b1)               // " + af + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b0; i++;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end		");    
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default: // Violation");
		pw3.println("      begin");
		pw3.println("        FSM=2;v=1'b1;");
		pw3.println("      end");
		pw3.println("    endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("        ");
		pw3.println("  initial ");
		pw3.println("  begin");
		pw3.println("    valid=1'b1;");
		pw3.println("    ds=0;");
		pw3.println("    df=max-1;");
		pw3.println("    d[max-1]=0;");
		pw3.println("    i=0;");
		pw3.println("    FSM = 0;");
		pw3.println("    v = 0;");
		pw3.println("  end");
		pw3.println("endmodule");
	}


	// ************************************************************************************************
	// ************************************************************************************************
	// ************************************************************************************************
	// ************************************************************************************************
	
	
	// ************************************************************************************************
	public void generateTriggers(String strA,String as,String af,String e) {
		System.out.println("\ngenerateTriggers");
		
		// CCSL+
		// e triggers A
		pw1.print(e);			
		pw1.print(" triggers ");			
		pw1.print(strA);
	
		
		// CCSL
		// e isSubclockOf as
		pw2.print(e + " isSubclockOf " + as);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");
		
		
		// SystemVerilog
		// ---------------------------------------
		pw3.println("// @Generated");
		pw3.println("// " + e + " triggers " + strA);
		pw3.println("");
		pw3.println("module Triggers (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + e + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");	
		pw3.println("  always @ (" + as + " or " + af + " or " + e + ")");
		pw3.println("	begin ");
		pw3.println("    case (FSM)");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  0 : // State " + strA + "'");
		pw3.println("		  begin");
		pw3.println("		    if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0) FSM=0;       // empty");
		pw3.println("		    else if (" + as + "==1'b1 && " + af + "==1'b0 && " + e + "==1'b0) FSM=1;  // " + as);
		pw3.println("		    else if (" + as + "==1'b1 && " + af + "==1'b0 && " + e + "==1'b1) FSM=1;  // " + as + "," + e);
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2; ");
		pw3.println("		      v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1 : // State " + strA);
		pw3.println("		  begin");
		pw3.println("		    if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0) FSM=1;       // empty");
		pw3.println("		    else if (" + as + "==1'b0 && " + af + "==1'b1 && " + e + "==1'b0) FSM=0;  // " + af);
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2; ");
		pw3.println("		      v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default : // Violation");
		pw3.println("		  begin");
		pw3.println("		    FSM=2;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end");
		pw3.println("    endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("  initial ");
		pw3.println("  begin");
		pw3.println("    FSM = 0;");
		pw3.println("    v = 0;");
		pw3.println("  end");
		pw3.println("endmodule");
	}
	
	// ************************************************************************************************
	public void generateTerminates(String strA,String as,String af,String e) {
		System.out.println("\ngenerateTerminates");
		
		// CCSL+
		// e terminates A
		pw1.print(e);
		pw1.print(" terminates ");
		pw1.print(strA);

		
		// CCSL
		// e isSubclockOf af
		pw2.print(e + " isSubclockOf " + af);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		
		// SystemVerilog
		// ---------------------------------------

		pw3.println("// @Generated");
		pw3.println("// " + e + " terminates " + strA);
		pw3.println("");
		pw3.println("module Terminates (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + e + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("");  
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("");
		pw3.println("  always @ (" + as + " or " + af + " or " + e + ")");
		pw3.println("	begin");
		pw3.println("    case (FSM)");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  0 : // State A'");
		pw3.println("		  begin");
		pw3.println("		    if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0) FSM=0;       // empty");
		pw3.println("		    else if (" + as + "==1'b1 && " + af + "==1'b0 && " + e + "==1'b0) FSM=1;  // " + as);
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2; ");
		pw3.println("		      v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1 : // State A");
		pw3.println("		  begin");
		pw3.println("		    if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0) FSM=1;       // empty");
		pw3.println("		    else if (" + as + "==1'b0 && " + af + "==1'b1 && " + e + "==1'b0) FSM=0;  // " + af);
		pw3.println("		    else if (" + as + "==1'b0 && " + af + "==1'b1 && " + e + "==1'b1) FSM=0;  // " + af + "," + e);
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2; ");
		pw3.println("		      v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default : // Violation");
		pw3.println("		  begin");
		pw3.println("		    FSM=2;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end");
		pw3.println("    endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("  initial ");
		pw3.println("  begin");
		pw3.println("    FSM = 0;");
		pw3.println("    v = 0;");
		pw3.println("  end");
		pw3.println("endmodule");

	}
	
	
	// ************************************************************************************************	
	public void generateForbids(String strA,String as,String af,String e) {
		System.out.println("\ngenerateForbids");
		
		// CCSL+
		// e forbids A
		pw1.print(e);
		pw1.print(" forbids ");
		pw1.print(strA);

		
		// CCSL
		// e sampledOn as precedes af
		pw2.print(e + " sampledOn " + as + " precedes " + af);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		
		// SystemVerilog
		// ---------------------------------------

		pw3.println("// @Generated");
		pw3.println("// " + e + " forbids " + strA);
		pw3.println("");
		pw3.println("module Forbids (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + e + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("");  
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("");
		pw3.println("  always @ (" + as + " or " + af + " or " + e + ")");
		pw3.println("	begin");
		pw3.println("    case (FSM)");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  0 : // State A'");
		pw3.println("		  begin");
		pw3.println("		    if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0) FSM=0;       // empty");
		pw3.println("		    else if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1) FSM=0;  // e");
		pw3.println("		    else if (" + as + "==1'b1 && " + af + "==1'b0 && " + e + "==1'b0) FSM=1;  // as");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2; ");
		pw3.println("		      v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1 : // State A");
		pw3.println("		  begin");
		pw3.println("		    if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0) FSM=1;       // empty");
		pw3.println("		    else if (" + as + "==1'b0 && " + af + "==1'b1 && " + e + "==1'b0) FSM=0;  // af");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2; ");
		pw3.println("		      v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default : // Violation");
		pw3.println("		  begin");
		pw3.println("		    FSM=2;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end");
		pw3.println("    endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("  initial ");
		pw3.println("  begin");
		pw3.println("    FSM = 0;");
		pw3.println("    v = 0;");
		pw3.println("  end");
		pw3.println("endmodule");

	}
	
	// ************************************************************************************************
	public void generateContains(String strA,String as,String af,String e) {
		System.out.println("\ngenerateContains");
		
		// CCSL+
		// A contains e
		pw1.print(strA);
		pw1.print(" contains ");
		pw1.print(e);

		
		// CCSL
		// as sampledOn e precedes af
		pw2.print(as + " sampledOn " + e + " precedes " + af);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		
		// SystemVerilog
		// ---------------------------------------

		pw3.println("// @Generated");
		pw3.println("// " + strA + " contains " + e);
		pw3.println("");
		pw3.println("module Contains (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + e + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("");  
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("");
		pw3.println("  always @ (" + as + " or " + af + " or " + e + ")");
		pw3.println("	begin");
		pw3.println("    case (FSM)");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  0 : // State " + strA + "'");
		pw3.println("		  begin");
		pw3.println("		    if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0) FSM=0;       // empty");
		pw3.println("		    else if (" + as + "==1'b1 && " + af + "==1'b0 && " + e + "==1'b0) FSM=1;  // " + as);
		pw3.println("		    else if (" + as + "==1'b1 && " + af + "==1'b0 && " + e + "==1'b1) FSM=1;  // " + as + "," + e);
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2; ");
		pw3.println("		      v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1 : // State " + strA);
		pw3.println("		  begin");
		pw3.println("		    if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b0) FSM=1;       // empty");
		pw3.println("		    else if (" + as + "==1'b0 && " + af + "==1'b0 && " + e + "==1'b1) FSM=1;  // " + e);
		pw3.println("		    else if (" + as + "==1'b0 && " + af + "==1'b1 && " + e + "==1'b0) FSM=0;  // " + af);
		pw3.println("		    else if (" + as + "==1'b0 && " + af + "==1'b1 && " + e + "==1'b1) FSM=0;  // " + af + "," + e);
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=2; ");
		pw3.println("		      v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default : // Violation");
		pw3.println("		  begin");
		pw3.println("		    FSM=2;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end");
		pw3.println("    endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("  initial ");
		pw3.println("  begin");
		pw3.println("    FSM = 0;");
		pw3.println("    v = 0;");
		pw3.println("  end");
		pw3.println("endmodule");

	}
}
