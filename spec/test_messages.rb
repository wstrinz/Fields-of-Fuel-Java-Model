
module TestMessages
  BASICMESSAGE = {
    "clientID" => "#{0}"
  }

  class ExpectReplies
    include Serializable

    attr_reader :expected

    def initialize(expected)
      @expected = expected
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

    attr_reader :message, :handler

    def initialize(roomName, handler)
      message = Hash.new
      message["clientID"] = BASICMESSAGE["clientID"]
      message["roomName"] = roomName
      message["event"] = "validateRoom"
      @message =  EventMessage.new(message.to_json)

      @handler = handler
    end
  end

end