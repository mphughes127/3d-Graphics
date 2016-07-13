/*
Author: Steve Maddock
Last updated: 13 November 2015
*/

//minor chaged to camera and interface made 
//author matthew hughes

import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class An1 extends Frame implements GLEventListener, ActionListener,
                                           ItemListener, MouseMotionListener {

  public final static int WIDTH=800;
  public final static int HEIGHT=800;
  private static final float NEAR_CLIP=0.1f;
  private static final float FAR_CLIP=100.0f;    
  private static final boolean CONTINUOUS_ANIMATION = false;

  private Point lastpoint;            // used with mouse routines
  private int width, height;

  private Checkbox checkAxes, checkObjects, checkLight2;
  private Button startAnim, pauseAnim, resetScene;
  private boolean continuousAnimation = CONTINUOUS_ANIMATION;

  private Camera camera;
  private An1Scene scene;
  private GLCanvas canvas;

  /**
   * The main method
   * @param args command line arguments supplied when the program is run
   */   
  public static void main(String[] args) {
    An1 gl=new An1();
    gl.setVisible(true);
  }

  /**
   * Constructor. Creates the GUI.
   */     
  public An1() {
    setTitle("An1");
    setSize(WIDTH, HEIGHT);

    GLProfile glp = GLProfile.getDefault();
    GLCapabilities caps = new GLCapabilities(glp);
    canvas = new GLCanvas(caps);
    add(canvas, "Center");
    canvas.addGLEventListener(this);
        
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    } );

    MenuBar menuBar = new MenuBar();
    setMenuBar(menuBar);
      Menu fileMenu = new Menu("File");
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
    menuBar.add(fileMenu);

    Panel p = new Panel(new GridLayout(2,1));
      Panel p1 = new Panel(new GridLayout(5,1));
        checkAxes = addCheckbox(p1, "axes on", this);
        checkObjects = addCheckbox(p1, "objects on", this);
        checkLight2 = addCheckbox(p1, "World Light on", this);
      p.add(p1);
      p1 = new Panel(new GridLayout(3,1));
        startAnim = new Button("Start animation");
        startAnim.setActionCommand("StartAnim");
        startAnim.addActionListener(this);
        p1.add(startAnim);
        pauseAnim = new Button("Pause animation");
        pauseAnim.setActionCommand("PauseAnim");
        pauseAnim.addActionListener(this);
        p1.add(pauseAnim);
        resetScene = new Button("Reset scene");
        resetScene.setActionCommand("ResetScene");
        resetScene.addActionListener(this);
        p1.add(resetScene);
      p.add(p1);
    add(p, "East");

    canvas.addMouseMotionListener(this); // link mouse motion events

    // We specify a refresh frame rate of 30 frames per second for the canvas.
    // An animation event is actually not needed for this program, as the
    // rotation is under control of a button press and a repaint call.

    FPSAnimator animator=new FPSAnimator(canvas, 30);
    animator.start();
  }

  // In the AWT classes a Checkbox is used in conjunction with
  // an ItemListener.
  // If the Swing classes were used, a JCheckBox is used in conjunction with 
  // an ActionListener.
  
  private Checkbox addCheckbox(Panel p, String s, ItemListener a) {
    Checkbox c = new Checkbox(s, true);
    c.addItemListener(a);
    p.add(c);
    return c;
  }

  /**
   * Responds to events such as button clicks
   * @param e Automatically supplied by the system when an action occurs.
   */   
  public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand().equalsIgnoreCase("rotate")) {
      scene.incRotate();
      canvas.repaint();
    }
    else if(e.getActionCommand().equalsIgnoreCase("quit")) {
      System.exit(0);
    }
    else if (e.getActionCommand().equalsIgnoreCase("startanim")) {
      setContinuousAnimation(true);
    }
    else if (e.getActionCommand().equalsIgnoreCase("pauseanim")) {
      setContinuousAnimation(false);
    }
    else if (e.getActionCommand().equalsIgnoreCase("resetscene")) {
      reset();
    }
  }

  /**
   * Responds to changes to any Checkbox in the GUI
   * @param e Automatically supplied by the system when a Checkbox changes its state due to user interaction.
   */   
  public void itemStateChanged(ItemEvent e) {
    Object source = e.getSource();
    if (source == checkAxes) {
      scene.getAxes().setSwitchedOn(checkAxes.getState());
      canvas.repaint();
    }
    else if (source == checkObjects) {
      scene.setObjectsDisplay(checkObjects.getState());
      canvas.repaint();
    }
    else if (source == checkLight2) {
      scene.getLight().setSwitchedOn(checkLight2.getState());
      canvas.repaint();
    }
  }
  
  private void setContinuousAnimation(boolean b) {
    continuousAnimation = b;
    if (b) scene.startAnimation();
    else scene.pauseAnimation();
  }

  private void reset() {
    checkAxes.setState(true);
    scene.getAxes().setSwitchedOn(true);
    checkObjects.setState(true);
    scene.setObjectsDisplay(true);
    checkLight2.setState(true);
    scene.getLight().setSwitchedOn(true);
    setContinuousAnimation(CONTINUOUS_ANIMATION);
    scene.reset();
  }

  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* initialisation */
  /**
   * METHOD DEFINED BY GLEventListener
   * Initialises the OpenGL context
   * @param drawable Automatically supplied by the system.
   */   
  public void init (GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); //black
    gl.glEnable(GL2.GL_DEPTH_TEST); // We want to use the z buffer so that overlapping objects are drawn correctly.
    gl.glEnable(GL2.GL_CULL_FACE);  // Enable the ability to discard polygons.
    gl.glCullFace(GL2.GL_BACK);     // State which polygons will be discarded, in this case those that
                                    // are facing away from the camera.
    gl.glShadeModel(GL2.GL_SMOOTH); // Colours computed at vertices are interpolated over the surface 
	                                  // of a polygon.
    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	                                  // Front and back facing polygons should be filled.
    gl.glEnable(GL2.GL_LIGHTING);   // Could be part of lights instead but done here as a default
									                  // to indicate lighting will be used.
    gl.glEnable(GL2.GL_LIGHT2);     // Default is to enable light 2 (change made)
    gl.glEnable(GL2.GL_NORMALIZE);  // If enabled, normal vectors specified with glNormal 
	                                  // are scaled to unit length after transformation.
                                    // This is only really necessary if a scale transformation
                                    // is being used, so as to correct the normals, unless you 
                                    // are prepared to do this by writing your own code.
                                    // For rotations and translations, it is not needed.
                                    // When turned on, it does slow rendering 
                                    // See en.wikipedia.org/wiki/Normal_%28geometry%29#Transforming_normals
                                    // for details of transforming normals.					
    double radius = 20.0;           // radius of 'camera sphere', i.e. distance from 
                                    // world origin
    double theta = Math.toRadians(-45); // theta rotates anticlockwise around y axis
                                    // here, 45 clockwise from x towards z axis
    double phi = Math.toRadians(30);  // phi is inclination from ground plane
                                    // here, 30 degrees up from ground plane
    camera = new Camera(theta, phi, radius);
    scene = new An1Scene(gl, camera);
  }
   
  /* Called to indicate the drawing surface has been moved and/or resized  */
  /**
   * METHOD DEFINED BY GLEventListener
   * Called when the user resizes the window
   * @param drawable Automatically supplied by the system.
   * @param x x coordinate of top left of window. Automatically supplied by the system.
   * @param y y coordinate of top left of window. Automatically supplied by the system.
   * @param width width of window. Automatically supplied by the system.
   * @param height height of window. Automatically supplied by the system.
   */   
  public void reshape (GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL2 gl = drawable.getGL().getGL2();

    this.width=width;
    this.height=height;
    
    scene.setCanvasSize(width,height);
    
    float fAspect=(float) width/height;
    float fovy=60.0f;

    gl.glViewport(0, 0, width, height);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    
    float top=(float) Math.tan(Math.toRadians(fovy*0.5))*NEAR_CLIP;
    float bottom=-top;
    float left=fAspect*bottom;
    float right=fAspect*top;
    
    gl.glFrustum(left, right, bottom, top, NEAR_CLIP, FAR_CLIP);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }

  /* draw */
  /**
   * METHOD DEFINED BY GLEventListener
   * Called when the canvas is displayed
   * @param drawable Automatically supplied by the system.
   */  
  public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    if (continuousAnimation) scene.update();
    scene.render(gl);
  }

  /**
   * METHOD DEFINED BY GLEventListener. Not required.
   * Called when closing the openGL context. 
   * @param drawable Automatically supplied by the system.
   */ 
  public void dispose(GLAutoDrawable drawable) {
  }

  /**
   * The mouse is used to control the camera position.
   *
   * @param e instance of MouseEvent, automatically supplied by the system when the user drags the mouse
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    
    float dx=(float) (ms.x-lastpoint.x)/width;
    float dy=(float) (ms.y-lastpoint.y)/height;
    
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK) {
      camera.updateThetaPhi(-dx*2.0f, dy*2.0f);
    }
    else if (e.getModifiers()==MouseEvent.BUTTON3_MASK) {
      camera.updateRadius(-dy);
    }
    
    lastpoint = ms;
  }

  /**
   * The mouse is used to control the camera position.
   *
   * @param e  instance of MouseEvent, automatically supplied by the system when the user moves the mouse
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }

}
