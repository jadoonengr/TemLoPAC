// ************************************************************************************************
// ************************************************************************************************	
// ************************************************************************************************
//
// This class provides the CCSL and Verilog Observers generation facility for State-State Relations
//
// @package		OBSERVER
// @class		StateObserver
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
public class StateObserver {

	// Important Variables
	public PrintWriter pw1,pw2,pw3;	
	
	
	// ************************************************************************************************
	// Constructor
	public StateObserver(){
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
	public void generateTimedPrecedes(String strA,String strB,String as,String af,String bs,String bf,String clk,int min,int max) {
		System.out.println("\ngenerateTimedPrecedes");
				
		// CCSL+
		// A precedes B by [m,n] on clk
		pw1.print(strA);
		pw1.print(" precedes ");	
		pw1.print(strB);
		pw1.print(" by [" + min + "," + max + "] on " + clk);
		
		
		// CCSL
		// af delayedFor min on clk precedes bs
		pw2.print(af + " delayedFor " + min + " on " + clk + " precedes " + bs);
		pw2.println("");
		
		// bs precedes af delayedFor max on clk
		pw2.print(bs + " precedes " + af + " delayedFor " + max + " on " + clk);
		pw2.println("");
		
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);
		
		
		// SystemVerilog
		// ---------------------------------------
		pw3.println("// @Generated");
		pw3.println("// " + strA + " precedes " + strB + " by [" + min + "," + max + "] on " + clk);
		pw3.println("");
		pw3.println("module Precedes (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
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
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + " or " + clk + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("      // --------------------------------------------------------");
		pw3.println("		  0: // State A'B'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=0;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk );
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=0;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=0;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=2; // " + as);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=2;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;FSM=1;valid=1'b1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + as + "," + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;FSM=3;valid=1'b1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end		");    
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State A'B");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=1;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=1;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=1;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=3; // " + as);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=3;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=0; // " + bf);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=0;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=2; // " + as + "," + bf);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + as + "," + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	  ");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State AB'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=2;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=2;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + af);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end ");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=0;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;valid=1'b1;FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=1; // " + af + "," + bs);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	  ");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State AB");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=3;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=3;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=3;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + af);
		pw3.println("		    begin");
		pw3.println("		      FSM=1;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end ");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=1;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=2; // " + bf);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0)        // " + af + "," + bf);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + af + "," + bf + "," + clk);
		pw3.println("        begin");
		pw3.println("          if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=0;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("        end");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
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
	public void generateTimedStarts(String strA,String strB,String as,String af,String bs,String bf,String clk,int min,int max) {
		System.out.println("\ngenerateTimedStarts");
		
		// CCSL+
		// A starts B after [m,n] on clk
		pw1.print(strA);
		pw1.print(" starts ");	
		pw1.print(strB);
		pw1.print(" after [" + min + "," + max + "] on " + clk);
		
		
		// CCSL
		// as delayedFor min on clk precedes bs
		pw2.print(as + " delayedFor " + min + " on " + clk + " precedes " + bs);
		pw2.println("");
		
		// bs precedes as delayedFor max on clk
		pw2.print(bs + " precedes " + as + " delayedFor " + max + " on " + clk);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " starts " + strB + " after [" + min + "," + max + "] on " + clk);
		pw3.println("");
		pw3.println("module Starts (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
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
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + " or " + clk + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("      // --------------------------------------------------------");
		pw3.println("		  0: // State A'B'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=0;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk );
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=0;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=0;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=2; // " + as);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=2;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;FSM=1;valid=1'b1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + as + "," + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;FSM=3;valid=1'b1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end		");    
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State A'B");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=1;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=1;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=1;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=3; // " + as);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=3;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=0; // " + bf);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=0;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=2; // " + as + "," + bf);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + as + "," + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	  ");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State AB'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=2;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=2;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + af);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end ");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=0;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;valid=1'b1;FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=1; // " + af + "," + bs);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	  ");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State AB");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=3;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=3;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=3;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + af);
		pw3.println("		    begin");
		pw3.println("		      FSM=1;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end ");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=1;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=2; // " + bf);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0)        // " + af + "," + bf);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + af + "," + bf + "," + clk);
		pw3.println("        begin");
		pw3.println("          if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=0;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("        end");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
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
	public void generateTimedFinishes(String strA,String strB,String as,String af,String bs,String bf,String clk,int min,int max) {
		System.out.println("\ngenerateTimedFinishes");
		
		// CCSL+
		// A finishes B after [m,n] on clk
		pw1.print(strA);
		pw1.print(" finishes ");	
		pw1.print(strB);
		pw1.print(" after [" + min + "," + max + "] on " + clk);
		
		
		// CCSL
		// af delayedFor min on clk precedes bf
		pw2.print(af + " delayedFor " + min + " on " + clk + " precedes " + bf);
		pw2.println("");
		
		// bf precedes af delayedFor max on clk
		pw2.print(bf + " precedes " + af + " delayedFor " + max + " on " + clk);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);

		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " finishes " + strB + " after [" + min + "," + max + "] on " + clk);
		pw3.println("");
		pw3.println("module Finishes (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
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
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + " or " + clk + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("      // --------------------------------------------------------");
		pw3.println("		  0: // State A'B'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=0;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk );
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=0;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=0;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=2; // " + as);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=2;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;FSM=1;valid=1'b1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + as + "," + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;FSM=3;valid=1'b1;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end		");    
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State A'B");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=1;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=1;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=1;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=3; // " + as);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + as + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=3;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=0; // " + bf);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=0;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=2; // " + as + "," + bf);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + as + "," + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	  ");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State AB'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=2;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=2;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + af);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end ");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=0;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + bs);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min && d[ds]<=max)");
		pw3.println("		      begin");
		pw3.println("		        i--; FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i>0 && d[ds]>=min-1 && d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i--;valid=1'b1;FSM=3;");
		pw3.println("		        ds=(ds+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=1; // " + af + "," + bs);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b1 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + bs + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	  ");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State AB");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0) FSM=3;    // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)      // " + clk);
		pw3.println("		    begin ");
		pw3.println("		      if(i==0) begin FSM=3;valid=1'b1; end");
		pw3.println("		      else if(i>0 && d[ds]<max-1)");
		pw3.println("		      begin");
		pw3.println("		        FSM=3;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b0)        // " + af);
		pw3.println("		    begin");
		pw3.println("		      FSM=1;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end ");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0 && " + clk + "==1'b1)        // " + af + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=1;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0) FSM=2; // " + bf);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + bf + "," + clk);
		pw3.println("		    begin");
		pw3.println("		      if(i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        FSM=2;valid=1'b1;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("		    end	");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b0)        // " + af + "," + bf);
		pw3.println("		    begin");
		pw3.println("		      FSM=0;");
		pw3.println("		      if(valid==1'b1)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		    end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1 && " + clk + "==1'b1)        // " + af + "," + bf + "," + clk);
		pw3.println("        begin");
		pw3.println("          if(valid==1'b0 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        valid=1'b1;FSM=0;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else if(valid==1'b1 && i==0 || d[ds]<max)");
		pw3.println("		      begin");
		pw3.println("		        i++;valid=1'b0;FSM=0;");
		pw3.println("		        d[(df+1) % max]=0;");
		pw3.println("		        df=(df+1) % max;");
		pw3.println("		        for(int unsigned x=ds;x<=ds+i-1;x++) d[x % max]++;");
		pw3.println("		      end");
		pw3.println("		      else");
		pw3.println("		      begin");
		pw3.println("		        FSM=4;v=1'b1;");
		pw3.println("		      end");
		pw3.println("        end");
		pw3.println("		    else ");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
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
	// UNTIMED PATTERNS
	// ************************************************************************************************
	// ************************************************************************************************
	
	
	// ************************************************************************************************
	public void generatePrecedes(String strA,String strB,String as,String af,String bs,String bf) {
		System.out.println("\ngeneratePrecedes");
		
		// CCSL+
		// A precedes B				
		pw1.print(strA);
		pw1.print(" precedes ");	
		pw1.print(strB);
		
		
		// CCSL
		// af precedes bs
		pw2.print(af);
		pw2.print(" precedes ");
		pw2.print(bs);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " precedes " + strB);
		pw3.println("");
		pw3.println("module Precedes (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned delta;");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("      	  // --------------------------------------------------------");
		pw3.println("		  0: // State " + strA + "'" + strB + "'");
		pw3.println("		  begin");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2; // " + as);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0)		 // " + bs);
		pw3.println("		    begin");
		pw3.println("  				if (delta>0) ");
		pw3.println("  				begin");
		pw3.println("    				delta--;");
		pw3.println("    				FSM=1;  ");
		pw3.println("  				end");
		pw3.println("  				else");
		pw3.println("  				begin");
		pw3.println("    				FSM=4;");
		pw3.println("    				v=1'b1;");
		pw3.println("  				end");
		pw3.println("			end");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0)		// " + as + "," + bs);
		pw3.println("		    begin");
		pw3.println("  				if (delta>0) ");
		pw3.println("  				begin");
		pw3.println("    				delta--;");
		pw3.println("    				FSM=3;  ");
		pw3.println("  				end");
		pw3.println("  				else");
		pw3.println("  				begin");
		pw3.println("    				FSM=4;");
		pw3.println("    				v=1'b1;");
		pw3.println("  				end");
		pw3.println("			end");
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State " + strA + "'" + strB);
		pw3.println("		  begin");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=1;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3; // " + as);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + bf);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=2; // " + as + "," + bf);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State " + strA + strB + "'");
		pw3.println("		  begin");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2;		// empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0)		// " + af);
		pw3.println("		    begin");
		pw3.println("    			delta++;");
		pw3.println("    			FSM=0;  ");
		pw3.println("			end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0)		// " + bs);
		pw3.println("		    begin");
		pw3.println("  				if (delta>0) ");
		pw3.println("  				begin");
		pw3.println("    				delta--;");
		pw3.println("    				FSM=3;  ");
		pw3.println("  				end");
		pw3.println("  				else");
		pw3.println("  				begin");
		pw3.println("    				FSM=4;");
		pw3.println("    				v=1'b1;");
		pw3.println("  				end");
		pw3.println("			end");
		pw3.println("			else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=1; // " + af + "," + bs);
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State " + strA + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3;		// empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0)		// " + af);
		pw3.println("		    begin");
		pw3.println("    			delta++;");
		pw3.println("    			FSM=1;  ");
		pw3.println("			end");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=2;// " + bf);   
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1)		// " + af + "," + bf);
		pw3.println("		    begin");
		pw3.println("    			delta++;");
		pw3.println("    			FSM=0;  ");
		pw3.println("			end");		
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // State 4: Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("endmodule");
	}

	
	// ************************************************************************************************
	public void generateStarts(String strA,String strB,String as,String af,String bs,String bf) {
		System.out.println("\ngenerateStarts");
		
		// CCSL+
		// A starts B				
		pw1.print(strA);
		pw1.print(" starts ");	
		pw1.print(strB);
		
		
		// CCSL
		// bs isSubclockOf as
		pw2.print(bs);
		pw2.print(" isSubclockOf ");
		pw2.print(as);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " starts " + strB);
		pw3.println("");
		pw3.println("module Starts (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("      // --------------------------------------------------------");
		pw3.println("		  0: // State " + strA + "'" + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=3; // " + as + "," + bs);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State " + strA + "'" + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=1;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + bf);  
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State " + strA + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0; // " + af);
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State " + strA + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=1; // " + af);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=2; // " + bf);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + af + "," + bf);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("endmodule");
		
	}

	
	// ************************************************************************************************
	public void generateFinishes(String strA,String strB,String as,String af,String bs,String bf) {
		System.out.println("\ngenerateFinishes");
		
		// CCSL+
		// A finishes B				
		pw1.print(strA);
		pw1.print(" finishes ");	
		pw1.print(strB);
		
		
		// CCSL
		// bf isSubclockOf af
		pw2.print(bf);
		pw2.print(" isSubclockOf ");
		pw2.print(af);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " finishes " + strB);
		pw3.println("");
		pw3.println("module Finishes (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("      // --------------------------------------------------------");
		pw3.println("		  0: // State " + strA + "'" + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2; // " + as); 
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=1; // " + bs); 
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=3; // " + as + "," + bs);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State " + strA + "'" + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=1;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3; // " + as);  
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State " + strA + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=3; // " + bs);
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State " + strA + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + af + "," + bf);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("endmodule");
	}

	
	// ************************************************************************************************
	public void generateCauses(String strA,String strB,String as,String af,String bs,String bf) {
		System.out.println("\ngenerateCauses");
		
		// CCSL+
		// A causes B				
		pw1.print(strA);
		pw1.print(" causes ");	
		pw1.print(strB);
		
		
		// CCSL
		// bs isSubclockOf af
		pw2.print(bs);
		pw2.print(" isSubclockOf ");
		pw2.print(af);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " causes " + strB);
		pw3.println("");
		pw3.println("module Causes (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("         // --------------------------------------------------------");
		pw3.println("		  0: // State " + strA + "'" + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2; // " + as);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State " + strA + "'" + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=1;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3; // " + as);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + bf); 
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=2; // " + as + "," + bf);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State " + strA + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=1; // " + af + "," + bs); 
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State " + strA + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=2; // " + bf);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("endmodule");
	}

	
	// ************************************************************************************************
	public void generateContains(String strA,String strB,String as,String af,String bs,String bf) {
		System.out.println("\ngenerateContains");
		
		// CCSL+
		// A contains B				
		pw1.print(strA);
		pw1.print(" contains ");	
		pw1.print(strB);
		
		
		// CCSL
		// as precedes bsTemp
		pw2.print(as);
		pw2.print(" precedes ");
		pw2.print("bsTemp");
		pw2.println("");
		
		// bfTemp precedes af
		pw2.print("bfTemp");
		pw2.print(" precedes ");
		pw2.print(af);
		pw2.println("");
				
		// bs isSubclockOf bsTemp
		pw2.print(bs);
		pw2.print(" isSubclockOf ");
		pw2.print("bsTemp");
		pw2.println("");

		// bf isSubclockOf bfTemp
		pw2.print(bf);
		pw2.print(" isSubclockOf ");
		pw2.print("bfTemp");
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);

		// bsTemp alternatesWith bfTemp
		pw2.print("bsTemp alternatesWith bfTemp");
		
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " contains " + strB);
		pw3.println("");
		pw3.println("module Contains (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("      	  // --------------------------------------------------------");
		pw3.println("		  0: // State " + strA + "'" + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2; // " + as);   
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=3; // " + as + "," + bs); 
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State " + strA + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0; // " + af); 
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=3; // " + bs); 
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State " + strA + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=2; // " + bf);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + af + "," + bf);    
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("endmodule");
	}
	
	
	// ************************************************************************************************
	public void generateImplies(String strA,String strB,String as,String af,String bs,String bf) {	
		System.out.println("\ngenerateImplies");
				
		// CCSL+
		// A implies B				
		pw1.print(strA);
		pw1.print(" implies ");	
		pw1.print(strB);
		
		
		// CCSL
		// bs isSubclockOf as
		pw2.print(bs);
		pw2.print(" isSubclockOf ");
		pw2.print("as");
		pw2.println("");

		// bf isSubclockOf af
		pw2.print(bf);
		pw2.print(" isSubclockOf ");
		pw2.print("af");
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);
	
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " implies " + strB);
		pw3.println("");
		pw3.println("module Implies (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("      	  // --------------------------------------------------------");
		pw3.println("		  0: // State " + strA + "'" + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0;    	 // empty");  
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=3; // " + as + "," + bs); 
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State " + strA + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + af + "," + bf);    
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("endmodule");
	}	
	
	
	// ************************************************************************************************
	public void generateForbids(String strA,String strB,String as,String af,String bs,String bf) {
		System.out.println("\ngenerateForbids");
		
		// CCSL+
		// A forbids B
		pw1.print(strA);
		pw1.print(" forbids ");	
		pw1.print(strB);
		
		
		// CCSL
		// af sampledOn bs precedes bf
		pw2.print(af);
		pw2.print(" sampledOn ");
		pw2.print(bs);
		pw2.print(" precedes ");
		pw2.print(bf);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		pw2.println("");

		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " forbids " + strB);
		pw3.println("");
		pw3.println("module Forbids (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("         // --------------------------------------------------------");
		pw3.println("		  0: // State " + strA + "'" + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2; // " + as);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=1; // " + bs);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=3; // " + as + "," + bs);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State " + strA + "'" + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=1;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3; // " + as);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + bf);
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=2; // " + as + "," + bf);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State " + strA + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0; // " + af);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=3; // " + bs);
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  3: // State " + strA + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=3;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=2; // " + bf);   
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("endmodule");
		
	}
	
	
	// ************************************************************************************************
	public void generateExcludes(String strA,String strB,String as,String af,String bs,String bf) {
		System.out.println("\ngenerateExcludes");
		
		// CCSL+
		// A excludes B
		pw1.print(strA);
		pw1.print(" excludes ");	
		pw1.print(strB);
		
		
		// CCSL
		// bs sampledOn as precedes af
		pw2.print(bs);
		pw2.print(" sampledOn ");
		pw2.print(as);
		pw2.print(" precedes ");	
		pw2.print(af);
		pw2.println("");

		// as sampledOn bs precedes bf
		pw2.print(as);
		pw2.print(" sampledOn ");
		pw2.print(bs);
		pw2.print(" precedes ");	
		pw2.print(bf);
		pw2.println("");
		
		// as alternatesWith af
		pw2.print(as);
		pw2.print(" alternatesWith ");	
		pw2.print(af);	
		
		// bs alternatesWith bf
		pw2.print(bs);
		pw2.print(" alternatesWith ");	
		pw2.print(bf);	
		
		
		// SystemVerilog
		pw3.println("// @Generated");
		pw3.println("// " + strA + " excludes " + strB);
		pw3.println("");
		pw3.println("module Excludes (");
		pw3.println("  input " + as + ",");
		pw3.println("  input " + af + ",");
		pw3.println("  input " + bs + ",");
		pw3.println("  input " + bf + ",");
		pw3.println("  output violation");
		pw3.println("  );");
		pw3.println("  ");
		pw3.println("  int unsigned FSM;");
		pw3.println("  reg v;");
		pw3.println("  ");
		pw3.println("  always @ (" + as + " or " + af + " or " + bs + " or " + bf + ")");
		pw3.println("  begin ");
		pw3.println("    case(FSM)");
		pw3.println("         // --------------------------------------------------------");
		pw3.println("		  0: // State " + strA + "'" + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b1 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=1; // " + as);
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b1 && " + bf + "==1'b0) FSM=2; // " + bs);  
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  1: // State " + strA + "'" + strB);
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=1;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b1 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=0; // " + af);  
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");  
		pw3.println("		  2: // State " + strA + strB + "'");
		pw3.println("		    if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b0) FSM=2;    	 // empty");
		pw3.println("		    else if(" + as + "==1'b0 && " + af + "==1'b0 && " + bs + "==1'b0 && " + bf + "==1'b1) FSM=0; // " + bf);
		pw3.println("		    else // Violation");
		pw3.println("		    begin");
		pw3.println("		      FSM=4;v=1'b1;");
		pw3.println("		    end");
		pw3.println("		  // --------------------------------------------------------");
		pw3.println("		  default:  // Violation ");
		pw3.println("		  begin");
		pw3.println("		    FSM=4;");
		pw3.println("		    v=1'b1;");
		pw3.println("		  end	  ");
		pw3.println("	   endcase");
		pw3.println("	end");
		pw3.println("	");
		pw3.println("  assign violation = v;");
		pw3.println("		    ");
		pw3.println("endmodule");

	}
	
}
