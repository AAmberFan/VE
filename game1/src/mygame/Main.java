package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.light.DirectionalLight;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.util.SkyFactory;
import com.jme3.scene.Spatial;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    /** Prepare the Physics Application State (jBullet) */
    private BulletAppState bulletAppState;
    private SphereCollisionShape bulletCollisionShape;
    
    private RigidBodyControl    boxBack_phy, boxLeft_phy, boxRight_phy, boxBottom_phy;
    private RigidBodyControl ball_phy;
    private static  Sphere  sphere;
    
    /** Prepare Materials */
      Material box_mat;
      Material ball_mat;
      Material tray_mat;
      
    @Override
    public void simpleInitApp() {
        /* Geometry box_geo = new Geometry("Box", box);
        box_geo.setMaterial(box_mat);
        //box_geo.setLocalTranslation(0, -0.1f, 0);
        this.rootNode.attachChild(box_geo);
       
        box_phy = new RigidBodyControl(0.0f);
        box_geo.addControl(box_phy);
        bulletAppState.getPhysicsSpace().add(box_phy);*/
        initMaterials(); 
        cam.setLocation(new Vector3f(0, 0.0f, 200.0f));
        /** Set up Physics Game */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState); 
       
        /** the box containts the whole stuff*/
        Box boxBack = new Box( 80.0f, 100.0f ,5.0f);
        Box boxLeft = new Box(50.0f, 100.0f, 5.0f);
        Box boxRight  = new Box(50.0f, 100.0f, 5.0f);
        Box boxBottom = new Box(80.0f , 50.0f, 5.0f);
        
        Geometry boxBack_geo = new Geometry("BoxBack", boxBack);
        boxBack_geo.setMaterial(box_mat);
        boxBack_geo.setLocalTranslation(0f, 0, -100.0f);
        rootNode.attachChild(boxBack_geo);
        boxBack_phy = new RigidBodyControl(0.0f);
        boxBack_geo.addControl(boxBack_phy);
        bulletAppState.getPhysicsSpace().add(boxBack_phy);
        
        Geometry boxLeft_geo = new Geometry("BoxLeft", boxLeft);
        boxLeft_geo.setMaterial(box_mat);
        boxLeft_geo.setLocalTranslation(-70.0f, 0, -75.0f);
        boxLeft_geo.rotate(0,-90f*FastMath.DEG_TO_RAD,0);
        rootNode.attachChild(boxLeft_geo);
        boxLeft_phy = new RigidBodyControl(0.0f);
        boxLeft_geo.addControl(boxLeft_phy);
        bulletAppState.getPhysicsSpace().add(boxLeft_phy);
        
        Geometry boxRight_geo = new Geometry("BoxRight", boxRight);
        boxRight_geo.setMaterial(box_mat);
        boxRight_geo.setLocalTranslation(70.0f, 0, -75.0f);
        boxRight_geo.rotate(0,-90f*FastMath.DEG_TO_RAD,0);
        rootNode.attachChild(boxRight_geo);
        boxRight_phy = new RigidBodyControl(0.0f);
        boxRight_geo.addControl(boxRight_phy);
        bulletAppState.getPhysicsSpace().add(boxRight_phy);
        
        Geometry boxBottom_geo = new Geometry("BoxBottom", boxBottom);
        boxBottom_geo.setMaterial(box_mat);
        boxBottom_geo.setLocalTranslation(0f, -100.0f, -75.0f);
        boxBottom_geo.rotate(-90f*FastMath.DEG_TO_RAD,0,0);
        rootNode.attachChild(boxBottom_geo);
        boxBottom_phy = new RigidBodyControl(0.0f);
        boxBottom_geo.addControl(boxBottom_phy);
        bulletAppState.getPhysicsSpace().add(boxBottom_phy);
        
        sphere = new Sphere(32, 32, 5f, true, false);
        sphere.setTextureMode(TextureMode.Projected);
    
        inputManager.addMapping("MakeBall", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "MakeBall");
        
    }
    private ActionListener actionListener = new ActionListener() {
            public void onAction(String name, boolean keyPressed, float tpf) {
              if (name.equals("MakeBall") && !keyPressed) {
                makeBall();
              }
            }
        };
    /** This method creates one individual physical  ball.
   * the ball start location arbitrarily anywhere on the top plane of the box*/
    public void makeBall() {
        /** Create a cannon ball geometry and attach to scene graph. */
        Geometry ball_geo = new Geometry("ball", sphere);
        ball_geo.setMaterial(ball_mat);
        ball_geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(ball_geo);

        /** Position the cannon ball  */

        float randNum = (float)(-75.0f+Math.random()*(149.9f));
        ball_geo.setLocalTranslation(randNum,100.0f,-55f);
        /** Make the ball physcial with a mass > 0.0f */
        bulletCollisionShape = new SphereCollisionShape(5.0f);
        ball_phy = new RigidBodyControl(bulletCollisionShape,1f);
        
        /** Add physical ball to physics space. */
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        ball_phy.setGravity(new Vector3f(0,-10,0f));
        /** Accelerate the physcial ball to shoot it. */
        ball_phy.setLinearVelocity(new Vector3f(0,-10,0).mult(25));
  }
    
     public void initMaterials() {
        box_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        Texture tex = assetManager.loadTexture("Textures/wall.jpeg");
        tex.setWrap(WrapMode.Repeat);
        box_mat.setTexture("ColorMap", tex);
        
        ball_mat =  new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        ball_mat.setColor("Color", ColorRGBA.White);
     }
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
