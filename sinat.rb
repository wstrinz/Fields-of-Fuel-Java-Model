require 'sinatra'

get '/hi' do
  "Hello World!"
end

get '/start' do
    `. run.sh`
end
