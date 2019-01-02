/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

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
import com.clt.gui.GUI;
import com.clt.util.Misc;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;

/**
 *
 * @author koller
 */
public class ResumingDialogRunner {

    public static void main(String[] args) throws IOException, Exception {
        File model = new File(args[0]);

        String inputForResume = null;
        String suspendedState = null;
        
        if (args.length > 1) {
            inputForResume = args[1];
            
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            suspendedState = r.readLine();
        }

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

        SingleDocument d = (SingleDocument) Document.load(model);
        
        if( inputForResume != null ) {
            JSONObject j = new JSONObject(suspendedState);
            DialogState state = DialogState.fromJson(j, d.getOwnedGraph());
            
            // send input value to node
            SuspendingNode n = (SuspendingNode) d.getOwnedGraph().findNodeById(state.getSuspendedNode().getId());
            n.resume(inputForResume);
            
            // resume graph execution
            d.getOwnedGraph().resume(state);
        }
        
        
        if (d.connectDevices(new StdIOConnectionChooser(), Preferences.getPrefs().getConnectionTimeout())) {
            final WozInterface executer = new Executer(null, false);

            try {
                ExecutionResult result = d.run(null, executer);
                System.out.println("execution finished, result: " + result);
            } catch (DialogSuspendedException exn) {
                System.err.println("dialog suspended!");
                System.out.println(exn.getDialogState().toJson().toString());
                System.exit(0);
            }
        }
    }
}
