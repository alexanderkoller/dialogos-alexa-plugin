/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import com.clt.dialogos.DialogOS;
//import com.clt.diamant.InputOutputSynchronizer;
//import com.clt.diamant.QueueInputOutputSynchronizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koller
 */
public class DialogosStreamHandler extends SkillStreamHandler {
//    private static InputOutputSynchronizer<HandlerInput, Optional<Response>> synchronizer = new QueueInputOutputSynchronizer<>();

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(new DialogosIntentHandler())
                .build();
    }

    public DialogosStreamHandler() {
        super(getSkill());

        try {
            File dialogModel = copyResourceToFile(DialogosStreamHandler.class, "test.dos");
            DialogOS.run(dialogModel, true, true, true);
        } catch (Exception ex) {
            Logger.getLogger(DialogosStreamHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws URISyntaxException, FileNotFoundException, IOException, Exception {
        File dialogModel = copyResourceToFile(DialogosStreamHandler.class, "test.dos");
//        InputOutputSynchronizer<HandlerInput, String> synchronizer = new QueueInputOutputSynchronizer<>();

        DialogOS.run(dialogModel, true, true, true);
        
    }

    private static void copyData(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }

    private static File copyResourceToFile(Class c, String resource) throws IOException {
        File temp = File.createTempFile("dialogos", "dos");
        OutputStream os = new FileOutputStream(temp);
        InputStream is = c.getResource(resource).openStream();
        copyData(is, os);
        os.flush();
        os.close();

        // TODO clean up temp on system exit
        return temp;
    }

}
