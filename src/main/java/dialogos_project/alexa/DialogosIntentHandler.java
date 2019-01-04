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
import com.amazon.ask.request.Predicates;
import com.clt.diamant.suspend.DialogState;
import com.clt.diamant.suspend.ResumingDialogRunner;
import com.clt.diamant.suspend.SuspendedExecutionResult;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            AlexaPluginSettings pluginSettings = (AlexaPluginSettings) runner.getDocument().getPluginSettings(Plugin.class);
            pluginSettings.setMostRecentHandlerInput(input);
            
            // decode dialog state from session
            Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
            String strDialogState = (String) sessionAttributes.get(DIALOG_STATE_KEY);
            DialogState state = DialogState.fromJson(strDialogState);

            // resume dialog
            SuspendedExecutionResult<String> result = runner.runUntilSuspend(state, input);
            return buildResponse(result, input);
        } catch (Exception ex) {
            Logger.getLogger(DialogosIntentHandler.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }

    static Optional<Response> buildResponse(SuspendedExecutionResult<String> result, HandlerInput input) {
        if (result == null) {
            // dialog terminated successfully
            return input.getResponseBuilder()
                    .withShouldEndSession(true)
                    .build();
        } else {
            Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
            sessionAttributes.put(DIALOG_STATE_KEY, result.getDialogState().toJson());
            input.getAttributesManager().setSessionAttributes(sessionAttributes);

            return input.getResponseBuilder()
                    .withSpeech(result.getPrompt())
                    .withSimpleCard("HelloWorld", result.getPrompt())
                    .withShouldEndSession(false)
                    .build();
        }
    }
}
