import socket
import time
import threading
from datetime import datetime
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
import dist_servers_pb2  # Protobuf tanımları


server_data = {
    1: {"timestamps": [], "statuses": []},
    2: {"timestamps": [], "statuses": []},
    3: {"timestamps": [], "statuses": []},
}


server_colors = {1: "red", 2: "blue", 3: "green"}


def plot_capacity():
    fig, ax = plt.subplots()
    lines = {}

    for server_id, color in server_colors.items():
        (lines[server_id],) = ax.plot([], [], label=f"Server {server_id}", color=color)

    ax.set_xlabel("Timestamp")
    ax.set_ylabel("Capacity")
    ax.legend()
    ax.grid()

    def update(frame):
        for server_id in server_data:
            timestamps = [
                datetime.fromtimestamp(ts).strftime("%H:%M:%S")
                for ts in server_data[server_id]["timestamps"]
            ]
            statuses = server_data[server_id]["statuses"]

            if timestamps and statuses:
                lines[server_id].set_data(timestamps, statuses)
                ax.set_xlim(timestamps[0], timestamps[-1])
                ax.set_ylim(0, max(max(statuses) + 10, 100))

        return lines.values()

    ani = FuncAnimation(fig, update, interval=1000)
    plt.show()


def listen_for_capacity(port):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
        server_socket.bind(("localhost", port))
        server_socket.listen()

        print(f"Plotter {port} numaralı portta bekliyor...")
        while True:
            client_socket, _ = server_socket.accept()
            with client_socket:
                print(f"Sunucudan bağlantı alındı. Port: {port}")
                while True:
                    data = client_socket.recv(1024)
                    if not data:
                        break
                    capacity = dist_servers_pb2.Capacity()
                    capacity.ParseFromString(data)

                    server_id = capacity.serverXStatus
                    timestamp = capacity.timestamp


                    server_data[server_id]["timestamps"].append(timestamp)
                    server_data[server_id]["statuses"].append(server_id)
                    print(
                        f"Server {server_id}: Capacity={server_id}, Timestamp={timestamp}"
                    )


if _name_ == "_main_":
  
    plot_thread = threading.Thread(target=plot_capacity)
    plot_thread.daemon = True
    plot_thread.start()

  
    listen_ports = [8001, 8002, 8003]

  
    for port in listen_ports:
        threading.Thread(target=listen_for_capacity, args=(port,), daemon=True).start()

    while True:
        time.sleep(1)
