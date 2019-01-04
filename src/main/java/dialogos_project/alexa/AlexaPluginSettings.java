/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.services.directive.Directive;
import com.amazon.ask.model.services.directive.DirectiveServiceClient;
import com.amazon.ask.model.services.directive.Header;
import com.amazon.ask.model.services.directive.SendDirectiveRequest;
import com.amazon.ask.model.services.directive.SpeakDirective;
import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.Document;
import com.clt.diamant.IdMap;
import com.clt.diamant.Main;
import com.clt.diamant.graph.Graph;
import com.clt.gui.OptionPane;
import com.clt.gui.ProgressDialog;
import com.clt.properties.DefaultBooleanProperty;
import com.clt.properties.Property;
import com.clt.util.JarBuilder;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.xml.sax.SAXException;

/**
 *
 * @author koller
 */
public class AlexaPluginSettings extends PluginSettings {

    private static final String TESTING = "testing";
    private Property<Boolean> testMode;

    // Remembers the most recent HandlerInput that was sent by Alexa.
    // This is not strictly speaking part of the settings (i.e.
    // cannot be edited through the GUI; will not persist), but this
    // seems like the most conveniently accessible place to put it.
    private HandlerInput mostRecentHandlerInput = null;

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
        if (name.equals(TESTING)) {
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

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Save dialog as Alexa skill"), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        JButton bGenerate = new JButton("Generate");
        bGenerate.addActionListener(new GenerateButtonActionListener());
        p.add(bGenerate, gbc);

        return p;
    }

    private class GenerateButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Main main = Main.getInstance();
            final Document doc = (main == null) ? null : main.getMostRecentlyActivatedDocumentWindow().getDocument();

            if (doc != null) {
                Runnable r = () -> {
                    // select filename
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar files", "jar");
                    chooser.setFileFilter(filter);

                    JButton source = (JButton) e.getSource();
                    Component parent = source.getTopLevelAncestor();
                    int result = chooser.showSaveDialog(parent);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        File outputFile = chooser.getSelectedFile();
                        ProgressDialog pd = new ProgressDialog(parent);
                        pd.setTitle("Generating Alexa skill");

                        boolean originalTestMode = testMode.getValueAsObject();

                        try {
                            JarBuilder jb = new JarBuilder(outputFile.getAbsolutePath());

                            jb.addProgressListener(pd);
                            SwingUtilities.invokeLater(() -> {
                                pd.setVisible(true);
                            });

                            // add all classes on the classpath to the output jar
                            jb.addClasspath();
                            SwingUtilities.invokeLater(() -> {
                                pd.setVisible(false);
                            });

                            // save current dialog to output jar, temporarily
                            // disabling test mode
                            File tmp = File.createTempFile("dialogos", "dos");
                            testMode.setValue(false);
                            doc.save(parent, tmp, pe -> { });
                            jb.addEntry("model.dos", new FileInputStream(tmp));
                            tmp.delete();

                            jb.close();

                            JOptionPane.showMessageDialog(parent, "Alexa skill saved.");
                        } catch (Exception ex) {
                            OptionPane.error(parent, ex);
                        } finally {
                            testMode.setValue(originalTestMode);
                        }
                    }
                };

                new Thread(r).start();
            }
        }
    }

    @Override
    protected PluginRuntime createRuntime(Component cmpnt) throws Exception {
        return new PluginRuntime() {
            @Override
            public void dispose() {
            }
        };
    }

    public HandlerInput getMostRecentHandlerInput() {
        return mostRecentHandlerInput;
    }

    public void setMostRecentHandlerInput(HandlerInput mostRecentHandlerInput) {
        this.mostRecentHandlerInput = mostRecentHandlerInput;
    }

    /**
     * Sends a "progressive response" to Alexa. This is an utterance that is
     * spoken to the user while the dialog is still working on a response.
     *
     * @param message
     * @param input
     */
    public void sendProgressiveResponse(String message, HandlerInput input) {
        System.err.println("progressive response: " + message);
        System.err.println("handler input: " + input);

        RequestEnvelope renv = input.getRequestEnvelope();
        DirectiveServiceClient directiveServiceClient = input.getServiceClientFactory().getDirectiveService();
        String requestId = renv.getRequest().getRequestId();

        Header hdr = Header.builder().withRequestId(requestId).build();
        Directive directive = SpeakDirective.builder().withSpeech(message).build();
        SendDirectiveRequest speakRequest = SendDirectiveRequest.builder().withHeader(hdr).withDirective(directive).build();

        System.err.println("directive: " + speakRequest);
        System.err.println("client: " + directiveServiceClient);
        directiveServiceClient.enqueue(speakRequest);
    }

}
