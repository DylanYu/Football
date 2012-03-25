package com.yu.zz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.yu.R;
import com.yu.R.id;
import com.yu.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SoccerServerActivity extends Activity {
	private Button startListening = null;
	private Button showResult = null;
	
	//ÿ���ھ����ms��
	private static final int cycle = 300;
	private static final int processTime = 10;

	// �������뻺��
	String inBuffer[] = new String[2];
	// �����������
	String outBuffer[] = new String[2];
	// �����ʾ
	String result[] = new String[2];

	int a1 = 0, b1 = 0, c1 = 0, d1 = 0, e1 = 0;
	int a2 = 0, b2 = 0, c2 = 0, d2 = 0, e2 = 0;

	// ��ʼ��
	private void someInit() {
		for (int i = 0; i < inBuffer.length; i++)
			inBuffer[i] = "-1";
		for (int i = 0; i < outBuffer.length; i++)
			outBuffer[i] = "-1";
		for (int i = 0; i < result.length; i++)
			result[i] = "-";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		startListening = (Button) findViewById(R.id.startListening);
		startListening.setOnClickListener(new startListener());
		showResult = (Button) findViewById(R.id.showResult);
		showResult.setOnClickListener(new showListener());
		// ��ʼ��
		someInit();
	}

	class showListener implements OnClickListener {
		public void onClick(View v) {
//			System.out.println("0 : " + result[0]);
//			System.out.println("1 : " + result[1]);
			System.out.println(String.format("1:%d; 2:%d; 3:%d; 4:%d; 5:%d", a1,b1,c1,d1,e1));
			System.out.println(String.format("1:%d; 2:%d; 3:%d; 4:%d; 5:%d", a2,b2,c2,d2,e2));
			System.out.println("0 : " + result[0]);
			System.out.println("1 : " + result[1]);
			
		}

	}

	class startListener implements OnClickListener {
		public void onClick(View v) {
			new ServerThread().start();
		}

	}

	class ServerThread extends Thread {
		public void run() {

			ServerSocket sc = null;
			try {
				sc = new ServerSocket(5678);
				System.out.println("Server Started at " + new Date());
				int numOfClient = 0;
				while (true) {
					Socket socket = sc.accept();
					System.out.println("Client No." + numOfClient
							+ "connected at " + new Date());
					InetAddress inetAddress = socket.getInetAddress();
					System.out.println("Client No." + numOfClient
							+ "'s name is " + inetAddress.getHostName());
					System.out.println("Client No." + numOfClient
							+ "'s address is " + inetAddress.getHostAddress());
					HandleReceiver task = new HandleReceiver(socket,
							numOfClient);
					HandleDataProcess task2 = new HandleDataProcess(numOfClient);
					HandleSender task3 = new HandleSender(socket, numOfClient);
					new Thread(task).start();
					new Thread(task2).start();
					new Thread(task3).start();
					numOfClient++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					sc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	class sendTask extends TimerTask {
		private DataOutputStream outToClient;
		private int count;

		public sendTask(DataOutputStream outToClient) {
			this.outToClient = outToClient;
			this.count = 1;
		}

		public void run() {
			try {
				outToClient.writeUTF("fromServerToClient" + "at " + new Date());
				outToClient.flush();
				// outToClient.close();!!!!!!!!!!!!!!!
				System.out.println(count++ + ":send");
			} catch (IOException e) {
				System.out.println("error here!");
				e.printStackTrace();
			}
		}
	}

	class processTask extends TimerTask {
		private int numOfClient;
		private int count;

		public processTask(int numOfClient) {
			this.count = 1;
			this.numOfClient = numOfClient;
		}

		public void run() {
			try {
				Thread.sleep(processTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result[numOfClient] += inBuffer[numOfClient];
			/**
			 * Here for test
			 */
			int n = Integer.parseInt(inBuffer[numOfClient]);
			if (numOfClient == 0) {
				switch (n) {
				case 0:
					a1++;
					break;
				case 1:
					b1++;
					break;
				case 2:
					c1++;
					break;
				case 3:
					d1++;
					break;
				case -1:
					e1++;
					break;
				default:
					break;
				}

			} else {
				switch (n) {
				case 0:
					a2++;
					break;
				case 1:
					b2++;
					break;
				case 2:
					c2++;
					break;
				case 3:
					d2++;
					break;
				case -1:
					e2++;
					break;
				default:
					break;
				}
			}
			System.out.println(count++ + ": process someting");
		}
	}

	class HandleDataProcess implements Runnable {
		private int numOfClient;

		public HandleDataProcess(int numOfClient) {
			this.numOfClient = numOfClient;
		}

		public void run() {
			Timer timer = new Timer();
			timer.schedule(new processTask(numOfClient), 0, cycle);
		}
	}

	class HandleSender implements Runnable {
		private Socket socket;
		// ������Ҫclient����Ϣ��������͵���Ϣ������û���������Ҫ
		private int numOfClient;

		public HandleSender(Socket socket, int numOfCilent) {
			this.socket = socket;
			this.numOfClient = numOfCilent;
		}

		public void run() {
			DataOutputStream outToClient = null;
			try {
				outToClient = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			Timer timer = new Timer();
			timer.schedule(new sendTask(outToClient), 0, cycle);
		}
	}

	class HandleReceiver implements Runnable {
		private Socket socket;
		private int numOfClient;

		// private boolean is
		public HandleReceiver(Socket socket, int numOfClient) {
			this.socket = socket;
			this.numOfClient = numOfClient;
		}

		public void run() {
			try {
				DataInputStream in = new DataInputStream(
						socket.getInputStream());
				// DataOutputStream outToClient = new
				// DataOutputStream(socket.getOutputStream());

				int count = 0;

				while (true) {
					String temp = in.readUTF().toString();
					if (numOfClient == 0)
						inBuffer[0] = temp;
					else
						inBuffer[1] = temp;
					count++;
					System.out.println(count + ": receive");

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}