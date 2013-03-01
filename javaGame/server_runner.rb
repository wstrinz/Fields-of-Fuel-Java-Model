scriptloc = File.expand_path(File.dirname(__FILE__))

if ARGV[0]
  puts `javac -cp "#{scriptloc}"/json-simple-1.1.1.jar "#{scriptloc}"/*.java` # attempt to compile server
end


require_relative "server_wrapper"



if !File.exist?(File.join(scriptloc, "../pipes/javapipe"))
  #puts 'making javapipe'
  # File.delete('javapipe')
  `mkfifo "#{scriptloc}"/../pipes/javapipe`
end

if !File.exist?(File.join(scriptloc,"../pipes/rubypipe"))
  #puts 'making rubypipe'
  # File.delete('rubypipe')
  `mkfifo "#{scriptloc}"/../pipes/rubypipe`
end

s = ServerWrapper.new

s.watch(ARGV[1])