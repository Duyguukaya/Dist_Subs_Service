import java.io.*;
import java.net.*;
import dist_servers.MessageOuterClass.*;
import dist_servers.CapacityOuterClass.*;
import dist_servers.SubscriberOuterClass.*;

public class DistributedServerHandler {

    private final int port;
    private final String serverName;
    private int capacityStatus = 1000;

    public DistributedServerHandler(int port, String serverName) {
        this.port = port;
        this.serverName = serverName;
    }

    public static void main(String[] args) {
        // Sunucuları başlat
        new DistributedServerHandler(5001, "Server1").start();
        new DistributedServerHandler(5002, "Server2").start();
        new DistributedServerHandler(5003, "Server3").start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(serverName + " is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println(serverName + " failed to start: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {

            // Mesajı oku
            Message incomingMessage = Message.parseDelimitedFrom(input);

            if (incomingMessage != null) {
                switch (incomingMessage.getDemand()) {
                    case STRT:
                        handleStartRequest(output);
                        break;

                    case CPCTY:
                        handleCapacityRequest(output);
                        break;

                    default:
                        System.out.println(serverName + " received an unknown demand.");
                        break;
                }
            } else {
                // Subscriber taleplerini işle
                Subscriber subscriber = Subscriber.parseDelimitedFrom(input);
                if (subscriber != null) {
                    handleSubscriberRequest(subscriber, output);
                }
            }

        } catch (IOException e) {
            System.err.println(serverName + " encountered an error: " + e.getMessage());
        }
    }

    private void handleStartRequest(OutputStream output) throws IOException {
        System.out.println(serverName + " received a START request.");

        // Yanıt oluştur
        Message response = Message.newBuilder()
                .setDemand(Message.Demand.STRT)
                .setResponse(Message.Response.YEP)
                .build();

        response.writeDelimitedTo(output);
        System.out.println(serverName + " sent response: " + response.getResponse());
    }

    private void handleCapacityRequest(OutputStream output) throws IOException {
        System.out.println(serverName + " received a CAPACITY request.");

        // Kapasite yanıtını oluştur
        long timestamp = System.currentTimeMillis() / 1000;
        Capacity capacity = Capacity.newBuilder()
                .setServerXStatus(capacityStatus)
                .setTimestamp(timestamp)
                .build();

        capacity.writeDelimitedTo(output);
        System.out.println(serverName + " sent capacity response: " + capacityStatus + " at " + timestamp);
    }

    private void handleSubscriberRequest(Subscriber subscriber, OutputStream output) throws IOException {
        switch (subscriber.getDemand()) {
            case SUBS:
                System.out.println(serverName + " received a SUBSCRIBE request for: " + subscriber.getNameSurname());
                break;

            case DEL:
                System.out.println(serverName + " received a DELETE request for ID: " + subscriber.getId());
                break;

            default:
                System.out.println(serverName + " received an unknown subscriber demand.");
                break;
        }

        // Yanıt gönder
        Message response = Message.newBuilder()
                .setDemand(Message.Demand.STRT) // Sabit bir yanıt olarak ayarlandı
                .setResponse(Message.Response.YEP)
                .build();

        response.writeDelimitedTo(output);
        System.out.println(serverName + " sent response: " + response.getResponse());
    }
}
