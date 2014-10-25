package checknpay.barmes.napst.checknpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;


public class PrincipalActivity extends Activity  implements MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    final String TAG = "CheckNPay";
    public static GoogleApiClient mGoogleApiClient = null;
    private static final String PAGO_CONFIRMADO = "/pago_confirmado";
    private static final String PAGO_CANCELADO = "/pago_cancelado";
    private static final String NOTIFICAR = "/notificar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_principal);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    public void loguear(View view) {
        //En esta metodo se debera guardar el usuario y contraseña para
        //conectarse al servidor push
        //puesto que es un prototipo y no poseemos la informacion sobre
        //la capa de conexion del servidor push el boton no hara la etapa de logueo
        LinearLayout login = (LinearLayout) findViewById(R.id.login);
        LinearLayout menu = (LinearLayout) findViewById(R.id.Menu);
        login.setVisibility(View.GONE);
        menu.setVisibility(View.VISIBLE);
    }
    public void emparejar(View view){
        //Se empareja el wereable elegido para permitir que este sea
        //el que permita o cancele los pagos asi como
        //almacenar en este ultimo una firma digital para verificar
        //su identidad y evitar posibles hackeos
        //Para este proceso hara falta ingresar de nuevo el usuario y contraseña
        //pues el usuario y contraseña solo se almacena una vez
        //Supondremos que ya han sido emparejados pues es un prototipo de muestra
    }

    public void simulacion(View view) {
        LinearLayout simulacion = (LinearLayout) findViewById(R.id.Simulacion);
        LinearLayout menu = (LinearLayout) findViewById(R.id.Menu);
        simulacion.setVisibility(View.VISIBLE);
        menu.setVisibility(View.GONE);
    }

    public void simulacionAtras(View view) {
        //Boton de atras
        LinearLayout simulacion = (LinearLayout) findViewById(R.id.Simulacion);
        LinearLayout menu = (LinearLayout) findViewById(R.id.Menu);
        simulacion.setVisibility(View.GONE);
        menu.setVisibility(View.VISIBLE);
    }

    public void enviarSimulacion(View view) {
        //Envia un ejemplo de pago al wereable
        Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        TextView txt_importe = (TextView) findViewById(R.id.text_importe);
        TextView txt_concepto = (TextView) findViewById(R.id.text_concepto);
        TextView txt_fecha = (TextView) findViewById(R.id.text_fecha);
        TextView txt_id = (TextView) findViewById(R.id.text_id);
        TextView txt_emisor = (TextView) findViewById(R.id.text_emisor);
        Pago p = new Pago(txt_importe.getText().toString(), txt_concepto.getText().toString(), txt_fecha.getText().toString(), txt_emisor.getText().toString(), txt_id.getText().toString());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(p);
            yourBytes = bos.toByteArray();
        }catch(IOException ioe){

        }finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        Toast.makeText(getApplicationContext(),"Enviado",Toast.LENGTH_SHORT);
        sendMessageToCompanion(NOTIFICAR,yourBytes);
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

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        //Se debera comprobar primero si el dispositivo esta incluido en la lista de wereables permitidos
        //para confirmar pagos, en caso de que no sea asi no se le responde.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(messageEvent.getPath().equals(PAGO_CONFIRMADO)){
                    Toast.makeText(getApplicationContext(),"Pago Confirmado",Toast.LENGTH_SHORT).show();
                }else if(messageEvent.getPath().equals(PAGO_CANCELADO)){
                    Toast.makeText(getApplicationContext(),"Pago Cancelado",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void salir(View view){
        //salir de la aplicacion
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect to Google Api Client with error code "
                + connectionResult.getErrorCode());
    }

    public void sendMessageToCompanion(final String p, final byte[] b) {
        //En este metod se tendra que tener en cuenta que el dispositivo esta emparejado
        //Para evitar que terceros confirmen pagos que no has realizado
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        for (final Node node : getConnectedNodesResult.getNodes()) {
                            Wearable.MessageApi.sendMessage(
                                    mGoogleApiClient, node.getId(), p, b)
                                    .setResultCallback(getSendMessageResultCallback());
                        }
                    }
                });
        Toast.makeText(getApplicationContext(),"Informacion enviada ",Toast.LENGTH_SHORT).show();
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
