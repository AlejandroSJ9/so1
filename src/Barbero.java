/**
 * Clase que representa al barbero de la barbería.
 */
public class Barbero implements Runnable {
    private final Barberia barberia;

    /**
     * Constructor de la clase Barbero.
     *
     * @param barberia La barbería a la que pertenece el barbero.
     */
    public Barbero(Barberia barberia) {
        this.barberia = barberia;
    }

    /**
     * Método que ejecuta la lógica del barbero para cortar el cabello.
     */
    @Override
    public void run() {
        try {
            while (true) {
                barberia.cortarCabello();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
