module AkkaHelper
  class Base < UntypedActor
    def self.create(*args)
      self.new(*args)
    end

    def self.build(*args)
      return Akka::UntypedActor.actorOf(self)  if args.empty?
      Akka::UntypedActor.actorOf { self.new *args }
    end

    def self.spawn(*args)
      build(*args).start
    end
  end

  class ConnectMessage
    include Serializable

    attr_reader :uri

    def initialize(uri)
      @uri = uri
    end
  end

  # class EventMessage
  #   include Serializable

  #   attr_reader :event

  #   def initialize(event)
  #     @event = event
  #   end
  # end

  class ServerListener < Base
    def onReceive(msg)
      if msg.is_a? ConnectMessage
        uri = msg.uri
        @red = Redis.new(:host => uri.host, :port => uri.port, :password => uri.password)
      elsif msg.is_a? EventMessage
        if @red
          @red.lpush("fromJava",msg.message)
        else
          puts "Redis sever not specified"
        end
      else
      end
    end
  end
end