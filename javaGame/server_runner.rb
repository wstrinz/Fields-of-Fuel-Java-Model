scriptloc = File.expand_path(File.dirname(__FILE__))

require_relative "server_wrapper"


s = ServerWrapper.new

# s.do_akka(ARGV[0])
s.do_akka(true) #true for local redis, false for redistogo (heroku)

