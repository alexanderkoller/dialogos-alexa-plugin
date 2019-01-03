/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.Pair;
import com.amazon.ask.request.Predicates;
import com.clt.diamant.graph.DialogState;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author koller
 */
public class DialogosIntentHandler implements RequestHandler {

    static final String DIALOG_STATE_KEY = "dialog_state";
    private final String modelResourceName;

    public DialogosIntentHandler(String modelResourceName) {
        this.modelResourceName = modelResourceName;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(Predicates.requestType(IntentRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        try {
            InputStream modelStream = getClass().getResourceAsStream(modelResourceName);
            ResumingDialogRunner<String, HandlerInput> runner = new ResumingDialogRunner<>(modelStream);
            
            // remember handler input
            runner.getPluginSettings().setMostRecentHandlerInput(input);
            
            // decode dialog state from session
            Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
            String strDialogState = (String) sessionAttributes.get(DIALOG_STATE_KEY);
            JSONObject j = new JSONObject(strDialogState);
            DialogState state = DialogState.fromJson(j);

            // resume dialog
            Pair<DialogState, String> result = runner.runUntilSuspend(state, input);
            return buildResponse(result, input);
        } catch (Exception ex) {
            Logger.getLogger(DialogosIntentHandler.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }

    static Optional<Response> buildResponse(Pair<DialogState, String> result, HandlerInput input) {
        if (result == null) {
            // dialog terminated successfully
            return input.getResponseBuilder()
                    .withShouldEndSession(true)
                    .build();
        } else {
            Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
            sessionAttributes.put(DIALOG_STATE_KEY, result.getName().toJson().toString());
            input.getAttributesManager().setSessionAttributes(sessionAttributes);

            return input.getResponseBuilder()
                    .withSpeech(result.getValue())
                    .withSimpleCard("HelloWorld", result.getValue())
                    .withShouldEndSession(false)
                    .build();
        }
    }
}
