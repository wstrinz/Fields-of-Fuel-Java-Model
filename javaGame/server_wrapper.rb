require 'java'
require 'redis'
require 'uri'
# require 'active_support/core_ext/numeric'

# require_relative 'json-simple-1.1.1.jar'

scriptloc = File.expand_path(File.dirname(__FILE__))

$CLASSPATH << "#{scriptloc}s"
$CLASSPATH << "#{scriptloc}/json-simple-1.1.1.jar"
java_import "EventHandler"

class ServerWrapper


  def open_pipes(mode)
    scriptloc = File.expand_path(File.dirname(__FILE__))
    if mode=="pipe"
      puts 'server opening pipes'
      puts @rpipe = open(File.join(scriptloc, "../pipes/rubypipe"),'r+')
      puts @wpipe = open(File.join(scriptloc, "../pipes/javapipe"),'w+')
    else
      puts 'connecting to redis server'
      uri = URI.parse("redis://redistogo:1f736fa2a27319dc45b7ebb470e04bbe@dory.redistogo.com:10177/")
      @red = Redis.new(:host => uri.host, :port => uri.port, :password => uri.password)
    end
    # puts "connected to redis #{@red}"



    @event_handler = EventHandler.new
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

      @event_handler.handle(str).each do |msg|
        # puts "writing #{msg} back"
        if mode=="pipe"
          write_pipe(msg)
        else
          write_queue(msg)
        end
      end
    end
  end

  def write_queue(msg)
    @red.lpush("fromJava",msg)
  end

  def read_queue
    @red.brpop("toJava", 120)[1] #
  end

  def sleepmode
    while (!(poppy = @red.bpop))
      sleep (60)
    end
  end

end
