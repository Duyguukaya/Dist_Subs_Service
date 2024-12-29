import java.io.*;
import java.net.*;
import dist_servers.MessageOuterClass.*;
import dist_servers.SubscriberOuterClass.*;

public class ClientHandler {

    private static final String[] SERVER_HOSTS = {"localhost", "localhost", "localhost"};
    private static final int[] SERVER_PORTS = {5001, 5002, 5003};

    public static void main(String[] args) {
        try {
            ClientHandler client = new ClientHandler();

            // Örnek bir abone oluştur ve sunucuya gönder
            Subscriber subscriber = Subscriber.newBuilder()
                    .setDemand(Subscriber.Demand.SUBS)
                    .setNameSurname("Jane Doe")
                    .setId(12)
                    .build();
            client.sendSubscriberRequest(subscriber);

            // Abonelik silme talebi gönder
            Subscriber deleteRequest = Subscriber.newBuilder()
                    .setDemand(Subscriber.Demand.DEL)
                    .setId(12) // Silinecek abonenin ID'si
                    .build();
            client.sendSubscriberRequest(deleteRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Abone talebi gönderen metot
    public void sendSubscriberRequest(Subscriber subscriber) {
        for (int i = 0; i < SERVER_HOSTS.length; i++) {
            try (Socket socket = new Socket(SERVER_HOSTS[i], SERVER_PORTS[i])) {
                System.out.println("Sending subscriber request to Server" + (i + 1) + "...");

                // Abone isteğini gönder
                sendSubscriber(socket, subscriber);

                // Sunucudan yanıt bekle
                Message response = receiveMessage(socket);
                System.out.println("Response from Server" + (i + 1) + ": " + response.getResponse());
            } catch (IOException e) {
                System.err.println("Failed to send subscriber request to Server" + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    // Abone nesnesini sunucuya gönder
    private void sendSubscriber(Socket socket, Subscriber subscriber) throws IOException {
        OutputStream output = socket.getOutputStream();
        subscriber.writeDelimitedTo(output);
        output.flush();
    }

    // Mesaj almak için yardımcı metot
    private Message receiveMessage(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        return Message.parseDelimitedFrom(input);
    }
}
