package checknpay.barmes.napst.checknpay;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class WearActivity extends Activity implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String PAGO_CONFIRMADO = "/pago_confirmado";
    private static final String PAGO_CANCELADO = "/pago_cancelado";
    GoogleApiClient mGoogleApiClient = null;
    final String TAG = "CheckNPay";
    private TextView mTextView;
    public static boolean recivedintent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addOnConnectionFailedListener(this)
                .build();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                if(recivedintent){
                    //Si se ha recivido un mensaje de pago
                    //se muestra el layout que contiene los botones y la informacion
                    //sobre el pago para el usuario
                    Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    LinearLayout menu = (LinearLayout) findViewById(R.id.MenuNotificacion);
                    menu.setVisibility(View.VISIBLE);
                    mTextView = (TextView) stub.findViewById(R.id.text_pago);
                    String s = "";
                    s += "¿Confirmar pago por " + getIntent().getStringExtra("importe");
                    s += " a fecha " + getIntent().getStringExtra("fecha");
                    s += " en concepto de " + getIntent().getStringExtra("concepto");
                    s += " y emitido por " + getIntent().getStringExtra("emisor") + "?";
                    mTextView.setText(s);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
    public void actualizar(){
        //chapuzilla para devolver la aplicacion a su estado original
        recivedintent=false;
    }
    public void confirmarPago(View view){
        LinearLayout confirmacion = (LinearLayout) findViewById(R.id.PagoConfirmado);
        LinearLayout menu = (LinearLayout) findViewById(R.id.MenuNotificacion);
        confirmacion.setVisibility(View.VISIBLE);
        menu.setVisibility(View.GONE);
        enviarConfirmacion(true,"");
        actualizar();
    }
    public void cancelarPago(View view){
        LinearLayout denegado = (LinearLayout) findViewById(R.id.PagoDenegado);
        LinearLayout menu = (LinearLayout) findViewById(R.id.MenuNotificacion);
        denegado.setVisibility(View.VISIBLE);
        menu.setVisibility(View.GONE);
        enviarConfirmacion(false,"");
        actualizar();
    }
    public void enviarConfirmacion(Boolean b, String id) {
        //Se envia el resultado, el metodo en este caso es sencillo
        //pero puede complicarse usando otras librerias de mas bajo nivel
        //que se encarguen de la encriptacion y la verificacion de los datos
        ConfirmacionPago p = new ConfirmacionPago(id,b);
        if(b){
            sendMessageToCompanion(PAGO_CONFIRMADO);
        }else{
            sendMessageToCompanion(PAGO_CANCELADO);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect to Google Api Client");
    }

    private void sendMessageToCompanion(final String path) {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        for (final Node node : getConnectedNodesResult.getNodes()) {
                            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path,
                                    new byte[0]).setResultCallback(getSendMessageResultCallback());
                        }
                    }
                }
        );

    }

    private ResultCallback<MessageApi.SendMessageResult> getSendMessageResultCallback() {
        return new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Failed to connect to Google Api Client with status "
                            + sendMessageResult.getStatus());
                }
            }
        };
    }
}
