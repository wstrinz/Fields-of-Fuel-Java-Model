package com.biofuels.fof.kosomodel;
import java.util.ArrayList;
import java.util.HashMap;

import akka.actor.*;


import org.json.simple.*;

public class Handler extends UntypedActor{

  HandlerHelper eh = new HandlerHelper(getContext().actorFor("/user/listener"));

  public void onReceive(Object message) throws Exception {

    if(message instanceof EventMessage){
      String[] replies = eh.handle(((EventMessage)message).message);

      for(String m:replies){
        // System.out.println("reply " + m + " to " + getContext().actorFor("../listener"));
        getContext().actorFor("../listener").tell(new EventMessage(m), getSelf());
      }
    }

    if(message instanceof ActorRef){
      eh.setListener((ActorRef) message);
    }
  }
}