package io.anuke.arc.scene.actions;

import io.anuke.arc.scene.Action;

/**
 * Removes an actor from the stage.
 * @author Nathan Sweet
 */
public class RemoveActorAction extends Action{
    private boolean removed;

    public boolean act(float delta){
        if(!removed){
            removed = true;
            target.remove();
        }
        return true;
    }

    public void restart(){
        removed = false;
    }
}
