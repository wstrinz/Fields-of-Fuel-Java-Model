require 'sinatra'

configure do
  @@started_s = false
end

get '/' do
  "Biofuels Game Model \n If you just woke up the server, try '/start' to get the model running again"
end

get '/hi' do
  "Hello World!"
end

get '/start' do
  unless @@started_s
    Thread.new do
      puts load('javaGame/server_runner.rb')
    end
    @@started_s = true
    "Started server. Don't go running this twice now..."
  else
    "Server already started!"
  end
end

