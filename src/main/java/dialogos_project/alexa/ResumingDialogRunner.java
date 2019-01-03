/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.model.services.Pair;
import com.clt.dialog.client.StdIOConnectionChooser;
import com.clt.dialogos.plugin.PluginLoader;
import com.clt.diamant.Document;
import com.clt.diamant.Executer;
import com.clt.diamant.ExecutionResult;
import com.clt.diamant.Preferences;
import com.clt.diamant.Resources;
import com.clt.diamant.SingleDocument;
import com.clt.diamant.WozInterface;
import com.clt.diamant.graph.DialogState;
import com.clt.diamant.graph.SuspendingNode;
import com.clt.diamant.graph.nodes.DialogSuspendedException;
import com.clt.event.ProgressEvent;
import com.clt.event.ProgressListener;
import com.clt.gui.GUI;
import com.clt.util.Misc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;

/**
 *
 * @author koller
 */
public class ResumingDialogRunner<FromDialogos, ToDialogos> {

    private InputStream modelStream;

    public ResumingDialogRunner(InputStream model) {
        this.modelStream = model;
    }

    public Pair<DialogState, FromDialogos> runUntilSuspend(DialogState state, ToDialogos inputValue) throws Exception {
        // initialize preferences
        Preferences.getPrefs();

        // load plugins
        File appDir = Misc.getApplicationDirectory();
        PluginLoader.loadPlugins(appDir, e -> {
            GUI.invokeAndWait(() -> {
                String pluginName = e.getMessage();
                System.err.println(Resources.format("LoadingPluginX", pluginName));
            });
        });

        SingleDocument d = SingleDocument.loadFromStream(modelStream);

        // resume suspended dialog
        if (state != null) {
            // send input value to node
            SuspendingNode<FromDialogos, ToDialogos> n = state.lookupNode(d.getOwnedGraph());
            n.resume(inputValue);

            // reset graph to correct state
            d.getOwnedGraph().resume(state);
        }

        if (d.connectDevices(new StdIOConnectionChooser(), Preferences.getPrefs().getConnectionTimeout())) {
            final WozInterface executer = new Executer(null, false);

            try {
                ExecutionResult result = d.run(null, executer);
            } catch (DialogSuspendedException exn) {
                // dialog was suspended
                Pair<DialogState, FromDialogos> ret = new Pair<>(exn.getDialogState(), (FromDialogos) exn.getPrompt());
                return ret;
            }
        }

        // dialog terminated by visiting an end node
        return null;
    }
    
//    private static class SilentProgressListener implements ProgressListener {
//        @Override
//        public void progressChanged(ProgressEvent pe) {
//        }
//    }

    public static void main(String[] args) throws IOException, Exception {
        File model = new File(args[0]);

        String inputForResume = null;
        DialogState state = null;

        if (args.length > 1) {
            inputForResume = args[1];

            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            String suspendedStateStr = r.readLine();
            
            JSONObject j = new JSONObject(suspendedStateStr);
            state = DialogState.fromJson(j);
        }

        ResumingDialogRunner<String, String> runner = new ResumingDialogRunner<>(new FileInputStream(model));
        Pair<DialogState,String> result = runner.runUntilSuspend(state, inputForResume);
        
        if( result == null ) {
            System.err.println("Dialog terminated successfully.");
        } else {
            System.err.println("Dialog suspended with prompt: " + result.getValue());
            System.out.println(result.getName().toJson());
        }
    }
}
