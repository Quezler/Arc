package io.anuke.arc.scene.actions;

import io.anuke.arc.scene.Action;
import io.anuke.arc.scene.utils.Layout;

/**
 * Sets an actor's {@link Layout#setLayoutEnabled(boolean) layout} to enabled or disabled. The actor must implements
 * {@link Layout}.
 * @author Nathan Sweet
 */
public class LayoutAction extends Action{
    private boolean enabled;

    public boolean act(float delta){
        target.setLayoutEnabled(enabled);
        return true;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setLayoutEnabled(boolean enabled){
        this.enabled = enabled;
    }
}
