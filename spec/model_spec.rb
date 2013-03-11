require 'rspec'
require 'json'

  scriptloc = File.expand_path(File.dirname(__FILE__))
  $CLASSPATH << "#{scriptloc}/../*"
  $CLASSPATH << "#{scriptloc}/../akka/*"
  $CLASSPATH << "#{scriptloc}/../javaGame/kosomodel/target/kosomodel-0.0.1-SNAPSHOT"
  $CLASSPATH << "#{scriptloc}/../javaGame/json-simple-1.1.1.jar"

  require 'java'
  require "#{scriptloc}/../scala-library.jar"
  require "#{scriptloc}/../akka/config-1.0.0.jar"
  require "#{scriptloc}/../akka/akka-actor_2.10-2.1.1.jar"
  require "#{scriptloc}/../javaGame/kosomodel/target/kosomodel-0.0.1-SNAPSHOT.jar"
  require "#{scriptloc}/../javaGame/json-simple-1.1.1.jar"

  # require 'akka-project-in-java/src/main/java/akka/test/AkkaProjectInJava'
  # require 'JavaActor.jar'

  java_import 'java.io.Serializable'
  java_import 'akka.actor.UntypedActor'
  java_import 'akka.actor.ActorRef'
  java_import 'akka.actor.ActorSystem'
  java_import 'akka.actor.Props'
  java_import 'scala.concurrent.Future'
  java_import 'scala.concurrent.Future'
  java_import 'scala.concurrent.Await'
  java_import 'akka.util.Timeout'
  java_import 'scala.concurrent.duration.Duration'
  java_import 'akka.pattern.Patterns'
  java_import 'java.util.concurrent.TimeUnit'

  java_import "com.biofuels.fof.kosomodel.Handler"
  java_import "com.biofuels.fof.kosomodel.EventMessage"
  java_import "com.biofuels.fof.kosomodel.ActorSystemHelper"

  require 'test_messages'
  # require "#{scriptloc}/../kosomodel/target/kosomodel-0.0.1-SNAPSHOT.jar"

# require 'java_loader_rspec'
  # java_import 'AkkaProjectInJava'
  # java_import 'AkkaProjectInJava.Counter'

# include JavaLoaderRspec

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

class ModelWrapper < Base
  def onReceive(msg)
    if msg.is_a? String
      # puts "got a string: #{msg}"
      if msg == "hello?"
        getSender().tell("hihi")
      end
    elsif msg.is_a? TestMessages::ValidateRoomMessage
      @replyaddr = getSender()
      @timeout = Timeout.new(Duration.create(15, TimeUnit::SECONDS));
      msg.handler.tell(msg.message)
    elsif msg.is_a? EventMessage
      @replyaddr.tell(msg.message)
    end
  end
end

describe ModelWrapper do
  before(:each) do
    @system = ActorSystem.create("BiofuelsTest")
    @listener = @system.actorOf(Props.new(ModelWrapper), "listener")
    @timeout = Timeout.new(Duration.create(30, TimeUnit::SECONDS));
    jside = ActorSystemHelper.new
    @handler = jside.makenew(@system, Handler, "handler")  # = system.actorOf(pro, "counter")
    @handler.tell(@listener)
  end

  it "should be friendly and say hello" do
    future = Patterns.ask(@listener, "hello?", @timeout);
    result = Await.result(future, @timeout.duration());
    result.should == "hihi"
    # should be "hello"
  end

  it "should be able to ask ifa room is open" do
    future = Patterns.ask(@listener, TestMessages::ValidateRoomMessage.new("new room name", @handler), @timeout)
    result = Await.result(future, @timeout.duration())
    parsed = JSON.parse(result)
    parsed["result"].should == true
  end

  after(:each) do
    @system.shutdown
    @system.await_termination
  end
end