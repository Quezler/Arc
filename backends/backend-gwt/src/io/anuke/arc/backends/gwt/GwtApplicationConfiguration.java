package io.anuke.arc.backends.gwt;

import io.anuke.arc.Application;
import io.anuke.arc.backends.gwt.GwtGraphics.OrientationLockType;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

public class GwtApplicationConfiguration{
    /**
     * If true, SoundManager2 will not be used. This means {@link Application#getAudio()} returns null and the SoundManager2 file
     * are not used.
     */
    public boolean disableAudio;
    /** the width of the drawing area in pixels **/
    public int width;
    /** the height of the drawing area in pixels **/
    public int height;
    /** whether to use a stencil buffer **/
    public boolean stencil = false;
    /** whether to enable antialiasing **/
    public boolean antialiasing = false;
    /**
     * the Panel to add the WebGL canvas to, can be null in which case a Panel is added automatically to the body element of the
     * DOM
     **/
    public Panel rootPanel;
    /**
     * the id of a canvas element to be used as the drawing area, can be null in which case a Panel and Canvas are added to the
     * body element of the DOM
     **/
    public String canvasId;
    /** a TextArea to log messages to, can be null in which case a TextArea will be added to the body element of the DOM. */
    public TextArea log;
    /** whether to use debugging mode for OpenGL calls. Errors will result in a RuntimeException being thrown. */
    public boolean useDebugGL = false;
    /** whether SoundManager2 should prefer to use flash instead of html5 audio (it should fall back if not available) */
    public boolean preferFlash = true;
    /** preserve the back buffer, needed if you fetch a screenshot via canvas#toDataUrl, may have performance impact **/
    public boolean preserveDrawingBuffer = false;
    /**
     * whether to include an alpha channel in the color buffer to combine the color buffer with the rest of the webpage
     * effectively allows transparent backgrounds in GWT, at a performance cost.
     **/
    public boolean alpha = false;
    /** whether to use premultipliedalpha, may have performance impact **/
    public boolean premultipliedAlpha = false;
    /**
     * screen-orientation to attempt locking as the application enters full-screen-mode. Note that on mobile browsers, full-screen
     * mode can typically only be entered on a user gesture (click, tap, key-stroke)
     **/
    public OrientationLockType fullscreenOrientation;
    /* Whether openURI will open page in new tab. By default it will, however if may be blocked by popup blocker. */
    /* To prevent the page from being blocked you can redirect to the new page. However this will exit your game.  */
    public boolean openURLInNewWindow = true;

    public GwtApplicationConfiguration(int width, int height){
        this.width = width;
        this.height = height;
    }
}
