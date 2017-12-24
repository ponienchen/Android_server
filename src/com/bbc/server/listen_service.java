package com.bbc.server;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class listen_service {

	
	 public static final String PREF = "IP_PORT";
	 public static final String PREF_IP = "IP_ADDRESS";
	 public static final String PREF_PORT = "PORT_NO";
	 public static final String PREF_MSG = "MSG";
	 public static final String PREF_CLIFD = "clifd";
	 
	 private final Handler mHandler_2;
	 private final Context context;
	 private myThread_2 myT_2;
	 
	 
	
	 public listen_service(Context context, Handler handler)
	 {
		 this.context = context;
		 mHandler_2 = handler; 
	 }
	 
	
	 
	public synchronized void start()
	{
		myT_2 = new myThread_2();
		myT_2.start();
			
	}
	 

	
	 //get received message
	 public class myThread_2 extends Thread{
		
	
				
			public myThread_2()
			{
				
			}
			
			

			 public void run()
			 {
				 
				 String received_msg;
				 SharedPreferences settings = context.getSharedPreferences(PREF, 0);
				 int clifd = settings.getInt(PREF_CLIFD, 0);
				 
				 
				 while(true)
				 {
					
					 //Log.d("server listen service", "clifd = " + String.valueOf(clifd));
					 
					 received_msg = Linuxc.recv(clifd);
					 
					 //Log.d("server listen service", "received message: " + received_msg);
					 
					 if (received_msg.equals("(server side) failed to receive"))
						 continue;
					 
					 mHandler_2.obtainMessage(server.MESSAGE_RECEIVED, 0, 0, received_msg).sendToTarget();
					
					 
				 }
				 	
			 }
	 }
	
	

	
	
}
