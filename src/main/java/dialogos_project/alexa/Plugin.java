package dialogos_project.alexa;

import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.IdMap;
import com.clt.diamant.graph.Node;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import java.awt.Component;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.xml.sax.SAXException;

public class Plugin implements com.clt.dialogos.plugin.Plugin {

    private static interface Channel<E> {
        public void send(E value);
        public E receive() throws InterruptedException, ExecutionException;
    }
    
    
    // works too
    private static class QueueChannel<E> implements Channel<E> {
        private BlockingQueue<E> q = new ArrayBlockingQueue<>(1);
        
        @Override
        public void send(E value) {
            q.offer(value);
        }
        
        @Override
        public E receive() throws InterruptedException {
            return q.take();
        }
    }
    
    
    /*
    // this works
    private static class FutureChannel<E> implements Channel<E> {
        private CompletableFuture<E> future = new CompletableFuture<>();
        private static Object lock = new Object();

        public void send(E value) {
            synchronized (lock) {
                future.complete(value);
                future = new CompletableFuture<>();
            }
        }

        public E receive() throws InterruptedException, ExecutionException {
            return future.get();
        }
    }
*/

    public static void main(String[] args) {
        AtomicInteger message = new AtomicInteger(1);
        Channel<Integer> aToB = new QueueChannel<>();
        Channel<Integer> bToA = new QueueChannel<>();

        // Thread A
        Thread onIntentThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    int value = message.getAndIncrement();
                    System.err.println("A->B: " + value);

                    aToB.send(value);

                    try {
                        int received = bToA.receive();
                        System.err.println("A received: " + received);

                        if (received > 10) {
                            System.exit(0);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        };

        // Thread B
        Thread dialogosThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int received = aToB.receive();
                        System.err.println("B received: " + received);

                        int value = message.getAndIncrement();
                        System.err.println("B -> A: " + value);

                        bToA.send(value);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        };

        System.err.println("******");
        dialogosThread.start();
        onIntentThread.start();
    }

    @Override
    public void initialize() {
        Node.registerNodeTypes(getId(),
                Arrays.asList(new Class<?>[]{
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
