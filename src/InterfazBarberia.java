import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfazBarberia extends JFrame {
    private JTextArea textArea;
    private JTextField maxCapacidadField;
    private JTextField numSofasField;
    private JTextField numSillasBarberoField;
    private JTextField numClientesField;
    private Barberia barberia;

    public InterfazBarberia() {
        setupUI();
    }

    private void setupUI() {
        setTitle("Barbería del Barbero Dormilón");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Capacidad máxima de la barbería:"));
        maxCapacidadField = new JTextField("10");
        inputPanel.add(maxCapacidadField);

        inputPanel.add(new JLabel("Número de sofás:"));
        numSofasField = new JTextField("5");
        inputPanel.add(numSofasField);

        inputPanel.add(new JLabel("Número de sillas de barbero:"));
        numSillasBarberoField = new JTextField("1");
        inputPanel.add(numSillasBarberoField);

        inputPanel.add(new JLabel("Número de clientes:"));
        numClientesField = new JTextField("10");
        inputPanel.add(numClientesField);

        JButton startButton = new JButton("Iniciar Simulación");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
        inputPanel.add(startButton);

        add(inputPanel, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(scrollPane, BorderLayout.CENTER);
    }

    public synchronized void log(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    private void startSimulation() {
        int maxCapacidad = Integer.parseInt(maxCapacidadField.getText());
        int numSofas = Integer.parseInt(numSofasField.getText());
        int numSillasBarbero = Integer.parseInt(numSillasBarberoField.getText());
        int numClientes = Integer.parseInt(numClientesField.getText());

        barberia = new Barberia(maxCapacidad, numSofas, numSillasBarbero, this);

        Barbero barbero = new Barbero(barberia);

        Thread hiloBarbero = new Thread(barbero);
        hiloBarbero.start();

        for (int i = 1; i <= numClientes; i++) {
            Cliente cliente = new Cliente(barberia, i);
            Thread hiloCliente = new Thread(cliente);
            hiloCliente.start();
            try {
                Thread.sleep((int) (Math.random() * 1000)); // Simula llegada de clientes aleatoria
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazBarberia app = new InterfazBarberia();
            app.setVisible(true);
        });
    }
}
