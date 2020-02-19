package com.fazlagida.burak.smartscaleusb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fazlagida.burak.smartscaleusb.Api.ApiClient;
import com.fazlagida.burak.smartscaleusb.Api.ApiService;
import com.fazlagida.burak.smartscaleusb.model.Flavour;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login, num1, num2, num3, num4, num5, num6, num7, num8, num9, num0, back;
    private  EditText userpin;
    ApiService service;
    SharedPref pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        service = ApiClient.getInstance(this).create(ApiService.class);
        pref = SharedPref.getPreferences(this);

        login = findViewById(R.id.btn_ok);
        userpin = findViewById(R.id.userpin);
        num0 = findViewById(R.id.btn0);
        num1 = findViewById(R.id.btn1);
        num2 = findViewById(R.id.btn2);
        num3 = findViewById(R.id.btn3);
        num4 = findViewById(R.id.btn4);
        num5 = findViewById(R.id.btn5);
        num6 = findViewById(R.id.btn6);
        num7 = findViewById(R.id.btn7);
        num8 = findViewById(R.id.btn8);
        num9 = findViewById(R.id.btn9);
        back = findViewById(R.id.btn_back);

        login.setOnClickListener(this);
        num0.setOnClickListener(this);
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        num6.setOnClickListener(this);
        num7.setOnClickListener(this);
        num8.setOnClickListener(this);
        num9.setOnClickListener(this);
        back.setOnClickListener(this);

        getFlavour();

    }

    public void getFlavour() {

        service.flavour().enqueue(new Callback<List<Flavour>>() {
            @Override
            public void onResponse(Call<List<Flavour>> call, Response<List<Flavour>> response) {

                List<Flavour> favs = response.body();

                if (response.isSuccessful()) {

                    for (Flavour fav : favs) {

                        pref.setIntData("perplate", Integer.parseInt(fav.getPerPlate()));
                        pref.setStringData("fav1", fav.getFav1());
                        pref.setStringData("fav2", fav.getFav2());
                        pref.setStringData("fav3", fav.getFav3());
                        pref.setStringData("fav4", fav.getFav4());
                        pref.setStringData("fav5", fav.getFav5());
                        pref.setStringData("fav6", fav.getFav6());
                    }

                }

            }

            @Override
            public void onFailure(Call<List<Flavour>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "SERVER ERROR", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_ok:
                startActivity(new Intent(LoginActivity.this, BarCodeActivity.class));
                finish();
                break;
            case R.id.btn0:
                pin("0");
                break;
            case R.id.btn1:
                pin("1");
                startActivity(new Intent(LoginActivity.this, BarCodeActivity.class));
                finish();
                break;
            case R.id.btn2:
                pin("2");
                break;
            case R.id.btn3:
                pin("3");
                break;
            case R.id.btn4:
                pin("4");
                break;
            case R.id.btn5:
                pin("5");
                break;
            case R.id.btn6:
                pin("6");
                break;
            case R.id.btn7:
                pin("7");
                break;
            case R.id.btn8:
                pin("8");
                break;
            case R.id.btn9:
                pin("9");
                break;
            case R.id.btn_back:
                backspace();
                break;
        }

    }

    public void pin(String pin) {
        String code = userpin.getText().toString().trim();
        if (!code.isEmpty()) {
            code = code + pin;
            userpin.setText(code);
        }
    }

    public void backspace() {
        String pin = userpin.getText().toString().trim();
        if (!pin.isEmpty()) {
            pin.substring(0, pin.length() - 1);
            userpin.setText(pin);
        }
    }
}
