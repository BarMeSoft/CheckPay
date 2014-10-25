package checknpay.barmes.napst.checknpay;

import java.io.Serializable;


public class ConfirmacionPago implements Serializable{
    private static final long serialVersionUID = -5020027638752709758L;
    private String ID;
    private boolean accion;

    public ConfirmacionPago(String ID, boolean accion){
        this.ID= ID;
        this.accion = accion;
    }
    public String getID(){
        return this.ID;
    }
    public boolean getAccion(){
        return this.accion;
    }

}

