Biofuel Game Model
===================

Separated from the main project since it can be run as a separate process. First, build the server with maven

> cd javaGame/kosomodel
>
> mvn compile
>
> mvn package

Then run load_akka.rb with jruby to start the server
> rvm jruby exec ruby load_akka.rb
