require 'sinatra'

get '/hi' do
  "Hello World!"
end

get '/start' do
  Process.spawn do
    `. run.sh`
  end
end
