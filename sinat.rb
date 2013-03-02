require 'sinatra'

get '/hi' do
  "Hello World!"
end

get '/start' do
  Thread.new do
    puts load('javaGame/server_runner.rb')
  end
  "Started server. Don't go running this twice now..."
end
