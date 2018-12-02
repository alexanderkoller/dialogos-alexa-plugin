/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.clt.diamant.ExecutionLogger;
import com.clt.diamant.IdMap;
import com.clt.diamant.InputCenter;
import com.clt.diamant.WozInterface;
import com.clt.diamant.graph.Node;
import com.clt.xml.XMLWriter;
import java.io.IOException;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author koller
 */
public class AlexaOutputNode extends Node {
    public AlexaOutputNode() {
        addEdge();
    }
    

    @Override
    public Node execute(WozInterface wi, InputCenter ic, ExecutionLogger el) {
        AlexaPluginRuntime runtime = (AlexaPluginRuntime) getPluginRuntime(Plugin.class, wi);
        runtime.getContext().write("hallo");
        
        return getEdge(0).getTarget();
    }

    @Override
    public JComponent createEditorComponent(Map<String, Object> map) {
        return new JLabel("editor component");
    }

    @Override
    public void writeVoiceXML(XMLWriter writer, IdMap idmap) throws IOException {
        
    }
    
}
