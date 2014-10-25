package checknpay.barmes.napst.checknpay;


import java.io.Serializable;


public class Pago implements Serializable{
    private static final long serialVersionUID = 618271523089195465L;
    private String importe;
    private String concepto;
    private String fecha;
    private String emisor;
    private String ID;

    public Pago(String importe,String concepto,String fecha,String emisor,String ID){
        this.importe=importe;
        this.concepto = concepto;
        this.fecha = fecha;
        this.emisor = emisor;
        this.ID = ID;
    }
    public String getImporte(){
        return this.importe;
    }
    public String getConcepto(){
        return this.concepto;
    }
    public String getFecha(){
        return this.fecha;
    }
    public String getEmisor(){
        return this.emisor;
    }
    public String getID(){
        return this.ID;
    }
}
