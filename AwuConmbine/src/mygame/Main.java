package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.*;
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
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.CompareMode;
import com.jme3.shadow.PssmShadowRenderer.FilterMode;
import com.jme3.font.BitmapText;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.bullet.PhysicsSpace;
import static com.jme3.bullet.PhysicsSpace.getPhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.objects.PhysicsVehicle;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author Yuxuan
 */
public class Main extends SimpleApplication implements AnimEventListener,PhysicsCollisionListener,ScreenController{

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
    Geometry ball_geo;
    int nameIndex;
    Geometry cc = new Geometry();
    private RigidBodyControl    tray_phy;
    private RigidBodyControl    bottom_phy;
    private RigidBodyControl    right_phy;
    private RigidBodyControl    left_phy;
    private RigidBodyControl    front_phy;
    private RigidBodyControl    back_phy;
    private Spatial bulb;
    private RigidBodyControl player;
    private boolean left = false, right = false, up = false, down = false;
    private boolean front = false, back = false;
    private static final Box    trayBottom;
    private static final Box    trayFront;
    private static final Box    trayLeft;
    private HingeJoint joint;
    private Vector3f walkDirection = new Vector3f();
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();

    public int i = 0;
    BitmapText helloText;
    PssmShadowRenderer bsr;
    Node bulbAni = new Node();
    /** Prepare Materials */
    Material box_mat;
    Material ball_mat, matnew;
    Material tray_mat;
    Material box1_mat;
    Material shootball_mat;
    /*Audio*/
    private AudioNode audio_hit;
    private AudioNode audio_shoot;
    /*animation*/
    private AnimChannel channel;
    private AnimControl control;
    /*text*/
    private Nifty nifty;
    int ballNum = 0; //record the num of ball
    /* when the bulb hit the fish, the ball will disapper, and get a score*/
    private int score = 0; 
    
     static {
    /** Initialize the ball geometry */
    sphere = new Sphere(32, 32, 10.0f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    
    /** Initialize the box geometry */
    box = new Box(200.0f, 200.0f, 100.0f);
    
    /** Initialize the tray geometry */
    trayBottom = new Box(51f, 0.5f, 21f);
    trayFront = new Box(50f, 20f, 0.5f);
    trayLeft = new Box(20f, 20f, 0.5f);
    
  }
     
    @Override
    public void simpleInitApp() {
        /*screen*/
        iniScreen();
        
        viewPort.setBackgroundColor(new ColorRGBA(135.0f/255.0f, 206.0f/255.0f, 250.0f/255.0f, 0));
        flyCam.setMoveSpeed(100);
        flyCam.setDragToRotate(true);
        setDisplayFps(false);
        setDisplayStatView(false);
        /*shadow*/
        bsr = new PssmShadowRenderer(assetManager, 1024, 2);
        bsr.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        bsr.setLambda(0.55f);
        bsr.setShadowIntensity(0.6f);
        bsr.setCompareMode(CompareMode.Hardware);
        bsr.setFilterMode(FilterMode.PCF4);
        viewPort.addProcessor(bsr);
       
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-100f, -100f, 0f));
        rootNode.addLight(sun);
        
        cam.setLocation(new Vector3f(0, 0.0f, 600.0f));
        /** Set up Physics Game */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        //iniText();
        initMaterials();  
        initBox();
        iniKey();
        initAudio();
        
        /*initray*/
        Node trayNode1 = new Node();
        trayNode1.setLocalTranslation(60f,30f, -30f);
        iniTray(trayNode1);
        
        Node trayNode2 = new Node();
        trayNode2.setLocalTranslation(-70f, -90f, 0f);
        iniTray(trayNode2);
        
        Node trayNode3 = new Node();
        trayNode3.setLocalTranslation(-30f, 80f, 20f);
        iniTray(trayNode3);


        createChar();
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    public void iniScreen(){
        myStartScreen startSceen = new myStartScreen();
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/showGUI.xml", "start", startSceen);

        guiViewPort.addProcessor(niftyDisplay);
    }
    
    public void createChar(){
         /*the fish */
        bulb = assetManager.loadModel("Models/bulb.j3o");
        bulb.scale(20);
        bulb.rotate(0, 0f*FastMath.DEG_TO_RAD, 0);
        rootNode.attachChild(bulb);
        
        bulbAni = (Node)(((Node)(((Node)(((Node)bulb).getChild(0))).getChild(0))).getChild(0));
        control = bulbAni.getControl(AnimControl.class);
  
        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("Idle");
        BoxCollisionShape capsuleShape = new BoxCollisionShape(new Vector3f(30,30,30));
        player = new RigidBodyControl(capsuleShape, 4f);
        
        bulb.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
    }
    
    public void iniText(){
        /*Text*/
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/BodoniMTBlack.fnt");
        
        BitmapText ballText ;
        ballText = new BitmapText(guiFont, false);
        ballText.setSize(guiFont.getCharSet().getRenderedSize());
        String ballStr = String.valueOf(ballNum);
        ballText.setText("THE NUMBER OF BALL: "+ballStr);
        guiNode.attachChild(ballText);
        ballText.setLocalTranslation(20, ballText.getLineHeight()+10, 10);
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
          if (name.equals("MakeBall") && !keyPressed) {
              if (ballNum <= 100){
                makeBall();
                ballNum++;
                audio_hit.playInstance();
              }
          }
          if (name.equals("Left")) {
              if (keyPressed) { left = true; 
              
              } 
              else { left = false; }
        } else if (name.equals("Right")) {
              if (keyPressed) { right = true;
             } 
              else { right = false; }
        } else if (name.equals("Up")) {
              if (keyPressed) { up = true; 
              } 
              else { up = false; }
        } else if (name.equals("Down")) {
          if (keyPressed) { down = true;
          }
          else { down = false; }
        } else if (name.equals("Front")) {
          if (keyPressed) { front = true;
          }
          else { front = false; }
        }else if (name.equals("Back")) {
          if (keyPressed) { back = true;
          }
          else { back = false; }
        }
          
        }//onAction
    };
    
    private AnalogListener analogListener = new AnalogListener() {
     public void onAnalog(String name, float intensity, float tpf) {  
    }
  };
      
    public void iniKey(){
        inputManager.addMapping("MakeBall", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Front", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addListener(actionListener, "MakeBall","Left","Right","Up","Down","Front","Back");
      }
      
    private void initAudio() {
        audio_hit = new AudioNode(assetManager, "Sounds/sound1.wav", DataType.Buffer);
        audio_hit.setPositional(false);
        audio_hit.setLooping(false);
        audio_hit.setVolume(0.5f);
        rootNode.attachChild(audio_hit);
        audio_shoot = new AudioNode(assetManager, "Sounds/bounce.wav", DataType.Buffer);
        audio_shoot.setPositional(false);
        audio_shoot.setLooping(false);
        audio_shoot.setVolume(0.5f);
        rootNode.attachChild(audio_shoot);
      }
      
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
        shootball_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        shootball_mat.setColor("Color",new ColorRGBA( 0.2f, 0.3f, 0.7f, 0.5f));
      }

    public void initBox() {
        Geometry box_geo = new Geometry("Box", box);
        box_geo.setMaterial(box_mat);
        box_geo.setLocalTranslation(0, -0.1f, -25f);
        box_geo.setShadowMode(RenderQueue.ShadowMode.Receive);
        boxscene = CollisionShapeFactory.createMeshShape(box_geo);
        this.rootNode.attachChild(box_geo);
        box_phy = new RigidBodyControl(boxscene,0.0f);
        box_geo.addControl(box_phy);
        bulletAppState.getPhysicsSpace().add(box_phy);
      }

    public void makeBall() {
        nameIndex = ballNum;
        ball_geo = new Geometry("ball", sphere);
        ball_geo.setMaterial(ball_mat);
        ball_geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(ball_geo);

        float randNum = (float)(-75.0f+Math.random()*(149.9f));
        float randNum4 = (float)(-70f + Math.random()*(140f));
        ball_geo.setLocalTranslation(randNum,130.0f,randNum4);
        /** Make the ball physcial with a mass > 0.0f */
        bulletCollisionShape = new SphereCollisionShape(10.0f);
        ball_phy = new RigidBodyControl(bulletCollisionShape,1f);
        ball_phy.setGravity(new Vector3f(0,-10,0));
        /** Add physical ball to physics space. */
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        float ranNum2 = (float)(-8.0f + Math.random()*(16.0f));
        ball_phy.setGravity(new Vector3f(0,-20,0f));
        ball_phy.setLinearVelocity(new Vector3f(ranNum2,-10,ranNum2).mult(15));
      }

    public void iniTray(Node trayNode){
        Node sideNode = new Node();
        trayNode.attachChild(sideNode);
        /* left box*/
        Node leftNode = new Node("Left");
        Geometry tray_left = new Geometry("trayleft",trayLeft);
        tray_left.setMaterial(tray_mat);
        tray_left.rotate(0, -90f*FastMath.DEG_TO_RAD, 0);
        tray_left.setQueueBucket(Bucket.Transparent); 
        leftNode.attachChild(tray_left);
        leftNode.setLocalTranslation(-50.0f, 0, 0);
        sideNode.attachChild(leftNode);
        left_phy = new RigidBodyControl(0);
        sideNode.addControl(left_phy);
        bulletAppState.getPhysicsSpace().add(left_phy);

        /*right*/
        Node rightNode = new Node("Right");
        Geometry tray_right = new Geometry("trayright",trayLeft);
        tray_right.setMaterial(tray_mat);
        tray_right.rotate(0, -90f*FastMath.DEG_TO_RAD, 0);
        tray_right.setQueueBucket(Bucket.Transparent); 
        rightNode.attachChild(tray_right);
        rightNode.setLocalTranslation(50.0f, 0, 0);
        sideNode.attachChild(rightNode);
        right_phy = new RigidBodyControl(0);
        sideNode.addControl(right_phy);
        bulletAppState.getPhysicsSpace().add(right_phy);

        /*front */
        Node frontNode = new Node("Front");
        Geometry tray_front = new Geometry("trayfront",trayFront);
        tray_front.setMaterial(tray_mat);
        tray_front.setQueueBucket(Bucket.Transparent); 
        frontNode.attachChild(tray_front);
        frontNode.setLocalTranslation(0, 0, 20);
        sideNode.attachChild(frontNode);
        front_phy = new RigidBodyControl(0);
        sideNode.addControl(front_phy);
        bulletAppState.getPhysicsSpace().add(front_phy);

        /*back*/
        Node backNode = new Node("Back");
        Geometry tray_back = new Geometry("trayback",trayFront);
        tray_back.setMaterial(tray_mat);
        tray_back.setQueueBucket(Bucket.Transparent); 
        backNode.attachChild(tray_back);
        backNode.setLocalTranslation(0, 0, -20);
        sideNode.attachChild(backNode);
        back_phy = new RigidBodyControl(0);
        sideNode.addControl(back_phy);
        bulletAppState.getPhysicsSpace().add(back_phy);


        /*bottom*/
        Node bottomNode = new Node("Bottom");
        Geometry tray_bottom = new Geometry("trayBottom",trayBottom);
        tray_bottom.setMaterial(tray_mat);
        tray_bottom.setQueueBucket(Bucket.Transparent); 
        bottomNode.attachChild(tray_bottom);
        bottomNode.setLocalTranslation(0 ,-21, 0);
        trayNode.attachChild(bottomNode);

        bottom_phy = new RigidBodyControl(new BoxCollisionShape(new Vector3f(50f, 0.5f, 20f)),0.1f);//0 1
        bottom_phy.setGravity(new Vector3f(0,0,0));
        bottomNode.addControl(bottom_phy);      
        bulletAppState.getPhysicsSpace().add(bottom_phy);

        rootNode.attachChild(trayNode);

        Node fixNode = new Node();
        Sphere dyBox = new Sphere(32, 32, 1.0f, true, false);
        Node dyBoxNode = new Node();
        Geometry dyBox_geo = new Geometry("dynamic",dyBox);
        dyBox_geo.setMaterial(box1_mat);

        dyBoxNode.attachChild(dyBox_geo);
        dyBoxNode.setLocalTranslation(-50f-1f ,-20, 0);
        trayNode.attachChild(dyBoxNode);

        RigidBodyControl dyBox_phy = new RigidBodyControl(new SphereCollisionShape(1),0);

        dyBoxNode.addControl(dyBox_phy);
        bulletAppState.getPhysicsSpace().add(dyBox_phy);

        System.out.println(dyBox_phy.getPhysicsLocation()+","+bottom_phy.getPhysicsLocation());
        joint=new HingeJoint(dyBox_phy, bottom_phy, 
                Vector3f.ZERO, new Vector3f(-50f-1f,0,0f),
                Vector3f.UNIT_Z, Vector3f.UNIT_Z);
        bulletAppState.getPhysicsSpace().add(joint);
        joint.enableMotor(true, -1f, 80f);        
        joint.setLimit(-0.5f, 0.5f);  
      }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f ro = new Vector3f(-60,0,0);
        Vector3f fishUp = new Vector3f(0,80,0);
        walkDirection.set(0, 0, 0);
        if (left)  { player.applyCentralForce(ro); }
        if (right) { player.applyCentralForce(ro.negate()); }
        if (up)    { player.applyCentralForce(fishUp);}
        if (down)  { player.applyCentralForce(fishUp.negate()); }
        if (front) { player.applyCentralForce(new Vector3f(0,0,50));}
        if (back)  { player.applyCentralForce(new Vector3f(0,0,-50));}
      }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        // unused
    }

    public void collision(PhysicsCollisionEvent event) {      
        if ("Box".equals(event.getNodeA().getName()) || "Box".equals(event.getNodeB().getName())) {
            if ("ball".equals(event.getNodeA().getName()) || "ball".equals(event.getNodeB().getName())) {
                getPhysicsSpace().remove(event.getNodeA());
                getPhysicsSpace().remove(event.getObjectA());
                rootNode.detachChild(event.getNodeA());
                audio_shoot.play();    
            }
        }
       
        if ("Bottom".equals(event.getNodeA().getName()) || "Bottom".equals(event.getNodeB().getName())) {
            if ("ball".equals(event.getNodeA().getName()) || "ball".equals(event.getNodeB().getName())) {
                final Spatial changecolor = event.getNodeB();
                cc = (Geometry) changecolor; 
                matnew = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matnew.setColor("Color",new ColorRGBA(0.88f, 0.44f, 0.84f, 0.5f) );
                cc.setMaterial(matnew);   
            }
        }
        if ("Left".equals(event.getNodeA().getName()) || "Left".equals(event.getNodeB().getName())) {
            if ("ball".equals(event.getNodeA().getName()) || "ball".equals(event.getNodeB().getName())) {
                final Spatial ballchange = event.getNodeB();
                cc = (Geometry) ballchange; 
                matnew = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matnew.setColor("Color", new ColorRGBA(0.88f, 0.44f, 0.84f, 0.5f));
                cc.setMaterial(matnew);   
            }
        }
        if ("Right".equals(event.getNodeA().getName()) || "Right".equals(event.getNodeB().getName())) {
            if ("ball".equals(event.getNodeA().getName()) || "ball".equals(event.getNodeB().getName())) {
                final Spatial ballchange = event.getNodeB();
                cc = (Geometry) ballchange; 
                matnew = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matnew.setColor("Color", new ColorRGBA(0.88f, 0.44f, 0.84f, 0.5f));
                cc.setMaterial(matnew);   
            }
        }
        if ("Back".equals(event.getNodeA().getName()) || "Back".equals(event.getNodeB().getName())) {
            if ("ball".equals(event.getNodeA().getName()) || "ball".equals(event.getNodeB().getName())) {
                final Spatial ballchange = event.getNodeB();
                cc = (Geometry) ballchange; 
                matnew = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matnew.setColor("Color", new ColorRGBA(0.88f, 0.44f, 0.84f, 0.5f));
                cc.setMaterial(matnew);   
            }
        }
        if ("Front".equals(event.getNodeA().getName()) || "Front".equals(event.getNodeB().getName())) {
            if ("ball".equals(event.getNodeA().getName()) || "ball".equals(event.getNodeB().getName())) {
                final Spatial ballchange = event.getNodeB();
                cc = (Geometry) ballchange; 
                matnew = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matnew.setColor("Color", new ColorRGBA(0.88f, 0.44f, 0.84f, 0.5f));
                cc.setMaterial(matnew);   
            }
        }
        
      }
      
    @Override
    public void bind(Nifty nifty, Screen screen) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
