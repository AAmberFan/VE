package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;

import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.HillHeightMap; // for exercise 2
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;
import java.util.List;
import com.jme3.scene.Spatial;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;

import com.jme3.scene.Geometry;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;

import com.jme3.scene.Node;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;


/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener{

    private TerrainQuad terrain;
    Material mat_terrain;
   // protected Spatial player;
    
    
    //private Spatial sceneModel;
    private BulletAppState bulletAppState;
   // private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;

  //Temporary vectors used on each frame.
  //They here to avoid instanciating new vectors on each frame
    //private Vector3f camDir;
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    
    private Node characterNode;
   
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
        
    }
    

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(100);
        
        
         viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        /** Question1*/
        /** 1. Create terrain material and load four textures into it. */
         mat_terrain = new Material(assetManager,  "Common/MatDefs/Terrain/Terrain.j3md");
         
        /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
         mat_terrain.setTexture("Alpha", assetManager.loadTexture(
            "rgbmap.png"));

        /** 1.2) Add GRASS texture into the red layer (Tex1). */
        Texture grass = assetManager.loadTexture("grass.jpeg");
        grass.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex1", grass);
        mat_terrain.setFloat("Tex1Scale", 64f);
        
       
         /** 1.4) Add gravel texture into the blue layer (Tex3) rock->gravel*/
        Texture gravel = assetManager.loadTexture( "gravel.jpeg");
        gravel.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", gravel);
        mat_terrain.setFloat("Tex2Scale", 32f);
        
         /** 1.3) Add WALL texture into the green layer (Tex2) dirt->wall*/
        Texture wall = assetManager.loadTexture( "wall.jpg");
        wall.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex3", wall);//still have some problems
        mat_terrain.setFloat("Tex3Scale", 64f);
        

        /** 2. Create the height map */
        AbstractHeightMap heightmap = null;
        Texture heightMapImage = assetManager.loadTexture(
            "heightmap.png");
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.load();
        
        /** 3. We have prepared material and heightmap.
        
        */
        int patchSize = 65;
        terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());

        /** 4. We give the terrain its material, position & scale it, and attach it. */
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 0.6f, 2f);
        rootNode.attachChild(terrain);

        /** 5. The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        terrain.addControl(control);
//        List<Camera> cameras = new ArrayList<Camera>();
//        cameras.add(getCamera());
//        TerrainLodControl control = new TerrainLodControl(terrain, cameras);
//        terrain.addControl(control);
        
        
        /** Question2* the rotate /
        /** 1.Load dinosaur */
        Spatial dinosaur[];
        dinosaur = new Spatial[4];
        for(int i = 0; i< 4;i++){
            dinosaur[i] = assetManager.loadModel("triceratops.j3o");
            dinosaur[i].scale(2.5f, 2.5f, 2.5f);
            //dinosaur[i] .rotate(-40.0f, 0.0f, 40.0f);
            //dinosaur[i] .setLocalTranslation(0.0f, 0.0f, 0.0f);
        }
        /* This quaternion stores a 180 degree rolling rotation */
       
        
        
        dinosaur[0] .setLocalTranslation(180.0f, 45.0f, 180.0f);
        dinosaur[0].rotate(0f, 0f, 50f*FastMath.DEG_TO_RAD);
        dinosaur[1] .setLocalTranslation(-180.0f, 45.0f, 180.0f);
        dinosaur[0].rotate(0f, 0f, 80f*FastMath.DEG_TO_RAD);
        dinosaur[2] .setLocalTranslation(180.0f, 45.0f, -180.0f);
        dinosaur[0].rotate(0f, 0f, -6f);
        dinosaur[3] .setLocalTranslation(-180.0f, 45.0f, -180.0f);
        dinosaur[0].rotate(0f, 0f, -6f);
       
        for(int i = 0; i< 4;i++){
            rootNode.attachChild(dinosaur[i] );
        }
        
        
        /** Question3 Grog*/
        
        
        Spatial grog = assetManager.loadModel("grog5k.j3o");
        grog.scale(0.7f, 0.7f, 0.7f);
        grog.rotate(0.0f, 0.0f, 0.0f);
        //grog.setLocalTranslation(0, -100, 0);
        
        
        rootNode.attachChild(grog);
        
        /**  need to have light to make the stuff visible*/
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-100f, -100f, 0f));
        rootNode.addLight(sun);

        //setUpLight();

        /**Question4 Grog Interaction*/
        //still have a question that the scence moves with the grog
        
        /** Set up Physics */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        //player = grog;
        
        initKeys(); // load my custom keybinding
        
        /**
         * Qustion5 to ensure that the grog stays on the surface of your terrain and does not submerge
         */
        
        /**  Add physics: */
        // We set up collision detection for the scene by creating a static
        /*RigidBodyControl with mass zero.*/
        
        terrain.addControl(new RigidBodyControl(0));
        bulletAppState.getPhysicsSpace().add(terrain);
       
        
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(15f, 55.0f, 1);
        player = new CharacterControl(capsuleShape, 0.5f);
        
        
        
        Vector3f v = grog.getLocalTranslation();
        player.setPhysicsLocation(new Vector3f(v.x, v.y, v.z));
        characterNode = new Node("character node");
        characterNode.setLocalTranslation(0.0f, -60.0f, 0.0f);
        characterNode.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
        characterNode.attachChild(grog);
        rootNode.attachChild(characterNode);
        
        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        bulletAppState.getPhysicsSpace().add(terrain);
       

    }

    
    private void initKeys() {
    // You can map one or several inputs to one named action
    inputManager.addMapping("Up",  new KeyTrigger(KeyInput.KEY_W));//the interactive keys are same with camera keys
    inputManager.addMapping("Down",  new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_D));
   
    // Add the names to the action listener.
    
   // inputManager.addListener(analogListener,"Left", "Right", "Up","Down");
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
   

    }
    
//    private AnalogListener analogListener = new AnalogListener() {
//    public void onAnalog(String name, float value, float tpf) {
//      
//          Vector3f v = player.getLocalTranslation();
//          
//          
//        if (name.equals("Up")) {
//          //Vector3f v = player.getLocalTranslation();
//          player.setLocalTranslation(v.x, v.y, v.z - value*speed*100);
//          up = true;
//         // Vector3f playDir = player.getLocalTranslation().clone().multLocal(0.6f);
//                  
//        }
//        if (name.equals("Down")) {
//          //Vector3f v = player.getLocalTranslation();
//          player.setLocalTranslation(v.x, v.y, v.z + value*speed*100);
//          down = true;
//        }
//        if (name.equals("Right")) {
//          //Vector3f v = player.getLocalTranslation();
//          player.setLocalTranslation(v.x + value*speed*100, v.y, v.z);
//          right = true;
//        }
//        if (name.equals("Left")) {
//         // Vector3f v = player.getLocalTranslation();
//          player.setLocalTranslation(v.x - value*speed*100, v.y, v.z);
//          left = true;
//        }
//
//     
//    }
//    
//    };
     public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Left")) {
      if (value) { left = true; } else { left = false; }
    } else if (binding.equals("Right")) {
      if (value) { right = true; } else { right = false; }
    } else if (binding.equals("Up")) {
      if (value) { up = true; } else { up = false; }
    } else if (binding.equals("Down")) {
      if (value) { down = true; } else { down = false; }
    } 
  }

  
    @Override
  public void simpleUpdate(float tpf) {
      //cam is default third-person camera 
    Vector3f camDir = cam.getDirection().clone().multLocal(1f);
    Vector3f camLeft = cam.getLeft().clone().multLocal(1f);
    walkDirection.set(0, 0, 0);
    if (left)  { walkDirection.addLocal(camLeft); }
    if (right) { walkDirection.addLocal(camLeft.negate()); }
    if (up)    { walkDirection.addLocal(camDir); }
    if (down)  { walkDirection.addLocal(camDir.negate()); }
    player.setWalkDirection(walkDirection);
    
    Vector3f v = player.getPhysicsLocation();
    Vector3f d = player.getViewDirection();
    //cam.setLocation(new Vector3f(v.x+30f,v.y+130f,v.z+130f));
    cam.setLocation(new Vector3f(v.x+30f,v.y+130f,v.z+130f));
    //cam.setRotation(Quaternion.IDENTITY);
    
    //cam.lookAtDirection(new Vector3f(d.x+10f,d.y,d.z+10f), new Vector3f(v.x+120f,v.y+120f,v.z+120f));
  }

    
} 
