/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.Pair;
import static com.amazon.ask.request.Predicates.requestType;
import com.clt.diamant.graph.DialogState;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koller
 */
public class LaunchRequestHandler implements RequestHandler {
    private final String modelResourceName;

    public LaunchRequestHandler(String modelResourceName) {
        this.modelResourceName = modelResourceName;
    }
    
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        InputStream modelStream = getClass().getResourceAsStream(modelResourceName);
        ResumingDialogRunner<String,HandlerInput> runner = new ResumingDialogRunner<>(modelStream);
        
        try {
            // run dialog from start state
            Pair<DialogState,String> result = runner.runUntilSuspend(null, null);
            return DialogosIntentHandler.buildResponse(result, input);
        } catch (Exception ex) {
            Logger.getLogger(LaunchRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
    
}
