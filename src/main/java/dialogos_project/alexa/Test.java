/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

/**
 *
 * @author koller
 */
public class Test {
    private static void copyData(InputStream in, OutputStream out) throws IOException  {
        byte[] buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }

    private static File copyResourceToFile(String resource) throws IOException {
        File temp = File.createTempFile("dialogos", "dos");
        OutputStream os = new FileOutputStream(temp);
        InputStream is = Test.class.getResource(resource).openStream();
        copyData(is, os);
        os.flush();
        os.close();
        
        // TODO clean up temp on system exit
        
        return temp;
    }

    public static void main(String[] args) throws URISyntaxException, FileNotFoundException, IOException {
        File f = copyResourceToFile("test.dos");
        
        
        
//        
//        String dialogURI = Test.class.getResource("test.dos").toExternalForm();
//        URI x = new URI(dialogURI);
//        System.err.println("str: " + dialogURI);
//        System.err.println("x: " + x);
//        File f = new File(x);
//
        BufferedReader r = new BufferedReader(new FileReader(f));
        for (int i = 0; i < 5; i++) {
            System.out.println(r.readLine());
        }
    }
}
