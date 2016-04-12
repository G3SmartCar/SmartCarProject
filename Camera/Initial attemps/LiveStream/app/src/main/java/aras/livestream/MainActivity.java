package aras.livestream;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class MainActivity extends ActionBarActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener{

    private MediaPlayer mPlayer;
    private SurfaceHolder surHolder;
    private SurfaceView surView;
    // Link of http video:
    String ipLink = "172.20.10.6:8080/?actiong=stream.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surView = (SurfaceView) findViewById(R.id.Stream);
        surHolder = surView.getHolder();
        surHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try{
            mPlayer = new MediaPlayer();
            mPlayer.setDisplay(surHolder);
            mPlayer.setDataSource(ipLink);
            mPlayer.prepare();
            mPlayer.setOnPreparedListener(this);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mPlayer.start();
    }
}