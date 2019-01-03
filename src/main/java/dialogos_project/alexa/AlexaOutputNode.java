/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.clt.diamant.ExecutionLogger;
import com.clt.diamant.IdMap;
import com.clt.diamant.InputCenter;
import com.clt.diamant.Slot;
import com.clt.diamant.WozInterface;
import com.clt.diamant.graph.Graph;
import com.clt.diamant.graph.Node;
import com.clt.diamant.graph.nodes.NodeExecutionException;
import com.clt.diamant.gui.NodePropertiesDialog;
import com.clt.script.Environment;
import com.clt.script.exp.Expression;
import com.clt.script.exp.Value;
import com.clt.script.exp.values.StringValue;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.xml.sax.SAXException;

/**
 *
 * @author koller
 */
public class AlexaOutputNode extends Node {

    private static final String PROMPT_PROPERTY = "prompt";

    public AlexaOutputNode() {
        setProperty(PROMPT_PROPERTY, "\"\"");
        addEdge();
    }

    @Override
    public Node execute(WozInterface wi, InputCenter ic, ExecutionLogger el) {
        AlexaPluginSettings settings = (AlexaPluginSettings) getPluginSettings(Plugin.class);

        // set prompt from node properties
        Environment env = getGraph().getOwner().getEnvironment(Graph.GLOBAL);
        String promptProp = (String) getProperty(PROMPT_PROPERTY);
        String prompt = "";

        try {
            Value promptValue = Expression.parseExpression(promptProp, env).evaluate(wi);
            prompt = ((StringValue) promptValue).getString();
        } catch (Exception ex) {
            Logger.getLogger(AlexaInputNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if( settings.isTestMode() ) {
            JOptionPane.showMessageDialog(null, prompt, "Alexa output node", JOptionPane.PLAIN_MESSAGE);
        } else if (settings.getMostRecentHandlerInput() == null) {
            throw new NodeExecutionException(this, "Cannot only Alexa output node after the first request from Alexa has been sent.");
        } else {
            settings.sendProgressiveResponse(prompt, settings.getMostRecentHandlerInput());
        }

        return getEdge(0).getTarget();
    }

    @Override
    public JComponent createEditorComponent(Map<String, Object> properties) {
        JPanel outputTab = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.anchor = GridBagConstraints.EAST;
        outputTab.add(new JLabel("Expression:"), gbc); // TODO localize

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        final JTextField tf = NodePropertiesDialog.createTextField(properties, PROMPT_PROPERTY);
        outputTab.add(tf, gbc);

        return outputTab;
    }

    @Override
    public void writeVoiceXML(XMLWriter writer, IdMap idmap) throws IOException {

    }

    @Override
    protected void writeAttributes(XMLWriter out, IdMap uid_map) {
        super.writeAttributes(out, uid_map);

        String prompt = (String) this.getProperty(PROMPT_PROPERTY);
        if (prompt != null) {
            Graph.printAtt(out, PROMPT_PROPERTY, prompt);
        }
    }

    @Override
    protected void readAttribute(XMLReader r, String name, String value, IdMap uid_map) throws SAXException {
        if (name.equals(PROMPT_PROPERTY)) {
            setProperty(PROMPT_PROPERTY, value);
        }
    }

}
