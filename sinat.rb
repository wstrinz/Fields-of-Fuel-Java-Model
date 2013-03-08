require 'sinatra'
require 'json'

configure do
  @@started_s = false
end

get '/' do
  "Biofuels Game Model Control \n If you just woke up the server, try '/start' to get the model running again"
end

get '/hi' do
  "Hello World!"
end

get '/start' do
  unless @@started_s
    Thread.new do
      puts load('load_akka.rb')
    end
    @@started_s = true
    "Started server. Don't go running this twice now..."
  else
    "Server already started!"
  end
end

# get '/start_js' do
#   unless @@started_s
#     Thread.new do
#       puts load('load_akka.rb')
#     end
#     @@started_s = true
#     content_type :json
#     {:result => true}.to_json
#   else
#     content_type :json
#     {:result => false}.to_json
#   end
# end

