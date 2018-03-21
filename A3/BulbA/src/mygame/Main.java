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
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * 
 * @author Yuxuan Fan
 */
public class Main extends SimpleApplication implements AnimEventListener{

    private AnimChannel channel;
    private AnimControl control;
    Node player;
    Spatial bulb;
    //Boolean isSwimming=false;
    //Boolean isIdle = false;
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
    viewPort.setBackgroundColor(new ColorRGBA(135.0f/255.0f, 206.0f/255.0f, 250.0f/255.0f, 0));
    initKeys();
    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
    rootNode.addLight(dl);
   
    
    //Spatial bulb = assetManager.loadModel("Models/blub_quadrangulated.mesh.xml");
    //Spatial bulb = assetManager.loadModel("Models/Models/bulb/blub_quadrangulated.mesh.xml");
    //Spatial bulb = assetManager.loadModel("Models/bulb_backup1.j3o");
    
     /*bulb = assetManager.loadModel("Models/bulb_backup1.j3o");
    bulb.setLocalTranslation(0,0,0);
    bulb.rotate(0,180f*FastMath.DEG_TO_RAD,0);*/
    //bulb.setMaterial(assetManager.loadMaterial("Materials/bulbMat.j3m"));
    player = (Node)assetManager.loadModel("Models/bulb.j3o");

    //rootNode.attachChild(bulb);
    rootNode.attachChild(player);
    Node scene = new Node();
    scene = (Node)player.getChild(0);
    Node bone = new Node();
    bone = (Node)scene.getChild(0);
    Node bulb = new Node();
    bulb = (Node)bone.getChild(0);
   // bulb.setMaterial(assetManager.loadMaterial("Materials/bulbMat.j3m"));
    bulb.rotate(70f*FastMath.DEG_TO_RAD,0,0);
    //bulb.rotate(90f*FastMath.DEG_TO_RAD,0,0);
    control = bulb.getControl(AnimControl.class);
    //control = bulb.getControl(AnimControl.class);
    control.addListener(this);
    channel = control.createChannel();
    channel.setAnim("Stop");
    }

    @Override
  public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
//    if (animName.equals("Swimming")) {
//      channel.setAnim("Idle", 2.0f);
//      channel.setLoopMode(LoopMode.DontLoop);
//      channel.setSpeed(1f);
//    }
  }
  @Override
  public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    // unused
  }
  private void initKeys() {
    inputManager.addMapping("Turn", new KeyTrigger(KeyInput.KEY_T));
    inputManager.addMapping("Swimming", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Idle", new KeyTrigger(KeyInput.KEY_I));
    
    inputManager.addListener(actionListener, "Idle","Swimming","Turn");
    //inputManager.addListener(analogListener, "Turn");
    //inputManager.addListener();
  }
  

  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Swimming") && !keyPressed) {
        if (!channel.getAnimationName().equals("Swimming")) {
          channel.setAnim("Swimming", 2.0f);
          channel.setLoopMode(LoopMode.Loop);
         
        }
        else{
            channel.setAnim("Stop",2.0f);
            channel.setLoopMode(LoopMode.Loop);
        }
      }
      
      if (name.equals("Idle") && !keyPressed) {
        if (!channel.getAnimationName().equals("Idle")) {
          channel.setAnim("Idle", 2.0f);
          channel.setLoopMode(LoopMode.Loop);
          
        }
        else{
            channel.setAnim("Stop",2.0f);
            channel.setLoopMode(LoopMode.Loop);
        }
      }
      if (name.equals("Turn") && !keyPressed) {
          
        if (!channel.getAnimationName().equals("TurnBack")) {
          channel.setAnim("TurnBack", 2.0f);
          channel.setLoopMode(LoopMode.DontLoop);
         
        }
        
      }
      
      
    }
  };
  private AnalogListener analogListener = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {
        if(name.equals("Turn")){
            int i =0;
            if (!channel.getAnimationName().equals("Turn")) {
                        
                    channel.setAnim("Turn", 2.0f);
                    //channel.setLoopMode(LoopMode.DontLoop);
                    channel.setLoopMode(LoopMode.DontLoop);
                    i++;
               
            }
            
        }
    }
  };
}
