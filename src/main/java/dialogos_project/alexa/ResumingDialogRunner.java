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
import com.clt.diamant.graph.nodes.DialogSuspendedException;
import com.clt.gui.GUI;
import com.clt.util.Misc;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author koller
 */
public class ResumingDialogRunner {

    public static void main(String[] args) throws IOException, Exception {
        File model = new File(args[0]);

        String resumeWithIntent = null;
        if (args.length > 1) {
            resumeWithIntent = args[1];
        }

        // initialize preferences
        Preferences.getPrefs();

        // load plugins
        File appDir = Misc.getApplicationDirectory();
        PluginLoader.loadPlugins(appDir, e -> {
            GUI.invokeAndWait(() -> {
                String pluginName = e.getMessage();
                System.out.println(Resources.format("LoadingPluginX", pluginName));
            });
        });

        SingleDocument d = (SingleDocument) Document.load(model);
        if (d.connectDevices(new StdIOConnectionChooser(), Preferences.getPrefs().getConnectionTimeout())) {
            final WozInterface executer = new Executer(null, false);

            try {
                ExecutionResult result = d.run(null, executer);
                System.out.println("execution finished, result: " + result);
            } catch (DialogSuspendedException exn) {
                System.err.println("dialog suspended!");
                System.err.println(exn.getDialogState());
                System.exit(0);
            }
        }
    }
}
