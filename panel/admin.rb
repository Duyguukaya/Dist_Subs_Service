require 'socket'
require 'google/protobuf'
require_relative 'dist_servers_pb'

class Configuration
  attr_reader :fault_tolerance_level, :method

  def initialize(fault_tolerance_level)
    @fault_tolerance_level = fault_tolerance_level
    @method = DistServers::Configuration::Method::STRT
  end

  def to_proto
    DistServers::Configuration.new(
      fault_tolerance_level: @fault_tolerance_level,
      method: @method
    )
  end
end


fault_tolerance_level = File.read("dist_subs.conf").split('=').last.strip.to_i
config = Configuration.new(fault_tolerance_level)

servers = [
  {name: 'Server1', port: 5001},
  {name: 'Server2', port: 5002},
  {name: 'Server3', port: 5003}
]

successful_servers = []

servers.each do |server|
  begin
    socket = TCPSocket.new('localhost', server[:port])

    start_message = DistServers::Message.new(
      demand: DistServers::Message::Demand::STRT,
      response: nil
    )
    socket.write(start_message.to_proto)

    response = DistServers::Message.decode(socket.read)
    if response.response == DistServers::Message::Response::YEP
      successful_servers << server
      puts "#{server[:name]} started successfully."
    else
      puts "#{server[:name]} failed to start."
    end
    socket.close
  rescue Errno::ECONNREFUSED
    puts "#{server[:name]} connection refused."
  end
end

loop do
  successful_servers.each do |server|
    begin
      socket = TCPSocket.new('localhost', server[:port])

      capacity_request = DistServers::Message.new(
        demand: DistServers::Message::Demand::CPCTY
      )
      socket.write(capacity_request.to_proto)
      
      capacity_response = DistServers::Capacity.decode(socket.read)
      puts "Capacity of #{server[:name]}: #{capacity_response.serverXStatus} at #{capacity_response.timestamp}"
      socket.close
    rescue Errno::ECONNREFUSED
      puts "#{server[:name]} capacity check connection refused."
    end
  end
  sleep 5
end
