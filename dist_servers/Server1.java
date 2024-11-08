import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import com.google.protobuf.*;

public class Server1 {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5001);
            System.out.println("Server1 is running...");

            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                int messageLength = in.readInt();
                byte[] messageBytes = new byte[messageLength];
                in.readFully(messageBytes);
                Message.MessageProto message = Message.MessageProto.parseFrom(messageBytes);

                Message.MessageProto response;
                if (message.getDemand() == Message.MessageProto.Demand.STRT) {
                    System.out.println("Received STRT demand");
                    response = Message.MessageProto.newBuilder()
                            .setDemand(Message.MessageProto.Demand.STRT)
                            .setResponse(Message.MessageProto.Response.YEP)
                            .build();
                    sendResponse(socket, response);
                } else if (message.getDemand() == Message.MessageProto.Demand.CPCTY) {
                    System.out.println("Received CPCTY demand");
                    Capacity.CapacityProto capacity = Capacity.CapacityProto.newBuilder()
                            .setServerXStatus(1000)
                            .setTimestamp(System.currentTimeMillis() / 1000L)
                            .build();
                    sendCapacity(socket, capacity);
                }
                TimeUnit.SECONDS.sleep(5); // 5 saniyede bir kapasite durumu gönderme
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendResponse(Socket socket, Message.MessageProto response) throws IOException {
        byte[] responseBytes = response.toByteArray();
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(responseBytes.length);
        out.write(responseBytes);
        out.flush();
    }

    public static void sendCapacity(Socket socket, Capacity.CapacityProto capacity) throws IOException {
        byte[] capacityBytes = capacity.toByteArray();
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(capacityBytes.length);
        out.write(capacityBytes);
        out.flush();
    }
}







              
