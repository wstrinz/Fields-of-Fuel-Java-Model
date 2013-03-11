
module TestMessages
  BASICMESSAGE = {
    "clientID" => "0"
  }

  class ExpectReplies
    include Serializable

    attr_reader :expected

    def initialize(expected)
      @expected = expected
    end
  end

  class TestMessage
    include Serializable

    attr_reader :event, :handler

    def initialize(message, handler)
      # msg = {
      #   "clientID" => "0"
      #   "event" => "placeholder event"
      # }
      # msg = msg.merge(message)
      @event = EventMessage.new(message.to_json)
      @handler = handler
    end
  end

  # class EventMessage
  #   include Serializable

  #   attr_reader :message

  #   def initialize(message)
  #     # @message.merge(BASICMESSAGE)
  #     @message = (message)
  #   end
  # end

  class ValidateRoomMessage
    include Serializable

    attr_reader :test_message

    def initialize(handler, options={})
      message = Hash.new
      message["clientID"] = BASICMESSAGE["clientID"]
      message["roomName"] = "noName"
      message["event"] = "validateRoom"
      message = message.merge(options)
      @test_message =  TestMessage.new(message, handler)
    end
  end

  class CreateRoomMessage

    include Serializable

    attr_reader :test_message

    def initialize(handler, options={})
      message = Hash.new
      message["clientID"] = "0"
      message["roomName"] = "noName"
      message["password"] = ""
      message["event"] = "createRoom"
      message["playerCount"] = 8
      message = message.merge(options)
      @test_message = TestMessage.new(message, handler)
    end
  end

end