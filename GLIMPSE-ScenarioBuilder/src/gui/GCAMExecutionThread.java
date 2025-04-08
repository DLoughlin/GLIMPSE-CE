/*
* LEGAL NOTICE
* This computer software was prepared by US EPA.
* THE GOVERNMENT MAKES NO WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY
* LIABILITY FOR THE USE OF THIS SOFTWARE. This notice including this
* sentence must appear on any copies of this computer software.
* 
* EXPORT CONTROL
* User agrees that the Software will not be shipped, transferred or
* exported into any country or used in any manner prohibited by the
* United States Export Administration Act or any other applicable
* export laws, restrictions or regulations (collectively the "Export Laws").
* Export of the Software may require some form of license or other
* authority from the U.S. Government, and failure to obtain such
* export control license may result in criminal liability under
* U.S. laws. In addition, if the Software is identified as export controlled
* items under the Export Laws, User represents and warrants that User
* is not a citizen, or otherwise located within, an embargoed nation
* (including without limitation Iran, Syria, Sudan, Cuba, and North Korea)
*     and that User is not otherwise prohibited
* under the Export Laws from receiving the Software.
*
* SUPPORT
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors * from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. Coding contributions have also been made by Aaron 
* Parks and Yadong Xu of ARA through the EPA�s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package gui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import glimpseUtil.StatusChecker;

public class GCAMExecutionThread {
	ExecutorService gCAMExecutor = null;
	ArrayList<Future> jobs =new ArrayList<Future>();
	StatusChecker status=new StatusChecker();
	boolean isCheckingStatus=false;
	int num_done=0;
	
	boolean block = false;
	
	public boolean didNumDoneChange() {
		//System.out.println("In didNumDoneChange");
		boolean rtn_bool=false;
		int local_num_done=0;
		for (int i=0;i<jobs.size();i++) {
			if (jobs.get(i).isDone()) local_num_done++; 
		}
		if (local_num_done==num_done) {
			rtn_bool=false;
		} else {
			rtn_bool=true;
			num_done=local_num_done;
		}
		return rtn_bool;
	}
	public void addRunnableCmdsToExecuteQueue(String[] args) {
		try {
			executeRunnableCmds(args);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startUpExecutorSingle() {
		if (gCAMExecutor == null) {
			gCAMExecutor = Executors.newSingleThreadExecutor();
		} else {
			// This should be an error message
			System.out.println(".");
		}

	}

	public void startUpExecutorMulti() {
		if (gCAMExecutor == null) {
			gCAMExecutor = Executors.newCachedThreadPool();
		} else {
			// This should be an error message
			System.out.println(".");
		}
	}
	
	public void executeRunnables(Runnable[] runnables) { 
		for (int i = 0; i < runnables.length; i++) {
			executeRunnable(runnables[i]);
		}
	}
	
	public void executeRunnable(Runnable runable) {
		System.out.println("Submitting to queue: " + runable.toString());
		jobs.add(gCAMExecutor.submit(runable));
		//if (!isCheckingStatus) { status.start(); isCheckingStatus=true; }
		// shutdown();
	}


	
	public void executeRunnableCmds(String[] args) throws InterruptedException { // args probably should be renamed?
		for (int i = 0; i < args.length; i++) {
			executeRunnableCmd(args[i]);
			// shutdown();
		}
	}
	
	public Future executeRunnableCmd(String arg) {
		RunnableCmd gr = new RunnableCmd();
		gr.setCmd(arg);
		System.out.println("Submitting to queue: " + arg);
		Future f=gCAMExecutor.submit(gr);
		jobs.add(f);
		//if (!isCheckingStatus) { status.start(); isCheckingStatus=true; }
		return f;
	}


	public void executeRunnableCmds(String[] args, String directory) throws InterruptedException {
		for (int i = 0; i < args.length; i++) {
			executeRunnableCmd(args[i],directory);
		}
		// shutdown();
	}
	
	public Future executeRunnableCmd(String arg, String directory) {
		String dir="\""+directory.replaceAll("\"","")+"\"";
		RunnableCmd gr = new RunnableCmd();
		gr.setCmd(arg, directory);
		System.out.println("Submitting to queue: " + arg + " with dir " + directory);
		Future f=gCAMExecutor.submit(gr);
		jobs.add(f);
		if (!isCheckingStatus) { status.start(); isCheckingStatus=true; }
		return f;
	}

	
	public void executeCallableCmd(Callable arg) {
		System.out.println("Submitting callable to queue: " + arg);
		jobs.add(gCAMExecutor.submit(arg));
	}
	

	//shutdown 
	public void shutdown() {
		status.terminate();
		gCAMExecutor.shutdown();
	}

	public void shutdownNow() {
		System.out.println("Attempting to shut down all model threads.");
		status.terminate();
		gCAMExecutor.shutdownNow();
	}

	public boolean isExecuting() {
		boolean is_executing = false;
		boolean is_terminated = false;
		if (gCAMExecutor == null) {
			is_executing = false;
		} else {
			is_executing = !gCAMExecutor.isShutdown();
			is_terminated = !gCAMExecutor.isTerminated();
		}

		System.out.println("Is executing: " + is_executing);
		System.out.println("Is terminated: " + is_terminated);

		return is_executing;
	}

	public String getQueue() {
		return gCAMExecutor.toString();
	}
	
//	public void addRunnableCmdsToExecuteQueue(String[] args) {
//
//		System.out.println("In addRunnableCmdsToExecuteQueue");
//		
//		try {
//			executeRunnableCmds(args);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public void startUpExecutorSingle() {
//		System.out.println("In startUpExecutorSingle");
//		
//		//if (gCAMExecutor == null) {
//			gCAMExecutor = Executors.newSingleThreadExecutor();
//		//} 
//
//	}
//
//	public void startUpExecutorMulti() {
//		System.out.println("in startUpExecutorMulti");
//		if (gCAMExecutor == null) {
//			gCAMExecutor = Executors.newCachedThreadPool();
//		} else {
//			// This should be an error message
//			System.out.println(".");
//		}
//	}
//	
//	public void executeRunnables(Runnable[] runnables) { 
//		System.out.println("IN executeRunnables");
//		for (int i = 0; i < runnables.length; i++) {
//			executeRunnable(runnables[i]);
//		}
//	}
//	
//	public void executeRunnable(Runnable runable) {
//		System.out.println("Submitting to queue in executeRunnable: " + runable.toString());
//		jobs.add(gCAMExecutor.submit(runable));
//		//if (!isCheckingStatus) { status.start(); isCheckingStatus=true; }
//		// shutdown();
//	}
//
//
//	
//	public void executeRunnableCmds(String[] args) throws InterruptedException { // args probably should be renamed?
//		System.out.println("in executeRunnableCmds");
//		//for (int i = 0; i < args.length; i++) {
//			executeRunnableCmd(args);
//			// shutdown();
//		//}
//	}
//	
//	public Future executeRunnableCmd(String[] arg) {
//		System.out.println("In executeRunnableCmd, arg:");
//		for(int i=0;i<arg.length;i++) {
//			System.out.print(arg[i]+ " ");
//		}
//		System.out.println("");
//		RunnableCmd gr = new RunnableCmd();
//		gr.setCmd(arg);
//		System.out.println("Submitting to queue in executeRunnableCmd: " + arg);
//		Future f=gCAMExecutor.submit(gr);
//		jobs.add(f);
//		//if (!isCheckingStatus) { status.start(); isCheckingStatus=true; }
//		//System.out.println("F:"+f.toString());
//		//while(!f.isDone()) {
//		//	System.out.println(LocalDateTime.now()+": F is still going.");
//		//	try {
//				
//		//		Thread.sleep(1000);
//		//	}catch(Exception e) {}
//		//}
//		//try {
//		//	System.out.println("F is done "+f.get());
//		//	
//		//}catch(Exception e) {
//		//	System.out.println("FERR: "+e.toString());
//		//}
//		
//		return f;
//	}
//
//
//	public void executeRunnableCmds(String[] args, String directory) throws InterruptedException {
//		System.out.println("in executeRunnableCmds ");
//		for (int i = 0; i < args.length; i++) {
//			executeRunnableCmd(args[i],directory);
//		}
//		// shutdown();
//	}
//	
//	public Future executeRunnableCmd(String arg, String directory) {
//		System.out.println("In executeRunnableCmd, 2 arg");
//		String dir="\""+directory.replaceAll("\"","")+"\"";
//		RunnableCmd gr = new RunnableCmd();
//		gr.setCmd(arg, directory);
//		System.out.println("Submitting to queue executeRunnableCmd: " + arg + " with dir " + directory);
//		Future f=gCAMExecutor.submit(gr);
//		jobs.add(f);
//		if (!isCheckingStatus) { status.start(); isCheckingStatus=true; }
//		return f;
//	}
//
//	
//	public void executeCallableCmd(Callable arg) {
//		System.out.println("Submitting callable to queue executeCallableCmd: " + arg);
//		jobs.add(gCAMExecutor.submit(arg));
//	}
//	
//
//	//shutdown 
//	public void shutdown() {
//		System.out.println("in shutdown");
//		status.terminate();
//		gCAMExecutor.shutdown();
//	}
//
//	public void shutdownNow() {
//		System.out.println("in shutdownNow");
//		System.out.println("Attempting to shut down all model threads.");
//		status.terminate();
//		gCAMExecutor.shutdownNow();
//	}
//
//	public boolean isExecuting() {
//		System.out.println("in isExecuting");
//		boolean is_executing = false;
//		boolean is_terminated = false;
//		if (gCAMExecutor == null) {
//			is_executing = false;
//		} else {
//			is_executing = !gCAMExecutor.isShutdown();
//			is_terminated = !gCAMExecutor.isTerminated();
//		}
//
//		System.out.println("Is executing: " + is_executing);
//		System.out.println("Is terminated: " + is_terminated);
//
//		return is_executing;
//	}
//
//	public String getQueue() {
//		return gCAMExecutor.toString();
//	}
//	

}
