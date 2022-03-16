package org.kyon.tipron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.cerevo.ps1.footworkservicelib.IFootworkService;
import com.cerevo.ps1.footworkservicelib.IPS1ServiceCallback;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";
    public IFootworkService footworkIf = null;
    public Random random = new Random();

    ServiceConnection mFootworkConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(MainActivity.TAG, "[footworkIf] onServiceConnected");
            IFootworkService newFootworkIf = IFootworkService.Stub.asInterface(service);
            MainActivity.this.footworkIf = newFootworkIf;
            Log.d(MainActivity.TAG, "[footworkIf] initService call");
            try {
                MainActivity.this.footworkIf.initService(MainActivity.this.random.nextInt(), 0, new IPS1ServiceCallback.Stub() {
                    public void footworkCallback(int msgId, String jsonParams) throws RemoteException {
                        Log.i(MainActivity.TAG, "[footworkIf] initService callback: " + jsonParams);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i(MainActivity.TAG, "[footworkIf] onServiceConnected finish");
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.w(MainActivity.TAG, "[footworkIf] onServiceDisconnected");
            MainActivity.this.unbindService(MainActivity.this.mFootworkConn);
            MainActivity.this.footworkIf = null;
        }
    };

    private void trySetLED(int[] color) {
        if(MainActivity.this.footworkIf != null) {
            try {
                MainActivity.this.footworkIf.setLedPattern(7, color[0], color[1], color[2]);
            }catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Log.i(MainActivity.TAG, "[footworkIf] Core service is not binding.");
        }
    }

    private enum COLOR {
        R, G, B, M, C, Y, OFF, COLOR_NUM
    }
    private int[][] color = {{255,0,0},{0,255,0},{0,0,255},{228,0,127},{0,161,233},{255,217,0},{0,0,0}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.r_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySetLED(color[COLOR.R.ordinal()]);
            }
        });
        findViewById(R.id.g_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySetLED(color[COLOR.G.ordinal()]);
            }
        });
        findViewById(R.id.b_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySetLED(color[COLOR.B.ordinal()]);
            }
        });
        findViewById(R.id.m_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySetLED(color[COLOR.M.ordinal()]);
            }
        });
        findViewById(R.id.c_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySetLED(color[COLOR.C.ordinal()]);
            }
        });
        findViewById(R.id.y_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySetLED(color[COLOR.Y.ordinal()]);
            }
        });
        findViewById(R.id.off_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySetLED(color[COLOR.OFF.ordinal()]);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.footworkIf == null) {
            Intent intent = new Intent("com.cerevo.ps1.footworkservicelib.IFootworkService");
            intent.setPackage("com.cerevo.ps1.footworktest");
            bindService(intent, this.mFootworkConn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.footworkIf == null) {
            unbindService(this.mFootworkConn);
        }
    }
}