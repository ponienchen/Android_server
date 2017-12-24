package com.bbc.server;


public class Linuxc{
	
	public static native String conn(String ip, int port);
	
	public static native void deliver(int client_fd, String msg);
	
	public static native String recv(int clifd);

	
	public static native int opengpp();

	
	public static native int closegpp();
	

	//receive signal from GPP0
	public static native  int receiveSignal(int selection);

	//send signal to GPP1,8,9,11, or 12
	public static native  void sendSignaltoGPP(int selection, int on_off);
	
	
	static{
		System.loadLibrary("server_conn");
	}

}


