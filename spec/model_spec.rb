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
    elsif msg.is_a? TestMessages::TestMessage
      @replyaddr = getSender()
      # @timeout = Timeout.new(Duration.create(15, TimeUnit::SECONDS));
      msg.handler.tell(msg.event)
    elsif msg.is_a? EventMessage
      @replyaddr.tell(msg.message)
    elsif msg.test_message
      @replyaddr = getSender()
      puts "testing #{msg.test_message.event.message}"
      msg.test_message.handler.tell(msg.test_message.event)
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
    @hash_template = {
      "roomName" => "noNameRoom",
      "clientID" => "0",
      "password" => "",
      "deviseName" => "fake@fake.com",
      "userName" => "Joe Farmer"
    }
  end

  after(:each) do
    @system.shutdown
    @system.await_termination
  end

  it "should be able to create and talk to actor" do
    future = Patterns.ask(@listener, "hello?", @timeout);
    result = Await.result(future, @timeout.duration());
    result.should == "hihi"
    # should be "hello"
  end

  it "should be able to ask if a room is open" do
    future = Patterns.ask(@listener, TestMessages::ValidateRoomMessage.new(@handler, @hash_template), @timeout)
    result = Await.result(future, @timeout.duration())
    parsed = JSON.parse(result)
    parsed["result"].should == true
  end

  it "should be able to create room" do
    future = Patterns.ask(@listener, TestMessages::CreateRoomMessage.new(@handler, @hash_template), @timeout)
    result = Await.result(future, @timeout.duration())
    parsed = JSON.parse(result)
    parsed["result"].should == true
  end

  it "should be unable to create room twice invalid" do
    future = Patterns.ask(@listener, TestMessages::CreateRoomMessage.new(@handler, @hash_template), @timeout)
    result = Await.result(future, @timeout.duration())
    parsed = JSON.parse(result)
    parsed["result"].should == true

    future = Patterns.ask(@listener, TestMessages::CreateRoomMessage.new(@handler, @hash_template), @timeout)
    result = Await.result(future, @timeout.duration())
    parsed = JSON.parse(result)
    parsed["result"].should == false
  end

  it "should see existing rooms as invalid" do
    future = Patterns.ask(@listener, TestMessages::CreateRoomMessage.new(@handler, @hash_template), @timeout)
    result = Await.result(future, @timeout.duration())
    parsed = JSON.parse(result)
    parsed["result"].should == true

    future = Patterns.ask(@listener, TestMessages::ValidateRoomMessage.new(@handler, @hash_template), @timeout)
    result = Await.result(future, @timeout.duration())
    parsed = JSON.parse(result)
    parsed["result"].should == false
  end

  it "should be able to create a passworded room" do
    @hash_template["password"]="apassword"

    future = Patterns.ask(@listener, TestMessages::CreateRoomMessage.new(@handler, @hash_template), @timeout)
    result = Await.result(future, @timeout.duration())
    parsed = JSON.parse(result)
    parsed["result"].should == true
  end

end