package com.example.ryanw.wirelessmediacontrol;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
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
        findViewById(R.id.imageTest).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getApplicationContext(), ServerTesterActivity.class));
                return false;
            }
        });
    }

    public void skipF(View v){
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
                Bitmap bmp = null;
                if(byteArray !=null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
                ((ImageView) findViewById(R.id.imageTest)).setBackgroundDrawable(bitmapDrawable);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
    };
}



