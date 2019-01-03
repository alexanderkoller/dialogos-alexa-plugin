package dialogos_project.alexa;

import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.IdMap;
import com.clt.diamant.graph.Node;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.xml.sax.SAXException;

public class Plugin implements com.clt.dialogos.plugin.Plugin {
    
    @Override
    public void initialize() {
        Node.registerNodeTypes(getId(), Arrays.asList(new Class<?>[]{
            AlexaOutputNode.class,
            AlexaInputNode.class
        }));
    }

    @Override
    public String getId() {
        return "alexa-plugin";
    }

    @Override
    public String getName() {
        return "Amazon Alexa plugin";
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
        return new AlexaPluginSettings();
    }
}
