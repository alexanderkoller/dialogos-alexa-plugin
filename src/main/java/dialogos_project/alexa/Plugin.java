package dialogos_project.alexa;

import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.graph.Node;
import java.util.Arrays;
import javax.swing.Icon;

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
