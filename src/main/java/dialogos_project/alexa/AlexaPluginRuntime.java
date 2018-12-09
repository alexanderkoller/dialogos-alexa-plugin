/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.clt.dialogos.plugin.PluginRuntime;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koller
 */
public class AlexaPluginRuntime implements PluginRuntime {
    private AlexaExecutionContext context = null;

    public AlexaPluginRuntime() throws IOException {
//        context = new AlexaExecutionContext(port);
//        context.connect();
    }

    @Override
    public void dispose() {
//        try {
//            context.disconnect();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
    }

    public AlexaExecutionContext getContext() {
        return context;
    }
    
    
}
