package com.yu.zz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.yu.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SoccerClientActivity extends Activity {
	private Button sendButton = null;
	DataOutputStream outputToServer = null;
	DataInputStream inputFromServer = null;
	int N = 500;
	String commands[] = new String[N];
	
	//初始化
	private void init(){
		for(int i =0; i<N-1;i++){
			commands[i] = String.valueOf(i % 4);
		}
		commands[N-1]="9";
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //INIT!!
        init();
        /////////
        sendButton = (Button)findViewById(R.id.sendMSG);
        //sendButton.setOnClickListener(new StartListening());
        try{
        	Socket socket = new Socket("10.0.2.2",4567);
	        outputToServer = new DataOutputStream(socket.getOutputStream());
			inputFromServer = new DataInputStream(socket.getInputStream());
			int count = 0;
			int length = commands.length;
			while(true){
				if(count >= length)
					break;
				//outputToServer.writeUTF(String.valueOf(count));
				HandleProcessTask task = new HandleProcessTask(outputToServer, commands[count]);
				new Thread(task).start();
				count++;
				//outputToServer.flush();
				String s = inputFromServer.readUTF();
				System.out.println(s);
			}
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }catch (IOException e){
        	e.printStackTrace();
        }
    }
    
    
    
    class StartListening implements OnClickListener{

		public void onClick(View v) {
			try{
//				Socket socket = new Socket("10.0.2.2",2222);
//		        outputToServer = new DataOutputStream(socket.getOutputStream());
//				inputFromServer = new DataInputStream(socket.getInputStream());
				int count = 0;
				while(true){
					outputToServer.writeUTF(String.valueOf(count));
					count++;
					outputToServer.flush();
					String s = inputFromServer.readUTF();
					System.out.println(s);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
    	
    }
}