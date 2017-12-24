package com.bbc.server;


import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class server extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViews();
        setListeners();
        
        /********this resets the car axes ***********/
        irda_buzzer_fd = -1;
        
        irda_buzzer_fd = Linuxc.opengpp();
        if (irda_buzzer_fd < 0)
        {
        	Toast.makeText(server.this, "(server side) irda_buzzer fd not acquired!", Toast.LENGTH_LONG).show();
        	return ;
        }
        Linuxc.closegpp();
        /********this resets the car axes ***********/
        
      
            
    }
    
    
    
    
    
    int irda_buzzer_fd;
    
    private Handler msg_handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 111)
			{
				//when something is within the range of the infrared
				if (((String)msg.obj).equals("1"))
				{
					//sending the "on" signal to GPP1
					Linuxc.sendSignaltoGPP(1, 1);
					//display.append((String)msg.obj);
					//display.append(" ");
					//display.setSelection(display.getText().length());
					
					
					//stop the car
					Linuxc.sendSignaltoGPP(8, 2);
					Linuxc.sendSignaltoGPP(9, 2);
					Linuxc.sendSignaltoGPP(11, 2);
					Linuxc.sendSignaltoGPP(12, 2);
					
					
					
					//tell the remote system to stop the DEMO!
					SharedPreferences settings = getSharedPreferences(PREF, 0);
					settings.edit()
					.putString(PREF_DELIVERY, "stop_demo")
					.commit();
					
					
					d_service = new delivery_service(server.this);
					d_service.start();
							
					
				} //when nothing is within the range of the infrared
				else if (((String)msg.obj).equals("0"))
				{
					//sending the "off" signal to GPP1
					Linuxc.sendSignaltoGPP(1, 2);
					//display.append((String)msg.obj);
					//display.append(" ");
					//display.setSelection(display.getText().length());
					
					
				}
				
			}
			
			super.handleMessage(msg);
		}
    	
    	
    	
    };
    
    
    Timer timer;
    TimerTask task;
    private TimerTask get_task()
	{
		    return new TimerTask(){
		    	
		    	int received_signal;
		    	SharedPreferences settings = getSharedPreferences(PREF, 0);
		
				@Override
				public void run() {
					
					/*
					if (received_signal == 0)
						received_signal = 1;
					else
						received_signal = 0;
			    	*/
			    	
			    	
					//Receive signal from GPP0
					received_signal = Linuxc.receiveSignal(0);
					//the value of the received signal will be either 0 or 1
					
					Message message = new Message();
					message.what = 111;
					message.obj = String.valueOf(received_signal);
					msg_handler.sendMessage(message);
					
					
					/*
					settings.edit()
					.putString(PREF_DELIVERY, String.valueOf(received_signal))
					.commit();
					
					//Toast.makeText(server.this, "Clifd: " + String.valueOf(settings.getInt(PREF_CLIFD, 0)), Toast.LENGTH_SHORT).show();
					//stopService(new Intent(server.this, delivery_service.class));
					
					
					signal_delivery = new delivery_service(server.this);
				
					signal_delivery.start();
					*/
				
				}
		    
		    };
    
	}    
    
    private delivery_service signal_delivery;
    
    
    
    
   

	    
    public static final int MESSAGE_AND_CLIFD = 2;
    public static final int MESSAGE_RECEIVED = 3;
    
    EditText display;
    EditText ip_addr;
    EditText portno;
    Button connect;
    Button disconnect;
    EditText msg_to_deliver;
    Button deliver;
    
    private void findViews()
    {
    	display = (EditText) findViewById(R.id.received_text);
    	ip_addr = (EditText) findViewById(R.id.ip_addr);
    	portno = (EditText) findViewById(R.id.portno);
    	connect = (Button) findViewById(R.id.connect);
    	disconnect = (Button)findViewById(R.id.disconnect);
    	msg_to_deliver = (EditText) findViewById(R.id.msg_to_deliver);
    	deliver = (Button) findViewById(R.id.deliver);
    }
    
    private void setListeners()
    {
    	connect.setOnClickListener(connect_handler);
    	disconnect.setOnClickListener(dis_handler);
    	deliver.setOnClickListener(deliver_handler);
    	
    }
    
    private delivery_service d_service;
    
    private Button.OnClickListener deliver_handler = new Button.OnClickListener(){

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			SharedPreferences settings = getSharedPreferences(PREF, 0);
			
			settings.edit()
			.putString(PREF_DELIVERY, msg_to_deliver.getText().toString())
			.commit();
			
			//Toast.makeText(server.this, "Clifd: " + String.valueOf(settings.getInt(PREF_CLIFD, 0)), Toast.LENGTH_SHORT).show();
			//stopService(new Intent(server.this, delivery_service.class));
			
			
			d_service = new delivery_service(server.this);
		
			d_service.start();
			
			msg_to_deliver.setText("");
			
		}

		
    
    };
    
    private myservice mService;
    
    private Button.OnClickListener connect_handler = new Button.OnClickListener(){

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			irda_buzzer_fd = -1;
	        
	        irda_buzzer_fd = Linuxc.opengpp();
	        if (irda_buzzer_fd < 0)
	        {
	        	Toast.makeText(server.this, "(server side) irda_buzzer fd not acquired!", Toast.LENGTH_LONG).show();
	        	return ;
	        }
			
			
			
			if (ip_addr.getText().length() == 0 || portno.getText().length() == 0)
			{
				Toast.makeText(server.this, "Invalid IP or port number", Toast.LENGTH_SHORT).show();
				return;
			}

			SharedPreferences settings = getSharedPreferences(PREF, 0);
			int clifd_check = settings.getInt(PREF_CLIFD, 0);
			if (clifd_check != 0)
			{
				Toast.makeText(server.this, "clifd already acquired!", Toast.LENGTH_SHORT).show();
		
				return;
			}
			

			
			display.setText("pending connection...");
			
			
			settings.edit()
			.putString(PREF_IP, ip_addr.getText().toString())
			.putString(PREF_PORT, portno.getText().toString())
			.commit();
			
		
			
			mService = new myservice(server.this, mHandler);
			mService.start();
			
			
			L_service = new listen_service(server.this, mHandler_1);
			
			
			
			
			
		}

		
    	
    };
    
    private listen_service L_service;
    
    //used first, and only once, for retrieving the client file descriptor
    private final Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch(msg.what)
			{
			case MESSAGE_AND_CLIFD:
				SharedPreferences settings = server.this.getSharedPreferences(PREF, 0);
				String text = (String) msg.obj;
				
				display.setText(text);
				display.append("\n");
				
				Pattern p = Pattern.compile("\\d+"); 
				Matcher m = p.matcher(text);
				
				String clifd_STR = "";
				
				while(m.find())
				{
					clifd_STR = m.group();
					
				}
				
				if (clifd_STR.equals(""))
				{
					Toast.makeText(server.this, "clifd not acquired!", Toast.LENGTH_SHORT).show();
					return;
				}		
				
				settings.edit()
				.putInt(PREF_CLIFD, Integer.valueOf(clifd_STR))
				.commit();
				
				
						
				break;
		
			default:
				return;
			
			}
			
			L_service.start();
			
			timer = new Timer();
		    task = get_task();
		    timer.schedule(task, 1000, 500);
			
			super.handleMessage(msg);
		}
    	
    };
    
    
    
    
    //for retrieving incoming messages
    private final Handler mHandler_1 = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			String text = (String)msg.obj;
			
			
			//Toast.makeText(server.this, "received message: " + (String)msg.obj, Toast.LENGTH_SHORT).show();
			if (text.equals(""))
				return;
			
			if (text.equals("AA"))
			{
				display.append(text);
				display.append("\n");
				display.setSelection(display.getText().length());
				
				//move forward
				Linuxc.sendSignaltoGPP(8, 1);
				Linuxc.sendSignaltoGPP(9, 1);
				Linuxc.sendSignaltoGPP(11, 1);
				Linuxc.sendSignaltoGPP(12, 1);
				
				return;
			}
			
			if (text.equals("AB"))
			{
				display.append(text);
				display.append("\n");
				display.setSelection(display.getText().length());
				
				
				//turn left
				Linuxc.sendSignaltoGPP(8, 1);
				Linuxc.sendSignaltoGPP(9, 1);
				Linuxc.sendSignaltoGPP(11, 1);
				Linuxc.sendSignaltoGPP(12, 2);
				
				return;
			}
			
			if (text.equals("BA"))
			{
				display.append(text);
				display.append("\n");
				display.setSelection(display.getText().length());
				
				//turn right
				Linuxc.sendSignaltoGPP(8, 1);
				Linuxc.sendSignaltoGPP(9, 2);
				Linuxc.sendSignaltoGPP(11, 1);
				Linuxc.sendSignaltoGPP(12, 1);
				
				return;
			}
			
			if (text.equals("BB"))
			{
				display.append(text);
				display.append("\n");
				display.setSelection(display.getText().length());
				
				//move backward
				Linuxc.sendSignaltoGPP(8, 1);
				Linuxc.sendSignaltoGPP(9, 2);
				Linuxc.sendSignaltoGPP(11, 1);
				Linuxc.sendSignaltoGPP(12, 2);
				
				return;
			}
			
			
			if (text.equals("CC"))
			{
				display.append(text);
				display.append("\n");
				display.setSelection(display.getText().length());
				
				//stop the car
				Linuxc.sendSignaltoGPP(8, 2);
				Linuxc.sendSignaltoGPP(9, 2);
				Linuxc.sendSignaltoGPP(11, 2);
				Linuxc.sendSignaltoGPP(12, 2);
				
				return;
			}
			
		
			
			
			switch(msg.what)
			{
			
			case MESSAGE_RECEIVED:
				display.append((String) msg.obj);
				display.append("\n");
				display.setSelection(display.getText().length());
				
				break;
				
			default:
				return;
			
			}
			
			
			super.handleMessage(msg);
		}

		
    	
    };
  
    
    
    
    
    private Button.OnClickListener dis_handler = new Button.OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			//send the "off" signal to GPP1
			Linuxc.sendSignaltoGPP(1, 2);
		
			
			
			SharedPreferences settings = getSharedPreferences(PREF,0);
			
			settings.edit()
			.putInt(PREF_CLIFD, 0)
			.commit();
			
			if (timer != null)
				timer.cancel();
			
			if (irda_buzzer_fd >= 0 )
			{
				//stop the car
				Linuxc.sendSignaltoGPP(8, 2);
				Linuxc.sendSignaltoGPP(9, 2);
				Linuxc.sendSignaltoGPP(11, 2);
				Linuxc.sendSignaltoGPP(12, 2);
				
				
				Linuxc.closegpp();
			}
			
			
			display.setText("");
			
			//finish();
		}

		
    	
    };
    
    

    public static final String PREF = "IP_PORT";
    public static final String PREF_IP = "IP_ADDRESS";
    public static final String PREF_PORT = "PORT_NO";
    public static final String PREF_MSG = "MSG";
    public static final String PREF_CLIFD = "clifd";
    public static final String PREF_DELIVERY = "msg to deliver";
    public static final String PREF_RCV = "rcv";
   
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		settings.edit()
		.putString(PREF_IP, ip_addr.getText().toString())
		.putString(PREF_PORT, portno.getText().toString())
		.commit();
	
		//Toast.makeText(server.this, "OnPause() invoked!", Toast.LENGTH_SHORT).show();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//Toast.makeText(server.this, "OnResume() invoked!", Toast.LENGTH_SHORT).show();
		
		SharedPreferences settings = getSharedPreferences(PREF,0);
		String ip = settings.getString(PREF_IP, "");
		String port = settings.getString(PREF_PORT, "");
		if ( !ip.equals("") && !port.equals(""))
		{
			ip_addr.setText(ip);
			portno.setText(port);
		}
	
		
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		//Toast.makeText(server.this, "OnDestroy() invoked!", Toast.LENGTH_SHORT).show(); 
		
		
		super.onDestroy();
	}
   
	
	
	
	
	

}



