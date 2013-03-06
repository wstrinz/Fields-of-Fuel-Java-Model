scriptloc = File.expand_path(File.dirname(__FILE__))
$CLASSPATH << "#{scriptloc}"
$CLASSPATH << "#{scriptloc}/json-simple-1.1.1.jar"
$CLASSPATH << "#{scriptloc}/akka-actor_2.10-2.1.1.jar"
$CLASSPATH << "#{scriptloc}/kosomodel/target/kosomodel-0.0.1-SNAPSHOT"

require 'java'
require 'redis'
require 'uri'
require 'json-simple-1.1.1.jar'
# require 'FoFModel.jar'
require 'kosomodel/target/kosomodel-0.0.1-SNAPSHOT.jar'
require 'akka_helper'

# require 'active_support/core_ext/numeric'

# require_relative 'json-simple-1.1.1.jar'


java_import "com.biofuels.fof.kosomodel.Handler"
java_import "com.biofuels.fof.kosomodel.EventMessage"
java_import "com.biofuels.fof.kosomodel.ActorSystemHelper"


include AkkaHelper

class ServerWrapper

  def open_pipes(mode)
    scriptloc = File.expand_path(File.dirname(__FILE__))
    if mode=="pipe"
      puts 'server opening pipes'
      puts @rpipe = open(File.join(scriptloc, "../pipes/rubypipe"),'r+')
      puts @wpipe = open(File.join(scriptloc, "../pipes/javapipe"),'w+')
    else
      puts 'connecting to redis server'
      @uri = URI.parse("redis://redistogo:1f736fa2a27319dc45b7ebb470e04bbe@dory.redistogo.com:10177/")
      @red = Redis.new(:host => @uri.host, :port => @uri.port, :password => @uri.password)
    end
    # puts "connected to redis #{@red}"
    # @event_handler = EventHandler.new
  end

  def write_pipe(msg)
    @wpipe.puts(msg)
    @wpipe.flush
  end

  def read_pipe
    @rpipe.gets
  end

  def watch(mode)
    open_pipes(mode)
    loop do
      # puts str
      #   write_pipe(msg)
      if mode=="pipe"
        str = read_pipe
      else
        str = read_queue
      end
      puts "handling #{str}"
      @handler.tell(EventMessage.new(str))

      # Future future = ask(counter, "get", 5000);

      # future.onSuccess(OnSuccess.new() {
      #       def onSuccess(result) {
      #           puts("handled #{result}")
      #           # System.out.println("Count is " + count);
      #       }
      #   }, @system.dispatcher());

      # @event_handler.handle(str).each do |msg|
      #   if mode=="pipe"
      #     write_pipe(msg)
      #   else
      #     # write_queue(msg)
      #     @listener.tell(EventMessage.new(msg))
      #   end
      # end
    end
  end

  def write_queue(msg)
    @red.lpush("fromJava",msg)
  end

  def read_queue
    ret = nil
    until ret
      begin
        ret = @red.brpop("toJava")
      rescue
        puts "couldn't connect to redis, retrying"
        #uri = URI.parse("redis://redistogo:1f736fa2a27319dc45b7ebb470e04bbe@dory.redistogo.com:10177/")
        @red = Redis.new(:host => @uri.host, :port => @uri.port, :password => @uri.password)
      end
      # unless ret
      #   puts "timeout, retrying"
      # end
    end
    ret[1] #
  end

  def sleepmode
    while (!(poppy = @red.bpop))
      sleep (60)
    end
  end

  def do_akka
    jside = ActorSystemHelper.new
    @system = ActorSystem.create("Biofuels")
    # pro = Props.new(HandlerActor)
    @handler = jside.makenew(@system, Handler, "handler")  # = system.actorOf(pro, "counter")
    @listener = @system.actorOf(Props.new(ServerListener), "listener")
    @listener.tell(ConnectMessage.new(URI.parse("redis://redistogo:1f736fa2a27319dc45b7ebb470e04bbe@dory.redistogo.com:10177/")))
    @handler.tell(@listener)
    watch('redis')
  end

end
