scriptloc = File.expand_path(File.dirname(__FILE__))

require_relative "server_wrapper"


s = ServerWrapper.new

# s.do_akka(ARGV[0])
s.do_akka(false) #true for local redis (development), false for redistogo (production/heroku)

# s.watch(ARGV[1])