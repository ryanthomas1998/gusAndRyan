package com.example.ryanw.wirelessmediacontrol;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.graphics.Palette;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends Activity {

    public Button mButt;
    public Bitmap bitmap;


    private TextView serverStatus;

    // DEFAULT IP
    public static String SERVERIP = "10.0.2.15";

    // DESIGNATE A PORT
    public static final int SERVERPORT = 8080;

    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ActionBar act = getActionBar();
//        act.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4dd0e1")));


        if(!(Settings.Secure.getString(MainActivity.this.getContentResolver(), "enabled_notification_listeners").contains(this.getPackageName())))
        {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }



        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

        mButt =  (Button) findViewById(R.id.play);
        AudioManager bob = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);

        if(bob.isMusicActive()){
            mButt.setText("Pause");
        }

      findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              play(v);
          }
      });
        findViewById(R.id.skipForward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipF(v);
            }
        });
        findViewById(R.id.skipBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipB(v);
            }
        });
        findViewById(R.id.skipForward).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getApplicationContext(), ServerTesterActivity.class));
                return false;
            }
        });
        findViewById(R.id.skipBack).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getApplicationContext(), ClientTestActivity.class));
                return false;
            }
        });

        serverStatus = (TextView) findViewById(R.id.server_status);

        SERVERIP = getLocalIpAddress();
        Environment.getExternalStorageDirectory().getAbsolutePath();

        Thread fst = new Thread(new ServerThread());
        fst.start();
    }

    public  void skipF(View v){
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
            sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
            sendOrderedBroadcast(i, null);
        }
    }
    public void skipB(View v){
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            sendOrderedBroadcast(i, null);
        }
    }
    public void play(View v){

        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
            sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
            sendOrderedBroadcast(i, null);
        }


        if (mButt.getText().equals("Pause")){
            mButt.setText("Play");
        }
        else{
            mButt.setText("Pause");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            setContentView(R.layout.settings);
        }

        return super.onOptionsItemSelected(item);
    }
    public class ServerThread implements Runnable {

        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // LISTEN FOR INCOMING CLIENTS
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Connected.");
                            }
                        });

                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                Log.d("ServerActivity", line);
                                final String msg = line;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        serverStatus.setText(msg);
                                        // DO WHATEVER YOU WANT TO THE FRONT END
                                        // THIS IS WHERE YOU CAN BE CREATIVE
                                        if (msg.equals("SkipF")){
                                            MainActivity.this.skipF(getCurrentFocus());
                                        }
                                        if (msg.equals("SkipB")){
                                            MainActivity.this.skipB(getCurrentFocus());
                                        }
                                        if (msg.equals("Play")){
                                            MainActivity.this.play(getCurrentFocus());
                                        }
                                    }
                                });
                            }
                            break;
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    private String getLocalIpAddress() {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.d("ip", ip);
        return ip;
    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            if(pack.equals("com.google.android.music")){
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");


            TextView artist = (TextView) findViewById(R.id.artist);
            artist.setText(text);
            TextView song = (TextView) findViewById(R.id.crtSng);
            song.setText(title);

            Context remotePackageContext = null;
            try {


                byte[] byteArray =intent.getByteArrayExtra("icon");

                if(byteArray !=null) {
                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    Palette p = Palette.generate(bitmap);
                    findViewById(R.id.mainlayout).setBackground(new ColorDrawable(p.getMutedColor(android.R.color.holo_red_dark)));
                }
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);

                        ((ImageView) findViewById(R.id.imageTest)).setBackgroundDrawable(bitmapDrawable);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        }
    };
    @Override
    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




