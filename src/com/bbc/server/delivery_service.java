package com.bbc.server;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class delivery_service{
	
	
	
	 public static final String PREF = "IP_PORT";
     public static final String PREF_CLIFD = "clifd";
     public static final String PREF_DELIVERY = "msg to deliver";
	
	
 
	 private final Context context;
	 private myThread_1 myT_1;
	
	 
	 public delivery_service(Context context)
	 {
		 this.context = context;
	
	 }
	 
	 
	
	public synchronized void start()
	{
		myT_1 = new myThread_1();
		myT_1.start();
			
	}
	
	
	public class myThread_1 extends Thread{
		
		 public myThread_1()
		 {
			 
		 }
		 
		 
		 public void run(){
		 
			 SharedPreferences settings = context.getSharedPreferences(PREF,0);
				
				int clifd = settings.getInt(PREF_CLIFD, 0);
				if (clifd == 0)
				{
					Log.e("server delivery service","clifd not available");
					return;
				}
				
				
				String msg = settings.getString(PREF_DELIVERY, "");
				
				
				Linuxc.deliver(clifd, msg);
			 
		 }
		 
		 
	 }
   
	
	
}


