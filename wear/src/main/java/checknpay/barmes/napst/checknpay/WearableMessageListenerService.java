/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package checknpay.barmes.napst.checknpay;

import android.content.Intent;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;


public class WearableMessageListenerService extends WearableListenerService {
    private static final String NOTIFICAR = "/notificar";
    private static final String EMPARENTAR = "/emparentar";
    @Override
    public void onMessageReceived(MessageEvent event) {
        if(event.getPath().equals(NOTIFICAR)){
            ByteArrayInputStream bis = new ByteArrayInputStream(event.getData());
            ObjectInput in = null;
            Pago p = null;
            try {
                in = new ObjectInputStream(bis);
                p = (Pago)in.readObject();
            }catch(IOException ioe){

            }catch(ClassNotFoundException cn){

            }finally {
                try {
                    bis.close();
                } catch (IOException ex) {
                    // ignore close exception
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }
            }
            if(check(p)){
                //Si el pago ya se ha realizado no se muestra nada
            }else{
                WearActivity.recivedintent = true;
                Intent startIntent = new Intent(this, WearActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent.putExtra("concepto",p.getConcepto());
                startIntent.putExtra("importe",p.getImporte());
                startIntent.putExtra("emisor",p.getEmisor());
                startIntent.putExtra("fecha",p.getFecha());
                almacenarPago(p);
                startActivity(startIntent);
            }
        }else if(event.getPath().equals(EMPARENTAR)){
            //llama a un metodo que se encarga de la comunicacion
            //para emparentar el wereable con el smartphone
        }
    }
    private boolean check(Pago p){
        //La clase comprueba si el pago ya ha sido realizado de una base de datos temporal que posee
        //En caso de haberse realizado no muestra nada
        //Como es un prototipo siempre devolvera ture
        return false;
    }
    private void almacenarPago(Pago p){
        //Almacena el pago en a base de datos temporal
    }
}
