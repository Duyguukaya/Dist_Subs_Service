import dist_servers.SubscriberOuterClass.*;

import java.io.*;
import java.util.List;

public class Client {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5001;

    public static void main(String[] args) {
        try {
            // İlk abone bilgileri
            //
            Subscriber subscriber = Subscriber.newBuilder()
                    .setDemand(Subscriber.Demand.SUBS)
                    .setID(1)
                    .setNameSurname("John Doe")
                    .setStartDate(System.currentTimeMillis())
                    .setLastAccessed(System.currentTimeMillis())
                    .addAllInterests(List.of("sports", "music"))
                    .setIsOnline(true)
                    .build();

            // Abonelik isteği gönder
            sendSubscriptionRequest(subscriber);
            System.out.println("Client1: Subscription request sent.");

            // Sunucudan gelen yanıtı al
            receiveServerResponse();

            // Abonelikten çıkma işlemi
            subscriber = Subscriber.newBuilder()
                    .setDemand(Subscriber.Demand.DEL)
                    .setID(1)
                    .build();
            sendSubscriptionRequest(subscriber);
            System.out.println("Client1: Unsubscribe request sent.");
            receiveServerResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendSubscriptionRequest(Subscriber subscriber) throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            OutputStream output = socket.getOutputStream();
            subscriber.writeDelimitedTo(output);
            output.flush();
        }
    }

    private static void receiveServerResponse() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            InputStream input = socket.getInputStream();
            dist_servers.Message response = dist_servers.Message.parseDelimitedFrom(input);
            if (response != null) {
                System.out.println("Client1: Server response: " + response.getResponse());
            }
        }
    }
}
