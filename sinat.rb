require 'sinatra'

get '/' do
  "Biofuels Game Model \n If you just woke up the server, try '/start' to get the model running again"
end

get '/hi' do
  "Hello World!"
end

get '/start' do
  Thread.new do
    puts load('javaGame/server_runner.rb')
  end
  "Started server. Don't go running this twice now..."
end

