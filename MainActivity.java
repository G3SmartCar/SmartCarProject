package aras.camerafeed;
/**
  *
  * @author Aras Bazyan
  * 16/04/2016
  *
  * Version 1.0
  */
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * This app assumes the RaspberryPi and its camera are powered up/running and are connected to internet.
         * The source should be updated if different connection is used instead of Aras's hotspot.
         */

        String feedSource = "http://172.20.10.6:8080/javascript_simple.html";
        WebView view = (WebView) this.findViewById(R.id.webView);
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(feedSource);
    }
}
