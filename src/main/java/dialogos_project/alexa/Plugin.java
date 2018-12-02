package dialogos_project.alexa;

import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.IdMap;
import com.clt.diamant.graph.Node;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import java.awt.Component;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.xml.sax.SAXException;

public class Plugin implements com.clt.dialogos.plugin.Plugin {

    @Override
    public void initialize() {
        Node.registerNodeTypes(getId(),
                Arrays.asList(new Class<?>[]{ 
                    AlexaOutputNode.class,
                    AlexaInputNode.class 
                } ));
    }

    @Override
    public String getId() {
        return "alexa-plugin";
    }

    @Override
    public String getName() {
        return "Alexa";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public PluginSettings createDefaultSettings() {
        return new PluginSettings() {
            @Override
            public void writeAttributes(XMLWriter writer, IdMap idmap) {
                // TODO - fill me in
            }

            @Override
            protected void readAttribute(XMLReader reader, String string, String string1, IdMap idmap) throws SAXException {
                // TODO - fill me in
            }

            @Override
            public JComponent createEditor() {
                return new JLabel("Alexa settings");
            }

            @Override
            protected PluginRuntime createRuntime(Component cmpnt) throws Exception {
                return new AlexaPluginRuntime(12345); // TODO - read port from settings
            }
        };
    }
}
