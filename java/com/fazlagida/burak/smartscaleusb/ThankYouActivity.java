package com.fazlagida.burak.smartscaleusb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ThankYouActivity extends AppCompatActivity {

    private ImageView thankyou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        thankyou = findViewById(R.id.thankyou);
        thankyou.setOnClickListener(view -> {
            startActivity(new Intent(ThankYouActivity.this,BarCodeActivity.class));
            finish();

        });
    }

    @Override
    public void onBackPressed() {
        // no backpress happen
    }
}
