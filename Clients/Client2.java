import dist_servers.SubscriberOuterClass.*;

import java.io.*;
import java.util.List;

public class Client2 {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5001;

    public static void main(String[] args) {
        try {
            // İkinci abone bilgileri
            Subscriber subscriber = Subscriber.newBuilder()
                    .setDemand(Subscriber.Demand.SUBS)
                    .setID(2)
                    .setNameSurname("Alice Smith")
                    .setStartDate(System.currentTimeMillis())
                    .setLastAccessed(System.currentTimeMillis())
                    .addAllInterests(List.of("travel", "movies"))
                    .setIsOnline(true)
                    .build();

            // Abonelik isteği gönder
            sendSubscriptionRequest(subscriber);
            System.out.println("Client2: Subscription request sent.");

            // Sunucudan gelen yanıtı al
            receiveServerResponse();

            // Abonelikten çıkma işlemi
            subscriber = Subscriber.newBuilder()
                    .setDemand(Subscriber.Demand.DEL)
                    .setID(2)
                    .build();
            sendSubscriptionRequest(subscriber);
            System.out.println("Client2: Unsubscribe request sent.");
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
                System.out.println("Client2: Server response: " + response.getResponse());
            }
        }
    }
}
