# Alexa plugin for DialogOS

```
public class DialogosSpeechlet implements Speechlet {
    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
      // (load predefined model file)
      // set up data structures as needed
    }
    
    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
      // create lock object on which input nodes for this session will be synchronized
      // create a communication object that input and output nodes for this session will use

      // create new SingleDocumentWindow for this session
      // somehow tell the Alexa plugin inside that SDW how to send responses (via link to comm object)
      // run SDW - in a new thread (see DialogOS#main)
      // (Alexa input nodes will wait() each time they are called)
      
      // Speechlet waits until dialog reaches Alexa input node -> then return from here
    } 

    public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
      // set input in comm object to text of intentRequest
      // notify lock object for this session to wake up graph execution
      // -> this will execute the graph until the next input node
      
      // onIntent waits until dialog reaches Alexa input node -> then return from here
      
      // Alexa input node needs to include prompt, which needs to be passed
      // to onIntent, so it can be returned to Alexa.
    }

    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
      // clean up
    }

}
```
