/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koller
 */
public class DialogosStreamHandler extends SkillStreamHandler {
//    private static final File dialogModel = copyResourceToFile(DialogosStreamHandler.class, "test.dos");
    private static final String DIALOG_MODEL = "branches.dos";
    
    public static void delay() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DialogosStreamHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(new LaunchRequestHandler(DIALOG_MODEL), new DialogosIntentHandler(DIALOG_MODEL))
                .build();
    }

    public DialogosStreamHandler() {
        super(getSkill());
    }

    
    /*
    private static void copyData(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }

    private static File copyResourceToFile(Class c, String resource) {
        try {
            File temp = File.createTempFile("dialogos", "dos");
            OutputStream os = new FileOutputStream(temp);
            InputStream is = c.getResource(resource).openStream();
            copyData(is, os);
            os.flush();
            os.close();

            // TODO clean up temp on system exit
            return temp;
        } catch (IOException ex) {
            Logger.getLogger(DialogosStreamHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
*/

}
