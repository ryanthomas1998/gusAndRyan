package com.example.ryanw.wirelessmediacontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ClientTestActivity extends Activity {

    private EditText serverIp;

    private Button connectPhones;

    private String serverIpAddress = "";

    private boolean connected = false;

    private int playClick, skipFClick, skipBClick;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_testing);

        serverIp = (EditText) findViewById(R.id.server_ip);
        connectPhones = (Button) findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(connectListener);
        findViewById(R.id.skipBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipBClick++;
            }
        });
        findViewById(R.id.skipForward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipFClick++;
            }
        });
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick++;
            }
        });
    }

    private View.OnClickListener connectListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!connected) {
                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals("")) {
                    Thread cThread = new Thread(new ClientThread(R.id.connect_phones));
                    cThread.start();
                }
            }
        }
    };

    public class ClientThread implements Runnable {

        int bId;
        public ClientThread(int buttonId){
            bId=buttonId;
        }
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = new Socket(serverAddr, ServerTesterActivity.SERVERPORT);
                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                .getOutputStream())), true);
                        // WHERE YOU ISSUE THE COMMANDS

                        if(skipBClick>0){
                           skipBClick=0;
                            out.println("SkipB");
                        }
                        if(skipFClick>0){
                            skipFClick=0;
                            out.println("SkipF");
                        }
                        if(playClick>0){
                            playClick=0;
                            out.println("Play");
                        }
                        Log.d("ClientActivity", "C: Sent.");

                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            connected=false;

    }
}
