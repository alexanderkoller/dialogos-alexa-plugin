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
import com.clt.diamant.graph.SuspendingNode;
import com.clt.diamant.graph.nodes.AbstractInputNode.EdgeManager;
import com.clt.diamant.graph.nodes.AbstractInputNode.PatternTable;
import com.clt.diamant.graph.nodes.TimeoutEdge;
import com.clt.diamant.graph.ui.EdgeConditionModel;
import com.clt.gui.GUI;
import com.clt.xml.XMLWriter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author koller
 */
public class AlexaInputNode extends SuspendingNode<String> {
    private static final String TIMEOUT_PROPERTY = "timeout";
    private EdgeManager edgeManager = new EdgeManager(this, TIMEOUT_PROPERTY);
//    private InputOutputSynchronizer<HandlerInput, Optional<Response>> synchronizer = getGraph().getSynchronizer();

    public AlexaInputNode() {
        /* important that some value is set (must not be one of Boolean values, not null later) */
        this.setProperty(TIMEOUT_PROPERTY, null);
        this.setProperty("background", Boolean.FALSE);
    }

    public static String getNodeTypeName(Class<?> c) {
        return "Alexa Input";
    }

    @Override
    public Node execute(WozInterface wi, InputCenter ic, ExecutionLogger el) {
        System.err.println("[DialogOS] execute");
        
        String inputValue = consumeInputValue();
        
        if( inputValue == null ) {
            suspend();
        } else {
            System.err.println("Alexa input node received input value: " + inputValue);
            // resume  -> choose port
        }
        
        
        // TODO figure out how to resume
        

//        try {
//            HandlerInput intent = synchronizer.receiveFromCaller();
//            System.err.println("[DialogOS] received: " + intent);
//
//            String speechText = "Hello world";
//            Optional<Response> ret = intent.getResponseBuilder()
//                    .withSpeech(speechText)
//                    .withSimpleCard("HelloWorld", speechText)
//                    .build();
//            
//            System.err.println("[DialogOS] built response: " + ret);
//
////            synchronizer.sendToCaller(ret);
//            System.err.println("[DialogOS] sent: " + ret);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(AlexaInputNode.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ExecutionException ex) {
//            Logger.getLogger(AlexaInputNode.class.getName()).log(Level.SEVERE, null, ex);
//        }



//        
//        
//        System.err.println("Alexa Input: EXECUTE");
//        
//        AlexaPluginRuntime runtime = (AlexaPluginRuntime) getPluginRuntime(Plugin.class, wi);
//        AlexaExecutionContext context = runtime.getContext();
//
//        try {
//            // disconnect and wait for next connection from client
//            context.disconnect();
//            context.connect();
//
//            // then read the line they sent
//            String input = context.read();
//            System.err.println("received: " + input);
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
        return getEdge(0).getTarget();
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

    @Override
    public JComponent createEditorComponent(Map<String, Object> properties) {
        JTabbedPane tabs = GUI.createTabbedPane();

        JPanel inputTab = new JPanel(new BorderLayout(6, 0));

//        mainPage.add(patternTable, BorderLayout.CENTER);
        JPanel outputTab = new JPanel();
        tabs.addTab("Input", inputTab);
        tabs.addTab("Output", outputTab);

        // populate Input panel
        final EdgeConditionModel edgeModel = new EdgeConditionModel(this, properties, "Intent patterns"); // TODO localize
        final PatternTable patternTable = new PatternTable(edgeModel);
        inputTab.add(patternTable);

        return tabs;
    }

    @Override
    public void writeVoiceXML(XMLWriter writer, IdMap idmap) throws IOException {

    }

}
