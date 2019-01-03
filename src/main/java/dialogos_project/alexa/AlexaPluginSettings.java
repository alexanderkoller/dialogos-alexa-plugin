/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.IdMap;
import com.clt.diamant.graph.Graph;
import com.clt.properties.DefaultBooleanProperty;
import com.clt.properties.DefaultStringProperty;
import com.clt.properties.Property;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.xml.sax.SAXException;

/**
 *
 * @author koller
 */
public class AlexaPluginSettings extends PluginSettings {

    private static final String TESTING = "testing";
    private Property<Boolean> testMode;

    public AlexaPluginSettings() {
        testMode = new DefaultBooleanProperty(TESTING, "Testing", "Reads inputs from the keyboard instead of Alexa."); // TODO localize
    }
    
    public boolean isTestMode() {
        return testMode.getValueAsObject();
    }

    @Override
    public void writeAttributes(XMLWriter writer, IdMap idmap) {
        Graph.printAtt(writer, TESTING, testMode.getValueAsString());
    }

    @Override
    protected void readAttribute(XMLReader reader, String name, String value, IdMap idmap) throws SAXException {
        if( name.equals(TESTING)) {
            testMode.setValue(Boolean.parseBoolean(value));
        }
    }

    @Override
    public JComponent createEditor() {
        JPanel p = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 3, 2, 3);

        testMode.addToPanel(p, gbc, false);

        return p;
    }

    @Override
    protected PluginRuntime createRuntime(Component cmpnt) throws Exception {
        return new PluginRuntime() {
            @Override
            public void dispose() {
            }
        };
    }

}
