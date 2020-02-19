package com.fazlagida.burak.smartscaleusb;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fazlagida.burak.smartscaleusb.Api.ApiClient;
import com.fazlagida.burak.smartscaleusb.Api.ApiService;
import com.fazlagida.burak.smartscaleusb.model.Flavour;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Map;

public class FlavourActivity extends Activity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    ApiService service;
    private static int count = 0;
    private int panipuariCount;
    private Button btn1, btn2, btn3, btn4, btn5, btn6;
    SharedPref pref;

    private TextView available, myPlate;

    //Defining a Callback which triggers whenever data is read.
    UsbSerialInterface.UsbReadCallback mCallback = arg0 -> {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String data = null;
                try {
                    data = new String(arg0, "UTF-8");

                    if (data.equals("A")) {

                        count++;

                        if (count == panipuariCount) {
                            stopConnection();
                            count = 0;
                            writeToArd("0");
                            disableButton();
                            startActivity(new Intent(FlavourActivity.this, ThankYouActivity.class));
                            finish();
                        }

                        int left = panipuariCount - count;
                        available.setText(String.valueOf(left));

                    }


                } catch (Exception e) {
                    Config.openDialog(FlavourActivity.this,"ERROR","Getting value from arduino.");
                }
            }
        });

    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
//                            Toast.makeText(context, "Connection Opened.", Toast.LENGTH_SHORT).show();

                        } else {
                            Config.openDialog(FlavourActivity.this,"CONNECTION PROBLEM", "PORT NOT OPEN");
                        }
                    } else {
                        Config.openDialog(FlavourActivity.this,"CONNECTION PROBLEM", "PORT IS EMPTY/NULL");
                    }
                } else {
                    Config.openDialog(FlavourActivity.this,"PERMISSION", "PERMISSION Required");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                startConnection();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                stopConnection();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flavour);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);

        service = ApiClient.getInstance(this).create(ApiService.class);
        pref = SharedPref.getPreferences(this);

        setupButton();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

    }

    public void setupButton() {

        startConnection();

        btn1 = (Button) findViewById(R.id.fav1);
        btn2 = (Button) findViewById(R.id.fav2);
        btn3 = (Button) findViewById(R.id.fav3);
        btn4 = (Button) findViewById(R.id.fav4);
        btn5 = (Button) findViewById(R.id.fav5);
        btn6 = (Button) findViewById(R.id.fav6);

        available = findViewById(R.id.total_avilalbe);
//        eaten = findViewById(R.id.panipuari_eaten);
//        pp = findViewById(R.id.per_plate);
        myPlate = findViewById(R.id.plate);

        btn1.setText(pref.getStringData("fav1", "Flavour 1"));
        btn2.setText(pref.getStringData("fav2", "Flavour 2"));
        btn3.setText(pref.getStringData("fav3", "Flavour 3"));
        btn4.setText(pref.getStringData("fav4", "Flavour 4"));
        btn5.setText(pref.getStringData("fav5", "Flavour 5"));
        btn6.setText(pref.getStringData("fav6", "Flavour 6"));

        int plate = pref.getIntData("plate", 7);
        int perPlate = pref.getIntData("perplate", 1);

        panipuariCount = perPlate * plate;
        available.setText(String.valueOf(panipuariCount));
        myPlate.setText(String.valueOf(plate));

        btn1.setOnClickListener(view -> sendDataToArduino("1"));
        btn2.setOnClickListener(view -> sendDataToArduino("2"));
        btn3.setOnClickListener(view -> sendDataToArduino("3"));
        btn4.setOnClickListener(view -> sendDataToArduino("4"));
        btn5.setOnClickListener(view -> sendDataToArduino("5"));
        btn6.setOnClickListener(view -> sendDataToArduino("6"));

    }

    public void startConnection() {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        } else {
            Config.openDialog(this,"No Connection","Device is not Connected.");
        }

    }

    public void sendDataToArduino(String data) {

        runOnUiThread(() -> {
            try {
                if (data != null) {
                    writeToArd(data);
                }
            } catch (Exception e) {
                Config.openDialog(FlavourActivity.this,"ERROR","Cannot send value to arduino.");
            }
        });

    }

    public void stopConnection() {
        serialPort.close();
    }

    public void writeToArd(String data) {
        byte[] bytesOut = data.getBytes();
        serialPort.write(bytesOut);
    }

    public void disableButton() {
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn5.setEnabled(false);
        btn6.setEnabled(false);
    }


    @Override
    public void onBackPressed() {
        // no backpress happen
    }

}
