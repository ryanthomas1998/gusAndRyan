package com.example.ryanw.wirelessmediacontrol;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    public Button mButt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    public void skipF(View v){
       Intent test = new Intent("com.android.music.musicservicecommand");
        test.putExtra("command", "next");
        MainActivity.this.sendBroadcast(test);
    }
    public void skipB(View v){
        Intent test = new Intent("com.android.music.musicservicecommand");
        test.putExtra("command", "previous");
        MainActivity.this.sendBroadcast(test);
    }
    public void play(View v){
        Intent test = new Intent("com.android.music.musicservicecommand");
        if (mButt.getText().equals("Pause")){
            mButt.setText("Play");
            test.putExtra("command", "pause");
        }
        else{
            mButt.setText("Pause");
            test.putExtra("command", "pause");
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

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            Log.d("Package Name", pack);
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            int id = intent.getIntExtra("icon", 0);

            Context remotePackageContext = null;
            try {
//                remotePackageContext = getApplicationContext().createPackageContext(pack, 0);
//                Drawable icon = remotePackageContext.getResources().getDrawable(id);
//                if(icon !=null) {
//                    ((ImageView) findViewById(R.id.imageTest)).setBackground(icon);
//                }
//                byte[] byteArray =intent.getByteArrayExtra("icon");
//                Bitmap bmp = null;
//                if(byteArray !=null) {
//                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                }
                TextView artist = (TextView) findViewById(R.id.artist);
                artist.setText(title);
                TextView song = (TextView) findViewById(R.id.crtSng);
                song.setText(text);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}



