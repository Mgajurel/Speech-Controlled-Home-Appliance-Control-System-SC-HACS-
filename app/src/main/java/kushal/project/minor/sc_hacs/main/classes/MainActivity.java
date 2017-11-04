package kushal.project.minor.sc_hacs.main.classes;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.clans.fab.FloatingActionButton;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import kushal.project.minor.sc_hacs.R;
import kushal.project.minor.sc_hacs.main.Fragments.NavigationDrawerFragment;
import kushal.project.minor.sc_hacs.main.services.BluetoothServices;


public class MainActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener {

    //variables
    public Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    String newBuf = "";

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;



    private TextView returnedText;
    private ToggleButton toggleButton;
    private CircularProgressView progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private MaterialRippleLayout ripp;
    private FloatingActionButton fb1, fb3, fb4;
    private static boolean connectCount = false;
    private static boolean delay = false;

    //variables
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private static final String on = "turn on";
    private static final String off = "turn off";
    private static final String r = "red";
    private static final String g = "green";
    private static final String b = "blue";

    private String mConnectedDeviceName = null;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothServices mService = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);

        image1.setBackgroundResource(R.mipmap.ic_bulb_off);
        image2.setBackgroundResource(R.mipmap.ic_bulb_off);
        image3.setBackgroundResource(R.mipmap.ic_bulb_off);

        //Setting up a custom toolbar (or App Bar as per the material design specs
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Setting up a custom toolbar (or App Bar as per the material design specs

        //setting up the navigation drawer
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_drawer);

        navigationDrawerFragment.setUp(R.id.nav_drawer, mDrawerLayout, toolbar);

        //setting up the navigation drawer


        //speechRecognition

        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (CircularProgressView) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        ripp = (MaterialRippleLayout) findViewById(R.id.rip);

        ripp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                returnedText.setText("");

            }
        });


        progressBar.setVisibility(View.INVISIBLE);


        initaiateSpeechRecog();


        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);

                    speech.startListening(recognizerIntent);


                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();


                }
            }
        });


        //speechRecognition

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        //floating buttons


        fb1 = (FloatingActionButton) findViewById(R.id.connect);
        fb3 = (FloatingActionButton) findViewById(R.id.menu_item4);
        fb4 = (FloatingActionButton) findViewById(R.id.menu_item5);


        fb1.setOnClickListener(this);
        fb3.setOnClickListener(this);
        fb4.setOnClickListener(this);
        //floating butttons
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    private void initaiateSpeechRecog() {


        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        if (D) Log.e(TAG, "++ON STRAT ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mService == null) setupConnection();
        }


    }

    @Override
    protected synchronized void onResume() {
        super.onResume();

        if (D) Log.e(TAG, "+ON RESUME+");

        initaiateSpeechRecog();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mService.getState() == BluetoothServices.STATE_NONE) {
                // Start the Bluetooth chat services
                mService.start();
            }
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mService != null) mService.stop();
        if (mBluetoothAdapter.isEnabled()) mBluetoothAdapter.disable();
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothServices.STATE_CONNECTED:


                            break;
                        case BluetoothServices.STATE_CONNECTING:


                            break;
                        case BluetoothServices.STATE_LISTEN:
                        case BluetoothServices.STATE_NONE:

                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;

                    // construct a string from the valid bytes in the buffer.
                    String readMessage = new String(readBuf, 0, msg.arg1);

//                    Toast.makeText(MainActivity.this, readMessage, Toast.LENGTH_SHORT).show();

                    setImage(readMessage);

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }




        }
    };


    private void setImage(String readMessage) {

        String sec= getIntStr(readMessage);

        switch (readMessage) {
            case "1b":
                image1.setBackgroundResource(R.mipmap.ic_bulb_on_green);
                break;
            case "1c":
                image1.setBackgroundResource(R.mipmap.ic_bulb_off);
                break;
            case "1d":
                image2.setBackgroundResource(R.mipmap.ic_bulb_on_red);
                break;
            case "1e":
                image2.setBackgroundResource(R.mipmap.ic_bulb_off);
                break;
            case "1f":
                image3.setBackgroundResource(R.mipmap.ic_bulb_on_blue);
                break;
            case "1g":
                image3.setBackgroundResource(R.mipmap.ic_bulb_off);
                break;
            case "1h":
                image1.setBackgroundResource(R.mipmap.ic_bulb_on_green);
                image2.setBackgroundResource(R.mipmap.ic_bulb_on_red);
                break;
            case "1i":
                image1.setBackgroundResource(R.mipmap.ic_bulb_off);
                image2.setBackgroundResource(R.mipmap.ic_bulb_off);
                break;
            case "1j":
                image2.setBackgroundResource(R.mipmap.ic_bulb_on_red);
                image3.setBackgroundResource(R.mipmap.ic_bulb_on_blue);
                break;
            case "1k":
                image3.setBackgroundResource(R.mipmap.ic_bulb_off);
                image2.setBackgroundResource(R.mipmap.ic_bulb_off);

                break;
            case "1l":
                image3.setBackgroundResource(R.mipmap.ic_bulb_on_blue);
                image1.setBackgroundResource(R.mipmap.ic_bulb_on_green);

                break;
            case "1m":
                image1.setBackgroundResource(R.mipmap.ic_bulb_off);
                image3.setBackgroundResource(R.mipmap.ic_bulb_off);

                break;
            case "1n":
                image1.setBackgroundResource(R.mipmap.ic_bulb_on_green);
                image2.setBackgroundResource(R.mipmap.ic_bulb_on_red);
                image3.setBackgroundResource(R.mipmap.ic_bulb_on_blue);

                break;
            case "1o":
                image1.setBackgroundResource(R.mipmap.ic_bulb_off);
                image2.setBackgroundResource(R.mipmap.ic_bulb_off);
                image3.setBackgroundResource(R.mipmap.ic_bulb_off);
                break;

            default:
                delayReceive(readMessage,sec);
                break;
        }
    }

    private void delayReceive(String readMessage, String sec) {

        if(TextUtils.equals(readMessage,sec + "b")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image1.setBackgroundResource(R.mipmap.ic_bulb_on_green);

                }
            });

        }else if(TextUtils.equals(readMessage,sec + "c")){
            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image1.setBackgroundResource(R.mipmap.ic_bulb_off);

                }
            });
        }else if(TextUtils.equals(readMessage,sec+"d")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image2.setBackgroundResource(R.mipmap.ic_bulb_on_red);

                }
            });

        }
        else if(TextUtils.equals(readMessage,sec+"e")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image2.setBackgroundResource(R.mipmap.ic_bulb_off);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"f")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image3.setBackgroundResource(R.mipmap.ic_bulb_on_blue);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"g")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image2.setBackgroundResource(R.mipmap.ic_bulb_off);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"h")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image1.setBackgroundResource(R.mipmap.ic_bulb_on_green);
                    image2.setBackgroundResource(R.mipmap.ic_bulb_on_red);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"i")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image1.setBackgroundResource(R.mipmap.ic_bulb_off);
                    image2.setBackgroundResource(R.mipmap.ic_bulb_off);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"j")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image3.setBackgroundResource(R.mipmap.ic_bulb_on_blue);
                    image2.setBackgroundResource(R.mipmap.ic_bulb_on_red);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"k")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image3.setBackgroundResource(R.mipmap.ic_bulb_off);
                    image2.setBackgroundResource(R.mipmap.ic_bulb_off);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"l")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image3.setBackgroundResource(R.mipmap.ic_bulb_on_blue);
                    image1.setBackgroundResource(R.mipmap.ic_bulb_on_green);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"m")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image3.setBackgroundResource(R.mipmap.ic_bulb_off);
                    image1.setBackgroundResource(R.mipmap.ic_bulb_off);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"n")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image1.setBackgroundResource(R.mipmap.ic_bulb_on_green);
                    image3.setBackgroundResource(R.mipmap.ic_bulb_on_blue);
                    image2.setBackgroundResource(R.mipmap.ic_bulb_on_red);

                }
            });

        }else if(TextUtils.equals(readMessage,sec+"o")){

            Utils.delay(getInt(sec), new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    image1.setBackgroundResource(R.mipmap.ic_bulb_off);
                    image3.setBackgroundResource(R.mipmap.ic_bulb_off);
                    image2.setBackgroundResource(R.mipmap.ic_bulb_off);

                }
            });

        }
    }

    private void setupConnection() {

        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mService = new BluetoothServices(this, mHandler);
    }

    @Override
    public void onClick(View view) {

        setupConnection();
        int ID = view.getId();
        switch (ID) {
            case R.id.connect:
                if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "Enable Bluetooth", Toast.LENGTH_LONG).show();
                } else {

                    //Toast.makeText(this,"i am here",Toast.LENGTH_SHORT).show();

                    if (connectCount == true) {

                        setupConnection();
                    }

                    connectDevice(true);


                }
                break;


            case R.id.menu_item4:

                disconnect();
                connectCount = true;
                Toast.makeText(getApplicationContext(), "Bluetooth service disconnected", Toast.LENGTH_LONG).show();


                break;
            case R.id.menu_item5:
                if (mService.getState() == BluetoothServices.STATE_CONNECTED) {    //Is there a connection to another device
                    mService.stop();    //Break bluetooth connection
                }
                if (mBluetoothAdapter.isEnabled()) {    //Check if bluetooth is enabled
                    mBluetoothAdapter.disable();    //Disable bluetooth
                }
                finish();
                break;
            default:
                Toast.makeText(getApplicationContext(), "This is item " + ID, Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnect() {

        if (mService.getState() == BluetoothServices.STATE_CONNECTED) {    //Is there a connection to another device
            mService.stop();    //Break bluetooth connection
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");

        if (speech != null) {
            speech.destroy();
        }


    }

    private void connectDevice(boolean secure) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("20:16:03:21:72:39");
        if (device == null) {
            Toast.makeText(getApplicationContext(), "device not found", Toast.LENGTH_LONG).show();
        }

        mService.connect(device, secure);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.setting) {

            Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();

        }


        if (id == R.id.speech) {

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                Toast.makeText(getApplicationContext(), "Blutooth is already enabled", Toast.LENGTH_SHORT).show();
            }


        }

        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {

                closeDrawer();

            } else {

                openDrawer();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private void closeDrawer() {

        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void openDrawer() {

        mDrawerLayout.openDrawer(Gravity.LEFT);


    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

        progressBar.setIndeterminate(false);
        progressBar.resetAnimation();
        progressBar.setMaxProgress(10);
        progressBar.startAnimation();


    }

    @Override
    public void onRmsChanged(float v) {


        progressBar.setProgress((int) v);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);


    }

    @Override
    public void onError(int i) {
        String errorMessage = getErrorText(i);
        Log.i(LOG_TAG, "FAILED" + errorMessage);
        returnedText.setText(errorMessage);
        toggleButton.setChecked(false);
    }

    private String getErrorText(int i) {
        String message;
        switch (i) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;

    }

    @Override
    public void onResults(Bundle bundle) {

        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = null;
        for (String result : matches)
            text = result;
        returnedText.setText(text);


        if (mService.getState() != BluetoothServices.STATE_CONNECTED) {    //Not connected to device
            Toast.makeText(getApplicationContext(), "Not connected to a device", Toast.LENGTH_SHORT).show();
        } else {



            String numStr = getIntStr(text);



//            Toast.makeText(getApplicationContext(), numStr, Toast.LENGTH_LONG).show();



                switch (returnedText.getText().toString()) {
                    case "turn on green":
                        returnedText.setTextColor(getResources().getColor(R.color.green));
                        mService.write("0a*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

                        break;
                    case "turn off green":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0b*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

                        break;
                    case "turn on red":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0c*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn off read":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0d*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn on blue":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0e*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn off Bluetooth":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0f*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn on green and red":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0g*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn off green and red":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0h*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn on red and blue":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0i*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn off red and blue":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0j*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn on green and blue":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0k*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn off green and blue":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0l*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn on all":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0m*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "turn off all":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0n*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "psychopath":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0o*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "ask your mama":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0p*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "baby reborn":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0q*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "Sunday":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0r*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;
                    case "binary counter":
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

                        break;
                    case "turn on the projector":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0e*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;

                    case "turn off the projector":
                        returnedText.setTextColor(getResources().getColor(R.color.green));

                        mService.write("0f*".getBytes());
                        Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                        break;

                    default:

                        delayCommand(returnedText.getText().toString(), numStr);


                        break;


            }



        }
        Log.i(LOG_TAG, "onResults");

    }



    private String getIntStr(String text) {
        String numStr = "";

        for (int i = 0; i < text.length(); i++) {
            char charCheck = text.charAt(i);
            if (Character.isDigit(charCheck)) {
                numStr += charCheck;

            }
        }
        return numStr;
    }

    private void delayCommand( String text, String numStr) {

        if (TextUtils.equals(text, on + " " + g + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"a*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text, off + " " + g + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"b*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text, on + " " + r + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"c*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text, off + " " + "read" + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"d*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text, on + " " + b + " after " + numStr +" seconds") ){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"e*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text, off + " " + "Bluetooth " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr + "f*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.equals(text, on + " " + g +" and " + r + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"g*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text, off + " " + g +" and " + r + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"h*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text, on + " " + r +" and " + b + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"i*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text, off + " " + r +" and " + b + " after " + numStr +" seconds") ){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr+"j*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.equals(text,on + " " + g +" and " + b + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr + "k*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.equals(text, off + " " + g +" and " + b + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr + "l*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.equals(text, on + " all" + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr + "m*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.equals(text, off + " all" + " after " + numStr +" seconds")){

            returnedText.setTextColor(getResources().getColor(R.color.green));

            mService.write((numStr + "n*").getBytes());
            Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
        }


        else {

            returnedText.setTextColor(getResources().getColor(R.color.red));
            Toast.makeText(MainActivity.this, "Invalid Command", Toast.LENGTH_SHORT).show();

        }

    }

    private int getInt(String numStr) {

        switch (numStr){
            case "1":
                return 1;
            case "2":
                return 2;
            case "3":
                return 3;
            case "4":
                return 1;
            case "5":
                return 2;
            case "6":
                return 3;
            case "7":
                return 1;
            case "8":
                return 2;
            case "9":
                return 3;
            default:
                return 100;
        }

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }


}

