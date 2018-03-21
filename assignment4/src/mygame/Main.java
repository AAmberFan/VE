package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.*;
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
import com.jme3.scene.Spatial;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.bullet.control.VehicleControl;
 

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author Yuxuan
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    /** Prepare the Physics Application State (jBullet) */
    private BulletAppState bulletAppState;
    
    private SphereCollisionShape bulletCollisionShape;
    
    private CollisionShape boxscene;
    private CollisionShape trayColl;
    
    /** Prepare geometries and physical nodes for bricks and cannon balls. */
    private RigidBodyControl    box_phy;
    private static final Box    box;
    private RigidBodyControl    ball_phy;
    private static final Sphere sphere;
    private RigidBodyControl    tray_phy;
    private RigidBodyControl    bottom_phy;
    private RigidBodyControl    right_phy;
    private RigidBodyControl    left_phy;
    private RigidBodyControl    front_phy;
    private RigidBodyControl    back_phy;
    private Node fish;
    private Spatial bulb;
    private VehicleControl player;
    private boolean left = false, right = false, up = false, down = false;
    //private static final Box    tray;
    private static final Box    trayBottom;
    private static final Box    trayFront;
    private static final Box    trayLeft;
    private HingeJoint joint;
    private Vector3f walkDirection = new Vector3f();
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();

    /** Prepare Materials */
    Material box_mat;
    Material ball_mat;
    Material tray_mat;
    Material box1_mat;
    
     static {
    /** Initialize the ball geometry */
    sphere = new Sphere(32, 32, 10.0f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    /** Initialize the box geometry */
    box = new Box(160.0f, 200.0f, 100.0f);
    //box.scaleTextureCoordinates(new Vector2f(1f, .5f));
    
    /** Initialize the tray geometry */
   // tray = new Box(20f, 20f, 2.0f);
    trayBottom = new Box(50f, 0.5f, 20f);
    trayFront = new Box(50f, 20f, 0.5f);
    trayLeft = new Box(20f, 20f, 0.5f);

  }
     
    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(135.0f/255.0f, 206.0f/255.0f, 250.0f/255.0f, 0));
        flyCam.setMoveSpeed(100);
        flyCam.setDragToRotate(true);
        setDisplayFps(false);
        setDisplayStatView(false);
//        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-100f, -100f, 0f));
        rootNode.addLight(sun);
        
        cam.setLocation(new Vector3f(0, 0.0f, 500.0f));
        /** Set up Physics Game */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState); 
        initMaterials();
         
        initBox();
        Node trayNode1 = new Node();
        trayNode1.setLocalTranslation(60f,30f, -30f);
        iniTray(trayNode1);
        
        Node trayNode2 = new Node();
        trayNode2.setLocalTranslation(-70f, -90f, 0f);
        iniTray(trayNode2);
        
        Node trayNode3 = new Node();
        trayNode3.setLocalTranslation(-30f, 80f, 20f);
        iniTray(trayNode3);
        /*the fish */
        bulb = assetManager.loadModel("Models/bulb.j3o");
        bulb.scale(20);
        bulb.rotate(0, -90f*FastMath.DEG_TO_RAD, 0);
        //rootNode.attachChild(bulb);
        rootNode.attachChild(bulb);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(15f, 30.0f, 1);
        player = new VehicleControl(capsuleShape, 1f);
        player.setGravity(new Vector3f(0,-30,0));
        bulb.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
        /* when hitting the space, a new ball can be created
         */
        inputManager.addMapping("MakeBall", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addListener(actionListener, "MakeBall","Left","Right","Up","Down");
        
        
    }
   
    /**
   * Every time the shoot action is triggered, a new cannon ball is produced.
   * The ball is set up to fly from the camera position in the camera direction.
   */
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
          if (name.equals("MakeBall") && !keyPressed) {
            makeBall();
          }
          
          if (name.equals("Left")) {
              if (keyPressed) { left = true; } 
              else { left = false; }
        } else if (name.equals("Right")) {
              if (keyPressed) { right = true; } 
              else { right = false; }
        } else if (name.equals("Up")) {
              if (keyPressed) { up = true; } 
              else { up = false; }
        } else if (name.equals("Down")) {
          if (keyPressed) { down = true; }
          else { down = false; }
        } 
        }
    };
    
    

    public void initMaterials() {
        box_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        box_mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        Texture tex = assetManager.loadTexture("Textures/wall.jpeg");
        tex.setWrap(WrapMode.Repeat);
        box_mat.setTexture("ColorMap", tex);
    
        tray_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        tray_mat.setColor("Color", new ColorRGBA( 0.7f, 0.7f, 0.7f, 0.5f));
        tray_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        ball_mat =  new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        ball_mat.setColor("Color", ColorRGBA.White);
        
        box1_mat =  new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        box1_mat.setColor("Color", ColorRGBA.White);
        box1_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        //tray_mat.setTexture("traycolor",tex);
  }
    
     /** Make a solid Box and add it to the scene. */
    public void initBox() {
        Geometry box_geo = new Geometry("Box", box);
        box_geo.setMaterial(box_mat);
        box_geo.setLocalTranslation(0, -0.1f, -25f);
        box_geo.setShadowMode(RenderQueue.ShadowMode.Receive);
        boxscene = CollisionShapeFactory.createMeshShape(box_geo);
        this.rootNode.attachChild(box_geo);
        
        /* Make the box physical with mass 0.0f! */
        box_phy = new RigidBodyControl(boxscene,0.0f);
        box_geo.addControl(box_phy);
        bulletAppState.getPhysicsSpace().add(box_phy);
        
    }
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
        float randNum4 = (float)(-80f + Math.random()*(160f));
        ball_geo.setLocalTranslation(randNum,140.0f,randNum4);
        /** Make the ball physcial with a mass > 0.0f */
        bulletCollisionShape = new SphereCollisionShape(10.0f);
        ball_phy = new RigidBodyControl(bulletCollisionShape,10f);
        ball_phy.setGravity(new Vector3f(0,-10,0));
        /** Add physical ball to physics space. */
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        float ranNum2 = (float)(-10.0f + Math.random()*(20.0f));
        
        float ranNum3 = (float)(10.0f+ Math.random()*(40.0f));
        ball_phy.setGravity(new Vector3f(0,-20,0f));
        /** Accelerate the physcial ball to shoot it. */
        ball_phy.setLinearVelocity(new Vector3f(ranNum2,-10,ranNum2).mult(ranNum3));
        
  }
    
    public void iniTray(Node trayNode){ 
        Node sideNode = new Node();
        trayNode.attachChild(sideNode);
        /* left box*/
        Node leftNode = new Node();
        Geometry tray_left = new Geometry("trayleft",trayLeft);
        tray_left.setMaterial(tray_mat);
        tray_left.rotate(0, -90f*FastMath.DEG_TO_RAD, 0);
        tray_left.setQueueBucket(Bucket.Transparent); 
        leftNode.attachChild(tray_left);
        leftNode.setLocalTranslation(-50.0f, 0, 0);
        sideNode.attachChild(leftNode);
        left_phy = new RigidBodyControl(0);
        sideNode.addControl(left_phy);
        //left_phy.setPhysicsLocation(new Vector3f(sideNode.getWorldTranslation().x-50,sideNode.getWorldTranslation().y-20,sideNode.getWorldTranslation().z));
        bulletAppState.getPhysicsSpace().add(left_phy);
        //System.out.println("leftphy"+left_phy.getPhysicsLocation());
        /*right*/
        Node rightNode = new Node();
        Geometry tray_right = new Geometry("trayright",trayLeft);
        tray_right.setMaterial(tray_mat);
        tray_right.rotate(0, -90f*FastMath.DEG_TO_RAD, 0);
        tray_right.setQueueBucket(Bucket.Transparent); 
        rightNode.attachChild(tray_right);
        rightNode.setLocalTranslation(50.0f, 0, 0);
        sideNode.attachChild(rightNode);
        right_phy = new RigidBodyControl(0);
        sideNode.addControl(right_phy);
        //left_phy.setPhysicsLocation(new Vector3f(sideNode.getWorldTranslation().x-50,sideNode.getWorldTranslation().y-20,sideNode.getWorldTranslation().z));
        bulletAppState.getPhysicsSpace().add(right_phy);
        /*front */
        Node frontNode = new Node();
        Geometry tray_front = new Geometry("trayfront",trayFront);
        tray_front.setMaterial(tray_mat);
        tray_front.setQueueBucket(Bucket.Transparent); 
        frontNode.attachChild(tray_front);
        frontNode.setLocalTranslation(0, 0, 20);
        sideNode.attachChild(frontNode);
        front_phy = new RigidBodyControl(0);
        sideNode.addControl(front_phy);
        //left_phy.setPhysicsLocation(new Vector3f(sideNode.getWorldTranslation().x-50,sideNode.getWorldTranslation().y-20,sideNode.getWorldTranslation().z));
        bulletAppState.getPhysicsSpace().add(front_phy);
        
        /*back*/
        Node backNode = new Node();
        Geometry tray_back = new Geometry("trayback",trayFront);
        tray_back.setMaterial(tray_mat);
        tray_back.setQueueBucket(Bucket.Transparent); 
        backNode.attachChild(tray_back);
        backNode.setLocalTranslation(0, 0, -20);
        sideNode.attachChild(backNode);
        back_phy = new RigidBodyControl(0);
        sideNode.addControl(back_phy);
        //left_phy.setPhysicsLocation(new Vector3f(sideNode.getWorldTranslation().x-50,sideNode.getWorldTranslation().y-20,sideNode.getWorldTranslation().z));
        bulletAppState.getPhysicsSpace().add(back_phy);
        
//        side_phy = new RigidBodyControl(new BoxCollisionShape(new Vector3f(20f, 20f, 0.5f)),0);
//        sideNode.addControl(side_phy);
//        bulletAppState.getPhysicsSpace().add(side_phy);
        
        
        
        /*BOTTOM    */
        Node bottomNode = new Node();
        Geometry tray_bottom = new Geometry("trayBottom",trayBottom);
        tray_bottom.setMaterial(tray_mat);
       // tray_bottom.rotate(-90f*FastMath.DEG_TO_RAD, 0, 0);
        tray_bottom.setQueueBucket(Bucket.Transparent); 
        bottomNode.attachChild(tray_bottom);
        bottomNode.setLocalTranslation(0 ,-20, 0);
        trayNode.attachChild(bottomNode);
        
        bottom_phy = new RigidBodyControl(new BoxCollisionShape(new Vector3f(50f, 0.5f, 20f)),0.1f);//0 1
        bottom_phy.setGravity(new Vector3f(0,0,0));
        bottomNode.addControl(bottom_phy);
        //bottom_phy.setPhysicsLocation(new Vector3f(bottomNode.getWorldTranslation().x,bottomNode.getWorldTranslation().y,bottomNode.getWorldTranslation().z));
//        ;
//        
        bulletAppState.getPhysicsSpace().add(bottomNode);

        rootNode.attachChild(trayNode);
        
         /*Hinge joint */
        /*fixed node*/
        Node fixNode = new Node();
        Sphere dyBox = new Sphere(32, 32, 1.0f, true, false);
        Node dyBoxNode = new Node();
        Geometry dyBox_geo = new Geometry("dynamic",dyBox);
        dyBox_geo.setMaterial(box1_mat);
        
        dyBoxNode.attachChild(dyBox_geo);
        dyBoxNode.setLocalTranslation(-50f-1f ,-20, 0);
        trayNode.attachChild(dyBoxNode);
        
        RigidBodyControl dyBox_phy = new RigidBodyControl(new SphereCollisionShape(1),0);
        //dyBox_phy.setPhysicsLocation(new Vector3f(bottomNode.getWorldTranslation().x-50f,bottomNode.getWorldTranslation().y,bottomNode.getWorldTranslation().z));

        dyBoxNode.addControl(dyBox_phy);
        bulletAppState.getPhysicsSpace().add(dyBox_phy);
        
        System.out.println(dyBox_phy.getPhysicsLocation()+","+bottom_phy.getPhysicsLocation());
         joint=new HingeJoint(dyBox_phy, bottom_phy, 
                Vector3f.ZERO, new Vector3f(-50f-1f,0,0f),
                Vector3f.UNIT_Z, Vector3f.UNIT_Z);
        bulletAppState.getPhysicsSpace().add(joint);
        joint.enableMotor(true, 0f, 0.5f);
        
        joint.setLimit(-0.2f, 0f);
        

    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        Vector3f camDir = cam.getUp().clone().multLocal(2f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(1f);
        walkDirection.set(0, 0, 0);
        if (left)  {  player.applyCentralForce(new Vector3f(-60.0f, 0.0f, 0.0f)); }
        if (right) {  player.applyCentralForce(new Vector3f(60.0f, 0.0f, 0.0f)); }
        if (up)    {  player.applyCentralForce(new Vector3f(0f, 100.0f, 0.0f)); }
        if (down)  {  player.applyCentralForce(new Vector3f(0f, -60.0f, 0.0f)); }
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
