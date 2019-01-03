/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Slot;
import com.clt.diamant.ExecutionLogger;
import com.clt.diamant.ExecutionStoppedException;
import com.clt.diamant.IdMap;
import com.clt.diamant.InputCenter;
import com.clt.diamant.WozInterface;
import com.clt.diamant.graph.Edge;
import com.clt.diamant.graph.Graph;
import com.clt.diamant.graph.Node;
import com.clt.diamant.graph.SuspendingNode;
import com.clt.diamant.graph.nodes.AbstractInputNode.EdgeManager;
import com.clt.diamant.graph.nodes.AbstractInputNode.PatternTable;
import com.clt.diamant.graph.nodes.NodeExecutionException;
import com.clt.diamant.graph.nodes.TimeoutEdge;
import com.clt.diamant.graph.ui.EdgeConditionModel;
import com.clt.diamant.gui.NodePropertiesDialog;
import com.clt.diamant.gui.SilentInputWindow;
import com.clt.gui.GUI;
import com.clt.script.exp.Expression;
import com.clt.script.exp.Match;
import com.clt.script.exp.Pattern;
import com.clt.script.exp.Value;
import com.clt.script.exp.values.StringValue;
import com.clt.script.exp.values.StructValue;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.xml.sax.SAXException;

/**
 *
 * @author koller
 */
public class AlexaInputNode extends SuspendingNode<String, HandlerInput> {

    private static final String TIMEOUT_PROPERTY = "timeout";
    private static final String PROMPT_PROPERTY = "prompt";

    private EdgeManager edgeManager = new EdgeManager(this, TIMEOUT_PROPERTY);

    public AlexaInputNode() {
        /* important that some value is set (must not be one of Boolean values, not null later) */
        this.setProperty(TIMEOUT_PROPERTY, null);
        this.setProperty("background", Boolean.FALSE);
        this.setProperty(PROMPT_PROPERTY, "\"\"");
    }

    public static String getNodeTypeName(Class<?> c) {
        return "Alexa Input";
    }

    @Override
    public Node execute(WozInterface wi, InputCenter ic, ExecutionLogger el) {
        String prompt = evaluatePromptExpression(PROMPT_PROPERTY);
        Value intentStruct = null;
        boolean testMode = getSettings().isTestMode();

        if (testMode) {
            // in testing mode, read string from popup window
            String s = SilentInputWindow.getString(null, "Alexa input node", prompt);

            if (s == null) {
                throw new ExecutionStoppedException(); // user wants to abort dialog
            }

            intentStruct = makeStructFromTypedString(s);
        } else {
            // otherwise, emit prompt to Alexa, then suspend the dialog
            // and wait for callback from Alexa
            HandlerInput inputValue = receiveAsynchronousInput(prompt);

//            AlexaPluginSettings settings = (AlexaPluginSettings) getPluginSettings(Plugin.class);
//            runtime.setMostRecentHandlerInput(inputValue);
            if (inputValue.getRequest() instanceof IntentRequest) {
                IntentRequest req = (IntentRequest) inputValue.getRequest();
                System.err.println("Alexa input node received: " + req.getIntent());
                intentStruct = makeIntentStruct(req.getIntent());
            } else {
                throw new NodeExecutionException(this, "Received an invalid request from Alexa: " + inputValue.getRequest());
            }
        }

        if (intentStruct != null) {
            List<Edge> edges = new ArrayList<>(edges());
            Pattern[] patterns = createPatterns(edges);

            for (int i = 0; i < patterns.length; i++) {
                Match m = patterns[i].match(intentStruct);
                if (m != null) {
                    setVariablesAccordingToMatch(m);
                    return getEdge(i).getTarget();
                }
            }
        }

        throw new NodeExecutionException(this, "Don't know how to handle Alexa intent: " + intentStruct);
    }

    private static Value makeIntentStruct(Intent intent) {
        Map<String, Value> struct = new HashMap<>();
        struct.put("INTENT", new StringValue(intent.getName()));

        if (intent.getSlots() != null) {
            for (String slotName : intent.getSlots().keySet()) {
                Slot slot = intent.getSlots().get(slotName);

                if (slot.getValue() != null) {
                    struct.put(slotName, new StringValue(slot.getValue()));
                }
            }
        }

        return new StructValue(struct);
    }

    private Value makeStructFromTypedString(String s) {
        if (s.trim().startsWith("{")) {
            try {
                return parseExpression(s).evaluate();
            } catch (Exception ex) {
                Logger.getLogger(AlexaInputNode.class.getName()).log(Level.SEVERE, null, ex);
                return new StructValue();
            }
        } else {
            Map<String, Value> struct = new HashMap<>();
            struct.put("INTENT", new StringValue(s));
            return new StructValue(struct);
        }
    }

    // copied from AbstractInputNode
    private Pattern[] createPatterns(List<Edge> edges) {
        final Pattern[] patterns = new Pattern[edges.size()];

        int n = 0;
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (!(e instanceof TimeoutEdge)) {
                try {
                    patterns[n] = parsePattern(e.getCondition()); // allow pattern-matching
                } catch (Exception exn) {
                    throw new NodeExecutionException(this,
                            com.clt.diamant.Resources.getString("IllegalPattern") + ": " + e.getCondition(), exn);
                }

                n++;
            }
        }

        return patterns;
    }

    @Override
    public void updateEdges() {
        edgeManager.updateEdges();
    }

    @Override
    public boolean editProperties(Component parent) {
        TimeoutEdge timeoutEdge = edgeManager.updateEdgeProperty();
        boolean approved = super.editProperties(parent);

        if (approved) {
            edgeManager.reinstallEdgesFromProperty(timeoutEdge);
            return true;
        } else {
            return false;
        }
    }

    /**
     * retrieve the settings from the dialog graph (which is where they are
     * stored -- not within Plugin!)
     */
    private AlexaPluginSettings getSettings() {
        if (getGraph() != null && getGraph().getOwner() != null) {
            return ((AlexaPluginSettings) getGraph().getOwner().getPluginSettings(Plugin.class));
        } else {
            return null;
        }
    }

    @Override
    public JComponent createEditorComponent(Map<String, Object> properties) {
        JTabbedPane tabs = GUI.createTabbedPane();

        // populate Input panel
        JPanel inputTab = new JPanel(new BorderLayout(6, 0));
        tabs.addTab("Input", inputTab);  // TODO localize

        final EdgeConditionModel edgeModel = new EdgeConditionModel(this, properties, "Intent patterns"); // TODO localize
        final PatternTable patternTable = new PatternTable(edgeModel);
        inputTab.add(patternTable);

        // populate Output panel
        JPanel outputTab = new JPanel(new GridBagLayout());
        tabs.addTab("Output", outputTab); // TODO localize

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.anchor = GridBagConstraints.EAST;
        outputTab.add(new JLabel("Prompt:"), gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        final JTextField tf = NodePropertiesDialog.createTextField(properties, PROMPT_PROPERTY);
        outputTab.add(tf, gbc);

        return tabs;
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

    @Override
    public void writeVoiceXML(XMLWriter writer, IdMap idmap) throws IOException {

    }

    private String evaluatePromptExpression(String property) {
        String promptProp = (String) getProperty(property);

        try {
            Expression expr = parseExpression(promptProp);
            Value promptV = expr.evaluate();
            return ((StringValue) promptV).getString();
        } catch (Exception ex) {
            Logger.getLogger(AlexaOutputNode.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
