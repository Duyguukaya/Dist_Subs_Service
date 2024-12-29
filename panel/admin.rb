require 'socket'
require 'google/protobuf'
require_relative 'dist_servers_pb'


module DistServers
  include Google::Protobuf
end

class AdminClient
  ADMIN_PORTS = [7001, 7002, 7003] 
  SERVERS = []

  def initialize
    read_config
    connect_to_servers
    send_start_command
    handle_capacity_requests
  end

  def read_config
    config_data = File.read('dist_subs.conf').split('=').map(&:strip)
    @fault_tolerance_level = config_data[1].to_i
  end

  def connect_to_servers
    ADMIN_PORTS.each do |port|
      begin
        socket = TCPSocket.new('localhost', port)
        SERVERS << socket
        puts "Port #{port} ile bağlantı kuruldu."
      rescue => e
        puts "Port #{port} ile bağlantı kurulamadı: #{e.message}"
      end
    end
  end

  def send_start_command
    config = DistServers::Configuration.new(
      fault_tolerance_level: @fault_tolerance_level,
      method: DistServers::Configuration::Method::STRT
    )

    SERVERS.each do |socket|
      socket.write(config.to_proto)

      response_data = socket.read(1024)
      response = DistServers::Message.decode(response_data)
      if response.response == DistServers::Message::Response::YEP
        puts "Sunucu başarıyla başlatıldı."
      else
        puts "Sunucu başlatma başarısız."
      end
    end
  end

  def handle_capacity_requests
    loop do
      sleep 5
      send_capacity_request
    end
  end

  def send_capacity_request
    capacity_request = DistServers::Capacity.new(
      serverXStatus: 0,
      timestamp: Time.now.to_i
    )

    SERVERS.each_with_index do |socket, index|
      capacity_request.serverXStatus = index + 1 
      socket.write(capacity_request.to_proto)

      response_data = socket.read(1024)
      response = DistServers::Capacity.decode(response_data)
      puts "Server #{index + 1} kapasite: #{response.serverXStatus} zaman: #{response.timestamp}"
    end
  end

  def close_connections
    SERVERS.each(&:close)
  end
end

admin_client = AdminClient.new

trap("INT") do
  admin_client.close_connections
  puts "Bağlantılar kapatıldı. Çıkılıyor..."
  exit
end

