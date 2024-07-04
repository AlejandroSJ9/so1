import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que representa una barbería donde se sincronizan las acciones del barbero, clientes y cajero.
 */
public class Barberia {
    private final int maxCapacidad;
    private final int numSofas;
    private final int numSillasBarbero;
    private final Queue<Cliente> salaEspera;
    private final Lock lock;
    private final Condition sofaDisponible;
    private final Condition sillaBarberoDisponible;
    private final Condition clienteListo;
    private final Condition corteTerminado;
    private final Condition pagoRealizado;
    private final Condition reciboEntregado;
    private final InterfazBarberia interfaz;

    private int sofasLibres;
    private int sillasBarberoLibres;
    private boolean barberoDurmiendo;

    /**
     * Constructor de la clase Barberia.
     *
     * @param maxCapacidad La capacidad máxima de la barbería.
     * @param numSofas El número de sofás disponibles en la barbería.
     * @param numSillasBarbero El número de sillas de barbero disponibles.
     * @param interfaz La interfaz gráfica para mostrar los eventos.
     */
    public Barberia(int maxCapacidad, int numSofas, int numSillasBarbero, InterfazBarberia interfaz) {
        this.maxCapacidad = maxCapacidad;
        this.numSofas = numSofas;
        this.numSillasBarbero = numSillasBarbero;
        this.salaEspera = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.sofaDisponible = lock.newCondition();
        this.sillaBarberoDisponible = lock.newCondition();
        this.clienteListo = lock.newCondition();
        this.corteTerminado = lock.newCondition();
        this.pagoRealizado = lock.newCondition();
        this.reciboEntregado = lock.newCondition();
        this.sofasLibres = numSofas;
        this.sillasBarberoLibres = numSillasBarbero;
        this.barberoDurmiendo = true;
        this.interfaz = interfaz;
    }

    /**
     * Método que gestiona la llegada de un cliente a la barbería.
     *
     * @param cliente El cliente que llega.
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public void llegaCliente(Cliente cliente) throws InterruptedException {
        lock.lock();
        try {
            if (salaEspera.size() == maxCapacidad) {
                interfaz.log("Cliente " + cliente.getId() + " se va porque la barbería está llena.");
                return;
            }
            salaEspera.add(cliente);
            interfaz.log("Cliente " + cliente.getId() + " llega y espera.");
            while (sofasLibres == 0) {
                sofaDisponible.await();
            }
            sofasLibres--;
            interfaz.log("Cliente " + cliente.getId() + " se sienta en el sofá.");
            clienteListo.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método que gestiona el proceso de cortar el cabello de un cliente.
     *
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public void cortarCabello() throws InterruptedException {
        lock.lock();
        try {
            while (salaEspera.isEmpty()) {
                interfaz.log("El barbero está durmiendo.");
                barberoDurmiendo = true;
                clienteListo.await();
            }
            Cliente cliente = salaEspera.poll();
            while (sillasBarberoLibres == 0) {
                sillaBarberoDisponible.await();
            }
            sillasBarberoLibres--;
            sofasLibres++;
            sofaDisponible.signal();
            interfaz.log("El barbero está cortando el cabello del cliente " + cliente.getId());
            barberoDurmiendo = false;
            Thread.sleep(5000); // Simula el tiempo de corte de cabello
            interfaz.log("El barbero ha terminado de cortar el cabello del cliente " + cliente.getId());
            cliente.setEsperandoPago(true);
            corteTerminado.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método que gestiona el pago del cliente.
     *
     * @param cliente El cliente que va a pagar.
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public void pagar(Cliente cliente) throws InterruptedException {
        lock.lock();
        try {
            while (!cliente.isEsperandoPago()) {
                corteTerminado.await();
            }
            interfaz.log("Cliente " + cliente.getId() + " paga.");
            cliente.setEsperandoRecibo(true);
            pagoRealizado.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método que gestiona la entrega del recibo al cliente.
     *
     * @param cliente El cliente que recibe el recibo.
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public void entregarRecibo(Cliente cliente) throws InterruptedException {
        lock.lock();
        try {
            while (!cliente.isEsperandoRecibo()) {
                pagoRealizado.await();
            }
            interfaz.log("El cajero entrega el recibo al cliente " + cliente.getId());
            cliente.setEsperando(false);
            sillasBarberoLibres++;
            sillaBarberoDisponible.signal();
        } finally {
            lock.unlock();
        }
    }
}
