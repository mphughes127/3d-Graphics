/*
Author: Steve Maddock
Last updated: 13 November 2015
*/

/* edits made to global variables, An1Scene, createRenderObject, update, dolight, render,
   any other new functions are mine 
 *  I declare that this code is my own work 
 *  Author Matthew Hughes mphughes1@sheffield.ac.uk 
 */

import java.io.File;
import java.awt.image.*;
import javax.imageio.*;
import com.jogamp.opengl.util.awt.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class An1Scene {

  private GLU glu = new GLU();
  private GLUT glut = new GLUT();

  private final double INC_ROTATE=2.0;

  private double rotate=0.0;
  private boolean objectsOn = true;

  private int canvaswidth=0, canvasheight=0;

  private Light light, light2, ceilingLight1, ceilingLight2, eyeLight, eyeLight2;
  private Camera camera;
  private Mesh meshPlane, meshCylinder, meshCube;   // Define mesh instances for the scene. 
                                      // We only really need to keep a record of these
                                      // if we intend to edit the mesh in some way after
                                      // it has been created. However, they are left here to illustrate that.
                                      // In practice, they could also be given more useful names 
                                      // to represent what they are.
                                      // We could also create an array of objects instead of individual variables.
  private Render plane, cylinder, cube;   // Define matching render objects for the scene meshes
  private int lightAnim, planeAnim, cylAnimX, cylAnimY, cylAnimZ, cubeAnim;
  private AnimationScene animationScene;
  private Axes axes;
  
  private Room room;
  private Texture floorTex, roofTex, wallTex;
  private Robot robot, robot2;


  /**
   * Constructor.
   * @param gl OpenGL context
   * @param camera Instance of the camera class, which uses the idea of moving around a virtual sphere,
   *               centred on the origin, under mouse control.
   */  
  public An1Scene(GL2 gl, Camera camera) {
    animationScene = new AnimationScene();
     light = new Light(GL2.GL_LIGHT0, new float[]{4,3,0,1}, 
                       Light.DEFAULT_AMBIENT, new float[]{0.5f,0.5f,0.5f}, new float[]{0.9f,0.9f,0.9f},
                       true);
    // light2 = new Light(GL2.GL_LIGHT1, new float[]{0,3,4,1}, 
    //                    Light.DEFAULT_AMBIENT, new float[]{0.5f,0.5f,0.5f}, new float[]{0.5f,0.5f,0.5f},
    //                    true);



    ceilingLight1=new Light(GL2.GL_LIGHT3, new float[]{-3,10,-3,1});
    ceilingLight1.makeSpotlight(new float[]{0,-1,0},15);

    ceilingLight2=new Light(GL2.GL_LIGHT4, new float[]{3,10,3,1});
    ceilingLight2.makeSpotlight(new float[]{0,-1,0},15);

    eyeLight = new Light(GL2.GL_LIGHT5, new float[]{0,1,0,1}); //will be positions as robot moves
    eyeLight.makeSpotlight(new float[]{0.5f,-1,0.1f},10);

    eyeLight2 = new Light(GL2.GL_LIGHT6, new float[]{0,1,0,1}); //will be positions as robot moves
    eyeLight2.makeSpotlight(new float[]{0.5f,-1,0.1f},10);


    this.camera = camera;
    axes = new Axes(2.2, 1.8, 1.6);


    floorTex = loadTexture(gl, "wood_floor.jpg");
    roofTex = loadTexture(gl, "ceiling.jpg");
    wallTex = loadTexture(gl, "wall_tex.jpg");

    createRenderObjects(gl);          // Create/load objects
  }
           
  private void createRenderObjects(GL2 gl) {    
    // White plane - white is the default material for a new Mesh object.
	  // This object will need an associated texture.
    meshPlane = ProceduralMeshFactory.createPlane(5,5,10,10,1,1);  // Create the mesh structure
    Material mat = meshPlane.getMaterial();
    mat.setAmbient(new float[]{0.25f,0.25f,1.0f, 1f}); // Colour will be mostly blue.
    mat.setDiffuse(new float[]{0.5f,0.5f,1f, 1f}); // Colour will be mostly blue.
    plane = new Render(meshPlane);    // Create a new Render object for the mesh
    plane.initialiseDisplayList(gl, false);
	
    // Mostly blue cylinder. No texture 
    meshCylinder = ProceduralMeshFactory.createCylinder();
    mat = meshCylinder.getMaterial();
    mat.setAmbient(new float[]{0.5f,0.25f,0.25f, 1f}); // Colour will be mostly red.
    mat.setDiffuse(new float[]{1f,0.5f,0.5f, 1f}); // Colour will be mostly red.
    cylinder = new Render(meshCylinder);       // We'll use the default of
                                               // immediate mode rendering for the cylinder
    // White hardCube.
	  // This object will need an associated texture.                                      
    meshCube = ProceduralMeshFactory.createHardCube();
    cube = new Render(meshCube);  


    this.robot = new Robot();
    this.robot2 = new Robot();
    room=new Room();
    room.createRenderObjects(gl, wallTex, floorTex, roofTex);

  }
 
  // called from SG1.reshape() if user resizes the window
  public void setCanvasSize(int w, int h) {
    canvaswidth=w;
    canvasheight=h;
  }

  /**
   * Method used from the GUI to control whether or not all the objects are displayed
   * @param b true if the objects should be displayed
   */    
  public void setObjectsDisplay(boolean b) {
    objectsOn = b;
  }

  /**
   * Retrieves the first Light instance so that its attributes can be set from the GUI.
   * Currently only returns the first light instance, which is set to be the general light for the entire scene.
   * If two lights are created in this class, then the method could be rewritten so that a
   * parameter could be used to return the specific light. Alternatively a separate method could be used.
   * @return returns the first Light instance used in this class
   */    
  public Light getLight() {
    return light;
  }

  /**
   * Retrieves the Axes instance so that its attributes can be set from the GUI, e.g. turned on and off.
   * @return returns the Axes instance used in this class
   */      
  public Axes getAxes() {
    return axes;
  }

  /**
   * Sets the animation control attribute to its initial value and sets the objects to display
   */  
  public void reset() {
    animationScene.reset();
    rotate=0.0;
    setObjectsDisplay(true);
  }

  public void startAnimation() {
    animationScene.startAnimation();
  }

  public void pauseAnimation() {
    animationScene.pauseAnimation();
  }
  
  /**
   * Increments the animation control attribute
   */ 
  public void incRotate() {
    rotate=(rotate+INC_ROTATE)%360;
  }

  /**
   * Updates the animation control variable.
   * Could be rewritten to update more things for each frame of animation.
   */ 
  public void update() {
    incRotate();
    double waggle = animationScene.getParam(animationScene.ROBOT_ARM);
    double lean = animationScene.getParam(animationScene.ROBOT_LEAN);
    double lean2 = animationScene.getParam(animationScene.ROBOT_LEAN2);
    double lean3 = animationScene.getParam(animationScene.ROBOT_LEAN3);
    if (lean != 0){
      robot.update(waggle,lean);
      
    } 
    else if (lean2 !=0){
      robot.update(waggle,lean2);
      
    }
    else if(lean3 !=0){
      robot.update(waggle,lean3);
      
    }
    else {
      robot.update(waggle,lean);
      
    }

    animationScene.update();
  }

  private void doLight(GL2 gl) {
    gl.glPushMatrix();
     
      if (light.getSwitchedOn()) {
        light.use(gl, glut, false);
      }
      else light.disable(gl);
    gl.glPopMatrix();
  }
  
  // private void doLight2(GL2 gl) {
  //   gl.glPushMatrix();
  //     double lp = animationScene.getParam(animationScene.LIGHT_PARAM);
  //     gl.glTranslated(0,lp,0);
  //     if (light2.getSwitchedOn()) {
  //       light2.use(gl, glut, true);
  //     }
  //     else light2.disable(gl);
  //   gl.glPopMatrix();
  // }

  private void doCeilingLight1(GL2 gl) {
      if (ceilingLight1.getSwitchedOn()) {
        ceilingLight1.use(gl, glut, false);
      }
      else ceilingLight1.disable(gl);
    }

     private void doCeilingLight2(GL2 gl) {
      if (ceilingLight2.getSwitchedOn()) {
        ceilingLight2.use(gl, glut, false);
      }
      else ceilingLight2.disable(gl);
    }
  
    private void doEyeLight(Robot robot, Light eyeLight, GL2 gl){
      gl.glPushMatrix();
      robot.transformForEye(gl);
      eyeLight.use(gl, glut, false);
      gl.glPopMatrix();
    }

    private void doEyeLight2(Robot robot, Light eyeLight2, GL2 gl){
      gl.glPushMatrix();
      robot.transformForEye(gl);
      eyeLight2.use(gl, glut, false);
      gl.glPopMatrix();
    }

  /**
   * Renders the scene.
   * @param gl OpenGL context
   */   
  public void render(GL2 gl) {
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT|GL2.GL_DEPTH_BUFFER_BIT);
    gl.glLoadIdentity();
    camera.view(glu);                 // Orientate the camera
    doLight(gl);                      // Place the light

    //doLight2(gl);

    doCeilingLight1(gl);

    doCeilingLight2(gl);
    
    if (axes.getSwitchedOn()) 
      axes.display(gl, glut);

    if (objectsOn) {                  // Render the objects
      
    room.drawRoom(gl);

    gl.glPushMatrix();
    double r1 = animationScene.getParam(animationScene.ROBOT_ROTATION);
    double r2 = animationScene.getParam(animationScene.ROBOT_ROTATION2);
    double r3 = animationScene.getParam(animationScene.ROBOT_ROTATION3);

    double rx = animationScene.getParam(animationScene.ROBOT_X_PARAM);
    double rz = animationScene.getParam(animationScene.ROBOT_Z_PARAM);
    gl.glTranslated(rx,0,rz);
    
    gl.glTranslated(-4,0,0); //position correction
    gl.glRotated(r3,0,1,0);
    gl.glRotated(r2,0,1,0);
    gl.glRotated(r1,0,1,0);
    gl.glRotated(14,0,1,0);//angle correction
    robot.display(gl, glut);
    doEyeLight(robot,eyeLight,gl);
    gl.glPopMatrix();
      

    gl.glPushMatrix();
    double rot1 = animationScene.getParam(animationScene.ROBOT2_ROTATION);
    double rot2 = animationScene.getParam(animationScene.ROBOT2_ROTATION2);

    double rx2 = animationScene.getParam(animationScene.ROBOT2_X_PARAM);
    double rz2 = animationScene.getParam(animationScene.ROBOT2_Z_PARAM);
    gl.glTranslated(rx2,0,rz2);
    
    gl.glTranslated(-6,0,-6);
    gl.glRotated(rot2,0,1,0);
    gl.glRotated(rot1,0,1,0);
    robot2.display(gl, glut);
    doEyeLight2(robot,eyeLight2,gl);
    gl.glPopMatrix();



    }
  }




  private void setMaterial(GL2 gl, float r, float g, float b) {
    float[] matAmbientDiffuse = {r,g,b, 1.0f};
    float[] matSpecular = {0.5f,0.5f,0.5f, 1.0f};
    float[] matShininess = {16.0f};
    float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbientDiffuse, 0);
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecular, 0);
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess, 0);
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, matEmission, 0);
  }

  //i start here

  private Texture loadTexture(GL2 gl, String filename) {
    Texture tex = null;
    // since file loading is involved, must use try...catch construct in Java
    try {
      File f = new File(filename);

      // The following line results in a texture that is flipped vertically (i.e. is upside down)
      // due to the OpenGL and Java (0,0) coordinate position being different,
      // one using the bottom left of an image as (0,0) and the other using the top left as (0,0):
      //   tex = TextureIO.newTexture(new File(filename), false);

      // So, instead, use the following three lines which flip the image vertically:
      BufferedImage img = ImageIO.read(f); // read file into BufferedImage
      ImageUtil.flipImageVertically(img);
      tex = AWTTextureIO.newTexture(GLProfile.getDefault(), img, false);

      // Different filter settings can be used to give different effects when the texture
      // is applied to a set of polygons - see the exercise sheet for an explanation
      tex.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
      tex.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
    }
    catch(Exception e) {
      System.out.println("Error loading texture " + filename); 
    }
    return tex;
  } // end of loadTexture()

  

}


