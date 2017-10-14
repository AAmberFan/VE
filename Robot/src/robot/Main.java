// ==========================================================================
// $Id: Main.java,v 1.4 2016/10/14 02:34:29 jlang Exp $
// ELG5124/CSI5151 Robot Arm Kinematics
// ==========================================================================
// (C)opyright:
//
//   Jochen Lang
//   EECS, University of Ottawa
//   800 King Edward Ave.
//   Ottawa, On., K1N 6N5
//   Canada. 
//   http://www.eecs.uottawa.ca
// 
// Creator: jlang (Jochen Lang)
// Email:   jlang@eecs.uottawa.ca
// ==========================================================================
// $Log: Main.java,v $
// ==========================================================================
package robot;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Eigen3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.scene.shape.Line;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

public class Main extends SimpleApplication {

    private Geometry line;
    private Geometry target;
    private Geometry joint1;
    private Geometry joint2;
    private Geometry cylinder1;
    private Geometry cylinder2;
    private Node sObject;
    //private Node t1 ;//node may be added...     
    //private Node t2 ;
   
    private Node joint1Node;
    private Node joint2Node;
    private Node cylinder1Node;
    private Node cylinder2Node;
    private Node mouth1Node;
    private Node mouth2Node;
    private double sita0;
    private double sita1;
    private double sita2;
    
    int num = -1;
    
    public static void main(String[] args) {
        Main app = new Main();
        // app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        /*
        settings.put("Width", 640);
        settings.put("Height", 480);
        */
        settings.put("Title", "ELG5124 Robot Arm");
        // VSynch
        // settings.put("VSync", true)
        // Anti-Aliasing
        // settings.put("Samples", 4);
        // Initialize sphere control        
        app.setSettings(settings);
        app.start();
    }
    
    private Vector3f startP; 
    private Vector3f endP;
    Vector3f [] vertices;

    /**
     * Simple analoglistener which takes the camera-based directions and adds it
     * to the local translation of the target. Implicitly assumes that the target is
     * directly attached to scene root.
     */
    final private AnalogListener analogListener;

    
    private final ActionListener actionListener;

    
    public Main() {
        this.actionListener = new ActionListener() {    
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                
                if (name.equals("Next") && !keyPressed) {
                    // Ray from target position at last time step to current.
                    // Reset our line drawing
                    startP.set(endP);
                    // and redraw
                    updateLine();
                    
                }
                if(name.equals("Joint") && !keyPressed){
                    num = (num+1)%3;
                }
                
            }
        };
        
        this.analogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                value *= 10.0;
                int jnum;
                // find forward/backward direction and scale it down
                Vector3f camDir = cam.getDirection().clone().multLocal(value);
                // find right/left direction
                Vector3f camLeft = cam.getLeft().clone().multLocal(value);
                // find up/down direction
                Vector3f camUp = cam.getUp().clone().multLocal(value);
                boolean pChange = false;
                if (name.equals("Left")) {
                    Vector3f v = target.getLocalTranslation();
                    target.setLocalTranslation(v.add(camLeft));
                    pChange = true;
                }
                if (name.equals("Right")) {
                    Vector3f v = target.getLocalTranslation();
                    target.setLocalTranslation(v.add(camLeft.negateLocal()));
                    pChange = true;
                }
                if (name.equals("Forward")) {
                    Vector3f v = target.getLocalTranslation();
                    target.setLocalTranslation(v.add(camDir));
                    pChange = true;
                }
                if (name.equals("Back")) {
                    Vector3f v = target.getLocalTranslation();
                    target.setLocalTranslation(v.add(camDir.negateLocal()));
                    pChange = true;
                }
                if (name.equals("Up")) {
                    Vector3f v = target.getLocalTranslation();
                    target.setLocalTranslation(v.add(camUp));
                    pChange = true;
                }
                if (name.equals("Down")) {
                    Vector3f v = target.getLocalTranslation();
                    target.setLocalTranslation(v.add(camUp.negateLocal()));
                    pChange = true;
                }
                if (pChange) updateLine();
                
                if(num == 0){
                    //Vector3f v = t1.getLocalTranslation();
                    if(name.equals("Add")){
                        joint1Node.rotate(0, value*speed*1, 0);
                    }
                    if(name.equals("Minus")){
                        joint1Node.rotate(0, value*speed*(-1), 0);
                    }
                }
                if(num == 1){
                    if(name.equals("Add")){
                        joint1Node.rotate(0, 0, value*speed*1);
                    }
                    if(name.equals("Minus")){
                        joint1Node.rotate(0, 0, value*speed*(-1));
                        
                    }
                }
                if(num == 2){
                    if(name.equals("Add")){
                        joint2Node.rotate(0,0, value*speed*1);
                    }
                    if(name.equals("Minus")){
                        joint2Node.rotate(0,0, value*speed*(-1));
                    }
                }
            }
        };
    }

    @Override
    public void simpleInitApp() {
        // Left mouse button press to rotate camera
        flyCam.setDragToRotate(true);
        // Do not display stats or fps
        setDisplayFps(false);
        setDisplayStatView(false);
        // move the camera back (10 is the default)
        // cam.setLocation(new Vector3f(0f, 0f, 5.0f));
        // also: cam.setRotation(Quaternion)

        
        // Generate the robot - starting with a box for the base
        sObject = new Node();
        Geometry base = new Geometry("Base", new Box(0.5f, 0.5f, 0.5f));
       // base.setLocalTranslation(-3.5f, -2.5f, 0.0f);
        sObject.setLocalTranslation(-3.0f, -2.5f, 0.0f);
        Material matBase = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        // Use transparency - just to make sure we can always see the target
        matBase.setColor("Color", new ColorRGBA( 0.7f, 0.7f, 0.7f, 0.5f)); // silver'ish
        matBase.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        base.setMaterial(matBase);
        base.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        sObject.attachChild(base);
        rootNode.attachChild(sObject);
        
        //joint1
        //t1 = new Node();
        joint1Node = new Node();
        joint1 = new Geometry("Joint1",new Sphere(6, 12, 0.3f));
        
        joint1Node.setLocalTranslation(0.0f,0.5f,0.0f);
        //joint1Node.setLocalTranslation(0.0f,0.0f,0.0f);
        Material matJoint = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matJoint.setColor("Color", new ColorRGBA( 1f, 0.68f, 0.68f, 0.5f));
        
        matJoint.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        joint1.setMaterial(matJoint);
        joint1.setQueueBucket(RenderQueue.Bucket.Transparent);
        joint1Node.attachChild(joint1);
        sObject.attachChild(joint1Node);
        //rootNode.attachChild(joint1Node);
        //sObject.attachChild(t1);
       
        //the cylinder1
        cylinder1Node = new Node();
        Cylinder c1 = new Cylinder(12,15,0.3f,2.0f);
        cylinder1 = new Geometry("Cylinder1",c1);
        //cylinder1Node.setLocalTranslation(-3.5f,-1.0f,0.0f);
        cylinder1Node.setLocalTranslation(0,1.0f,0);
        cylinder1.rotate(90f*FastMath.DEG_TO_RAD, 0f, 0f);
        Material matcylinder = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matcylinder.setColor("Color", new ColorRGBA( 0.7f, 0.7f, 0.7f, 0.5f));
        
        matcylinder.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        cylinder1.setMaterial(matcylinder);
        cylinder1.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        //sObject.attachChild(cylinder1);
        cylinder1Node.attachChild(cylinder1);
        joint1Node.attachChild(cylinder1Node);
        //rootNode.attachChild(cylinder1Node);
        //joint1Node.attachChild(cylinder1);
        
        //joint2
        joint2Node = new Node();
        joint2 = new Geometry("Joint2",new Sphere(6, 12, 0.3f));
        joint2Node.setLocalTranslation(0.0f,2.0f,0.0f);
        
        joint2.setMaterial(matJoint);
        joint2.setQueueBucket(RenderQueue.Bucket.Transparent);
        joint2Node.attachChild(joint2);
        joint1Node.attachChild(joint2Node);
        
        //the cylinder1
        cylinder2Node = new Node();
        Cylinder c2 = new Cylinder(12,15,0.3f,2.0f);
        cylinder2 = new Geometry("Cylinder2",c2);
        cylinder2Node.setLocalTranslation(1.0f,0.0f,0.0f);
        cylinder2.rotate( 0f,90f*FastMath.DEG_TO_RAD, 0f);
       
        cylinder2.setMaterial(matcylinder);
        cylinder2.setQueueBucket(RenderQueue.Bucket.Transparent);
        cylinder2Node.attachChild(cylinder2);
        joint2Node.attachChild(cylinder2Node);
        
        //mouth
        mouth1Node = new Node();
        mouth2Node = new Node();
        Box box1 = new Box(0.3f,0.1f,0.3f);
        Geometry MouthAbove = new Geometry("MouthUp",box1);
        mouth1Node.setLocalTranslation(1.15f,0.2f,0.0f);
        MouthAbove.rotate(0f, 0f,25f*FastMath.DEG_TO_RAD);
        Material matMouth = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matMouth.setColor("Color", new ColorRGBA( 1.0f, 0.7f, 0.7f, 0.5f));
        mouth1Node.attachChild(MouthAbove);
        matMouth.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        MouthAbove.setMaterial(matMouth);
        MouthAbove.setQueueBucket(RenderQueue.Bucket.Transparent);
        cylinder2Node.attachChild(mouth1Node);
        
        Box box2 = new Box(0.3f,0.1f,0.3f);
        Geometry MouthBelow = new Geometry("MouthBelow",box2);
        mouth2Node.setLocalTranslation(1.15f,-0.2f,0.0f);
        MouthBelow.rotate(0f, 0f,-25f*FastMath.DEG_TO_RAD);
        
        MouthBelow.setMaterial(matMouth);
        MouthBelow.setQueueBucket(RenderQueue.Bucket.Transparent);
        mouth2Node.attachChild(MouthBelow);
        cylinder2Node.attachChild(mouth2Node);
        
        // Generate a sphere as a symbol for the target point
        target = new Geometry("Sphere", new Sphere(6, 12, 0.1f));
        //target.setLocalTranslation(-1.5f, 0, 0);
        target.setLocalTranslation(-1.0f, 0, 0);
        Material matSphere = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matSphere.setColor("Color", ColorRGBA.Red);
        target.setMaterial(matSphere);

        rootNode.attachChild(target);

        // Set up line drawing from last position to current position of target
        // We artifically create a time step based on the number of interactions
        startP = new Vector3f(target.getLocalTranslation());
        endP = new Vector3f(target.getLocalTranslation()); 
        vertices = new Vector3f[]{startP,endP};
        Line ln = new Line(startP,endP);
        ln.setLineWidth(2);
        line = new Geometry("line", ln);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        line.setMaterial(mat);
        
        rootNode.attachChild(line);

        /** Set up interaction keys */
        setUpKeys();

        // Can be used to change mapping of mouse/keys to camera behaviour
        /*
         if (inputManager != null) {
         inputManager.deleteMapping("FLYCAM_RotateDrag");
         flyCam.setDragToRotate(true);
         inputManager.addMapping("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
         inputManager.addListener(flyCam, "FLYCAM_RotateDrag");
         }
         */
    }

    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_PGDN));
        
        inputManager.addMapping("Joint", new KeyTrigger(KeyInput.KEY_J));
        
        
        inputManager.addMapping("Add",new KeyTrigger(KeyInput.KEY_ADD));
        inputManager.addMapping("Minus",new KeyTrigger(KeyInput.KEY_MINUS));
        
        inputManager.addListener(analogListener,
                "Left", "Right", "Up", "Down", "Forward", "Back","Add","Minus");
        inputManager.addMapping("Next", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Next","Joint");
    }
       
    // Helper routine to update line
    void updateLine() {
        Mesh m = line.getMesh();
        endP.set(target.getLocalTranslation());
        m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        m.updateBound();
        
        // Tutorial section on how to use the Jama package
//        java.util.Random rand = new java.util.Random();
//        double[][] dm = new double[3][3];
//        for ( int r=0; r<3; r++) {
//            if (r!=1) { // make it singular
//                for ( int c=0; c<3; c++) {
//                    dm[r][c] = rand.nextFloat();
//                }
//            }
//        }
//        Jama.Matrix jm3 = new Jama.Matrix(dm);
//        Jama.SingularValueDecomposition svd = new Jama.SingularValueDecomposition(jm3);
//        double[] s = svd.getSingularValues();
//        for ( double e:s) {
//            System.out.print(e + " ");
//        }
//        System.out.println();
    }    
    
    // Update the position every sec
    @Override
    public void simpleUpdate(float tpf) {
        sita1 = 0.0;
        sita2 = 0.0;
        sita0 = 0.0;
        //double dx, dy, dz;
        Vector3f v = target.getLocalTranslation();
        double [] detaPosArray = { v.x, v.y, v.z };
        //System.out.print(v);
        Jama.Matrix dataPos = new Jama.Matrix(detaPosArray,3);//dx,dy,dz
      
        
//        double [][]Mb0 = 
//        {{Math.cos(sita0),0,Math.sin(sita0),0},{0,1,0,0},{-1*Math.sin(sita0),0,Math.cos(sita0),0},{0,0,0,1} };
//        Jama.Matrix Tb0 = new Jama.Matrix(Mb0);
//        
//        double [][] M01 = 
//        { {Math.cos(sita1),-1*Math.sin(sita1),0,0},{Math.sin(sita1),Math.cos(sita1),0,0 }, {0,0,1,0},{0,0,0,1}};
//        Jama.Matrix T10 = new Jama.Matrix(M01);
//        
//        double [][] M12 =
//        { {Math.cos(sita2), -1* Math.sin(sita2),0,2}, {Math.sin(sita2), Math.cos(sita2),0,0},{0,0,1,0}, {0,0,0,1} };
//        Jama.Matrix T20 = new Jama.Matrix(M12);
//        
//        double [][] M122 ={ {1,0,0,2},{0,1,0,0},{0,0,1,0},{0,0,0,1} };
//        Jama.Matrix T22 = new Jama.Matrix(M122);
//        
//        Jama.Matrix Tb2 = Tb0.times(T10).times(T20).times(T22);
//        
//        dx = Tb2.get(1, 4);
//        dy = Tb2.get(2, 4);
//        dz = Tb2.get(3, 4);
        
       
        //Jama.SingularValueDecomposition svd = new Jama.SingularValueDecomposition(jm3);
       
        
        
        double[][] MJ 
                ={ {-1*(2+2*Math.cos(sita2))*(Math.sin(sita0)*Math.cos(sita1)) + ( (2*Math.sin(sita0)*Math.sin(sita1)*Math.sin(sita2) ) ),
                -1*(2+2*Math.cos(sita2))*(Math.sin(sita1)*Math.cos(sita0)) - (2*Math.cos(sita0)*Math.cos(sita1)*Math.sin(sita2) ), 
                (-2*Math.sin(sita2))*(Math.cos(sita1)*Math.cos(sita0)) - (2*Math.cos(sita0)*Math.sin(sita1)*Math.cos(sita2) )},
                    { 0,
                    (2+2*Math.cos(sita2)) * Math.cos(sita1) - 2*Math.sin(sita2)*Math.sin(sita1),
                    (-2*Math.sin(sita2)) * Math.sin(sita1) + 2*Math.cos(sita2)*Math.cos(sita1)},
                    { -1*(2+2*Math.cos(sita2)) * (Math.cos(sita0)*Math.cos(sita1)) + 2*Math.cos(sita0)*Math.sin(sita1)*Math.sin(sita2),
                      (2+2*Math.cos(sita2)) * (Math.sin(sita0)*Math.sin(sita1)) + 2*Math.sin(sita0)*Math.cos(sita1)*Math.sin(sita2),
                      (2*Math.sin(sita2)) * (Math.sin(sita0)*Math.cos(sita1)) + 2*Math.sin(sita0)*Math.sin(sita1)*Math.cos(sita2)
                    }
                };
        
        //System.out.println(MJ);
        Jama.Matrix J = new Jama.Matrix(MJ);
        Jama.SingularValueDecomposition svd = new Jama.SingularValueDecomposition(J);
        Jama.Matrix S = svd.getS();
        Jama.Matrix V = svd.getV();
        Jama.Matrix U = svd.getU();
        double[][]s =new double[3][3];
        for(int i =0 ;i<3;i++){
            for(int j = 0; j<3; j++){
                s[i][j]=S.get(i, j);
                if(s[i][j]!= 0){
                    s[i][j] = 1.0 / s[i][j];
                }
            }
        }
        //System.out.println(s[1][1]);
        S = new Jama.Matrix(s);
        Jama.Matrix JA = V.times(S).times(U.transpose());
           
//        Jama.Matrix Jt = J.transpose();
//        Jama.Matrix J1 = (J.times(Jt)).inverse();
//        Jama.Matrix JA = Jt.times(J1);
       Jama.Matrix sitaM = JA.times(dataPos);
        //System.out.println(sitaM);
        sita0 = sitaM.get(0,0);
        sita1 = sitaM.get(1,0);
        sita2 = sitaM.get(2,0);
        System.out.println(sita0+","+sita1+","+sita2);
//        Quaternion q1,q2,q3;
//        q1 = new Quaternion( (float)Math.sin(sita0/2) ,0,(float)Math.cos(sita0/2)*1,0);
//        q2 = new Quaternion( (float)Math.sin(sita1/2),0,0 ,(float)Math.cos(sita1/2)*1);
//        q3 = new Quaternion( (float)Math.sin(sita2/2),0,0 ,(float)Math.cos(sita2/2)*1);
//        joint1Node.setLocalRotation(q1);
//        joint1Node.setLocalRotation(q2);
//        joint2Node.setLocalRotation(q3);
        /*joint1Node.rotate(0,(float)sita0,0);
        joint1Node.rotate(0,0,(float)sita1);
        joint2Node.rotate(0,0,(float)sita2);*/ // isn't the effect we want to
        Matrix3f m1 = new Matrix3f((float)Math.cos(sita0),0,(float)Math.sin(sita0),0,1.0f,0,-1*(float)Math.sin(sita0),0,(float)Math.cos(sita0));
        Matrix3f m2 = new Matrix3f((float)Math.cos(sita1),-1*(float)Math.sin(sita1),0, (float)Math.sin(sita1),(float)Math.cos(sita1),0, 0,0,1.0f);
        Matrix3f m3 = new Matrix3f((float)Math.cos(sita2),-1*(float)Math.sin(sita2),0, (float)Math.sin(sita2),(float)Math.cos(sita2),0, 0,0,1.0f);
        
        
        joint1.setLocalRotation(m1);
        joint1Node.setLocalRotation(m2);
        joint2Node.setLocalRotation(m3);
        
}
}

