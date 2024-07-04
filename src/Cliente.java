/**
 * Clase que representa a un cliente de la barbería.
 */
public class Cliente implements Runnable {
    private final Barberia barberia;
    private final int id;
    private boolean esperando;
    private boolean esperandoPago;
    private boolean esperandoRecibo;

    /**
     * Constructor de la clase Cliente.
     *
     * @param barberia La barbería a la que pertenece el cliente.
     * @param id El identificador del cliente.
     */
    public Cliente(Barberia barberia, int id) {
        this.barberia = barberia;
        this.id = id;
        this.esperando = true;
        this.esperandoPago = false;
        this.esperandoRecibo = false;
    }

    public int getId() {
        return id;
    }

    public boolean isEsperando() {
        return esperando;
    }

    public void setEsperando(boolean esperando) {
        this.esperando = esperando;
    }

    public boolean isEsperandoPago() {
        return esperandoPago;
    }

    public void setEsperandoPago(boolean esperandoPago) {
        this.esperandoPago = esperandoPago;
    }

    public boolean isEsperandoRecibo() {
        return esperandoRecibo;
    }

    public void setEsperandoRecibo(boolean esperandoRecibo) {
        this.esperandoRecibo = esperandoRecibo;
    }

    /**
     * Método que ejecuta la lógica del cliente al llegar, pagar y recibir el recibo.
     */
    @Override
    public void run() {
        try {
            barberia.llegaCliente(this);
            // Espera a que termine el corte de cabello
            barberia.pagar(this);
            barberia.entregarRecibo(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
