package com.biofuels.fof.kosomodel;
import java.util.ArrayList;
import java.util.HashMap;

import akka.actor.*;


import org.json.simple.*;

public class Handler extends UntypedActor{

  HandlerHelper eh = new HandlerHelper(getContext().actorFor("/user/listener"), getSelf());

  public void onReceive(Object message) throws Exception {

    if(message instanceof EventMessage){
      eh.handle(((EventMessage)message).message);
    }


    else if(message instanceof ActorRef){
      eh.setListener((ActorRef) message);
    }
  }
}