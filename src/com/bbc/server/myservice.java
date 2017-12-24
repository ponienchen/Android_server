package com.bbc.server;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.bbc.server.server;




public class myservice{
	
	 public static final String PREF = "IP_PORT";
	 public static final String PREF_IP = "IP_ADDRESS";
	 public static final String PREF_PORT = "PORT_NO";
	 public static final String PREF_MSG = "MSG";
	 public static final String PREF_CLIFD = "clifd";
	 
	 private static String msg;
	 private final Handler mHandler;
	 private final Context context;
	 private myThread_1 myT_1;
	
	 
	 public myservice(Context context, Handler handler)
	 {
		 this.context = context;
		 mHandler = handler; 
	 }
	 
	 
	
	public synchronized void start()
	{
		myT_1 = new myThread_1();
		myT_1.start();
			
	}
	
	
	
	 //get the clifd
	 public class myThread_1 extends Thread{
		
		 public myThread_1()
		 {
			 
		 }
		 
		 
		 public void run(){
		 
			 Log.e("myservice", "START");
				try{
					SharedPreferences settings = context.getSharedPreferences(PREF, 0);
					String ip = settings.getString(PREF_IP, "");
					String port = settings.getString(PREF_PORT, "");
					msg = Linuxc.conn(ip, Integer.parseInt(port));
					
					//msg = "hello 22";
					Log.d("INFO", msg);
					
					/*
			        settings.edit()
					.putString(PREF_MSG, msg)
					.commit();
			        */
			        
			        mHandler.obtainMessage(server.MESSAGE_AND_CLIFD, 0, 0, msg).sendToTarget();
				
			        Log.e("myservice", "last line");
				}
				catch(NullPointerException e)
				{
					Log.e("myservice", "Oh Oh~");
					e.printStackTrace();
				}
				
				Log.e("myservice", "END");
			 
		 }
		 
		 
	 }
	 

	
	
	 
			
}
	
	

