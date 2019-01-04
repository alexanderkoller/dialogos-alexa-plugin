/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogos_project.alexa;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;

/**
 *
 * @author koller
 */
public class DialogosStreamHandler extends SkillStreamHandler {
    private static final String DIALOG_MODEL = "branches.dos";
    
    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new LaunchRequestHandler(DIALOG_MODEL), 
                        new DialogosIntentHandler(DIALOG_MODEL)
                ).build();
    }

    public DialogosStreamHandler() {
        super(getSkill());
    }
}
