package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements AnimEventListener{
    private AnimChannel channel;
    private AnimControl control;
    Node player;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
  public void simpleInitApp() {
    viewPort.setBackgroundColor(ColorRGBA.LightGray);
    initKeys();
    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
    rootNode.addLight(dl);
    player = (Node) assetManager.loadModel("Models/fish2.j3o");
    player.setLocalScale(2.0f);
    rootNode.attachChild(player);
    control = player.getControl(AnimControl.class);
    control.addListener(this);
    channel = control.createChannel();
    channel.setAnim("none");
  }
  
   @Override
  public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    if (animName.equals("Swimming")) {
      channel.setAnim("none", 2.0f);
      channel.setLoopMode(LoopMode.DontLoop);
      channel.setSpeed(1f);
    }
  }
  @Override
  public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    // unused
  }

  /** Custom Keybinding: Map named actions to inputs. */
  private void initKeys() {
    inputManager.addMapping("Turn", new KeyTrigger(KeyInput.KEY_T));
    inputManager.addMapping("Swimming", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Idle", new KeyTrigger(KeyInput.KEY_I));
    
    inputManager.addListener(actionListener, "Idle","Swimming");
  }
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Swimming") && !keyPressed) {
        if (!channel.getAnimationName().equals("Swimming")) {
          channel.setAnim("Swimming", 2.0f);
          channel.setLoopMode(LoopMode.Loop);
        }
      }
      
      if (name.equals("Idle") && !keyPressed) {
        if (!channel.getAnimationName().equals("Idle")) {
          channel.setAnim("Idle", 2.0f);
          channel.setLoopMode(LoopMode.Loop);
        }
      }
    }
  };
}
