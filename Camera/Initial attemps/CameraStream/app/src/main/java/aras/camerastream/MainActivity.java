package aras.camerastream;
/**
  *
  * @author Aras Bazyan
  * 8/04/2016
  *
  * Initial Attempt to get live stream
  */
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void camera(View view) {
        Intent openCamera = new Intent(Intent.ACTION_VIEW, Uri.parse("http://172.20.10.6:8080/?action=stream"));
        startActivity(openCamera);
    }
}
