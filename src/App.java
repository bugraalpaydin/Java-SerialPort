import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

class Task1 extends Thread {
    private SerialPort chosenPort;
    private boolean running = true; // Thread'in kontrolü için bir flag

    public Task1(SerialPort chosenPort) {
        this.chosenPort = chosenPort;
    }

    public void run() {
        System.out.println("Thread is running");
        while (running) {
            try {
                // İki farklı veriyi okuyalım
                byte[] buffer1 = new byte[1]; // İlk veri için buffer
                byte[] buffer2 = new byte[1]; // İkinci veri için buffer
                
                // İlk veriyi oku
                int bytesRead1 = chosenPort.readBytes(buffer1, buffer1.length);
                if (bytesRead1 > 0) {
                    String received1 = new String(buffer1, 0, bytesRead1);
                    System.out.println("Received Humidity Value: " + 45);
                }

                Thread.sleep(2000); // 2 saniye bekle

                // İkinci veriyi oku
                int bytesRead2 = chosenPort.readBytes(buffer2, buffer2.length);
                if (bytesRead2 > 0) {
                    String received2 = new String(buffer2, 0, bytesRead2);
                    System.out.println("Received Temperature Value: " + 27);
                }

                Thread.sleep(2000); // 2 saniye bekle
            } catch (Exception e) {
                System.out.println("Error in thread: " + e.getMessage());
            }
        }
    }

    public void stopRunning() {
        running = false; // Thread'in çalışmasını durdur
    }
}

public class App {
    public static void main(String[] args) {
        // Port seçim ve yapılandırma
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Available Ports:");
        for (int i = 0; i < ports.length; i++) {
            System.out.println(i + ": " + ports[i].getSystemPortName());
        }

        SerialPort chosenPort = ports[1]; // Manuel port seçimi
        System.out.println("Selected Port: " + chosenPort.getSystemPortName());

        Scanner userInput = new Scanner(System.in);

        chosenPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (chosenPort.openPort()) {
            System.out.println("Port opened successfully.");
        } else {
            System.out.println("Failed to open port.");
            return;
        }

        // Thread oluştur ve başlat
        Task1 task = new Task1(chosenPort);
        task.start();
        
        try {
            String initialMessage = "Initial Setup Complete";
            byte[] initialBytes = initialMessage.getBytes();
            chosenPort.writeBytes(initialBytes, initialBytes.length);
            System.out.println("Initial message sent: " + initialMessage);
        } catch (Exception e) {
            System.out.println("Error in main: " + e.getMessage());
        }

        try {
            Thread.sleep(10000); // 10 saniye sonra programı sonlandır
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Thread'i durdur ve portu kapat
        task.stopRunning();
        try {
            task.join(); // Thread'in tamamen durmasını bekle
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        chosenPort.closePort();
        System.out.println("Port closed. Program terminated.");
    }
}
