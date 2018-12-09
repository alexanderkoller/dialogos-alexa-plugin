/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.clt.diamant.InputOutputSynchronizer;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koller
 */
public class DialogosIntentHandler implements RequestHandler {
    private InputOutputSynchronizer<HandlerInput, Optional<Response>> synchronizer;

    public DialogosIntentHandler(InputOutputSynchronizer<HandlerInput, Optional<Response>> synchronizer) {
        this.synchronizer = synchronizer;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return true; // for now
//        return input.matches(Predicates.intentName("HelloWorldIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        try {
            synchronizer.sendToDialogos(input);
            return synchronizer.receiveFromDialogos();
        } catch (Exception ex) {
            Logger.getLogger(DialogosIntentHandler.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
}
