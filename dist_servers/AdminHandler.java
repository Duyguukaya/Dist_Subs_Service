import dist_servers.MessageOuterClass.*;
import dist_servers.CapacityOuterClass.*;
import dist_servers.ConfigurationOuterClass.*;
import dist_servers.SubscriberOuterClass.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class AdminHandler {

    private static final String[] SERVER_HOSTS = {"localhost", "localhost", "localhost"};
    private static final int[] SERVER_PORTS = {5001, 5002, 5003};

    public static void main(String[] args) {
        try {
            AdminHandler adminHandler = new AdminHandler();
            // Configuration ayarlarını oku
            Configuration config = adminHandler.loadConfiguration();

            // Sunucuları başlatma komutu gönder
            adminHandler.sendStartCommandToServers(config);

            // Sunuculardan gelen yanıtları kontrol et
            adminHandler.checkServerResponses();

            // Sunuculardan kapasite bilgilerini periyodik olarak al
            adminHandler.getCapacityStatusFromServers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Configuration loadConfiguration() {
        // Burada config dosyasını okuyup Configuration nesnesini oluşturuyoruz
        try (BufferedReader reader = new BufferedReader(new FileReader("dist_subs.conf"))) {
            String line;
            int faultToleranceLevel = 1;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("fault_tolerance_level")) {
                    faultToleranceLevel = Integer.parseInt(line.split("=")[1].trim());
                }
            }

            // Configuration nesnesini oluştur
            return Configuration.newBuilder()
                    .setFaultToleranceLevel(faultToleranceLevel)
                    .setMethod(Configuration.Method.STRT)
                    .build();
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            return null;
        }
    }

    private void sendStartCommandToServers(Configuration config) {
        for (int i = 0; i < SERVER_HOSTS.length; i++) {
            try (Socket socket = new Socket(SERVER_HOSTS[i], SERVER_PORTS[i])) {
                System.out.println("Sending start command to Server" + (i + 1));

                // START komutunu gönder
                sendMessage(socket, Message.newBuilder()
                        .setDemand(Message.Demand.STRT)
                        .setResponse(Message.Response.YEP) // Başlatma başarılı yanıtı
                        .build());

            } catch (IOException e) {
                System.err.println("Failed to send start command to Server" + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    private void checkServerResponses() {
        for (int i = 0; i < SERVER_HOSTS.length; i++) {
            try (Socket socket = new Socket(SERVER_HOSTS[i], SERVER_PORTS[i])) {
                // Sunucudan gelen yanıtı kontrol et
                Message response = receiveMessage(socket);

                if (response != null && response.getResponse() == Message.Response.YEP) {
                    System.out.println("Server" + (i + 1) + " is up and running.");
                } else {
                    System.out.println("Server" + (i + 1) + " is not responding correctly.");
                }

            } catch (IOException e) {
                System.err.println("Error checking response from Server" + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    private void getCapacityStatusFromServers() {
        for (int i = 0; i < SERVER_HOSTS.length; i++) {
            try (Socket socket = new Socket(SERVER_HOSTS[i], SERVER_PORTS[i])) {
                // Kapasite talebini gönder
                sendMessage(socket, Message.newBuilder()
                        .setDemand(Message.Demand.CPCTY)
                        .setResponse(Message.Response.YEP)
                        .build());

                // Sunucudan kapasite bilgisi al
                Capacity capacity = receiveCapacity(socket);
                if (capacity != null) {
                    System.out.println("Server" + (i + 1) + " capacity: " + capacity.getServerXStatus() +
                            " at timestamp: " + capacity.getTimestamp());
                }

                // 5 saniye bekleyin
                TimeUnit.SECONDS.sleep(5);
            } catch (IOException | InterruptedException e) {
                System.err.println("Error getting capacity from Server" + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    private void sendMessage(Socket socket, Message message) throws IOException {
        OutputStream output = socket.getOutputStream();
        message.writeDelimitedTo(output);
        output.flush();
    }

    private Message receiveMessage(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        return Message.parseDelimitedFrom(input);
    }

    private Capacity receiveCapacity(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        return Capacity.parseDelimitedFrom(input);
    }
}
