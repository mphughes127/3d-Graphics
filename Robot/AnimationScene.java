/**
 * A class for controlling a set of Anim instances
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (21/11/2013)
 */
  

  /*edited animation scene and the global variables
 *  I declare that this code is my own work 
 *  Author Matthew Hughes mphughes1@sheffield.ac.uk 
 */
public class AnimationScene {

  public static final int ROBOT_X_PARAM = 0;
  public static final int ROBOT_Z_PARAM = 1;
  public static final int ROBOT_ROTATION = 2;
  public static final int ROBOT_ROTATION2 = 3;
  public static final int ROBOT_ROTATION3 = 4;
  public static final int ROBOT_ARM = 5;
  public static final int ROBOT_LEAN = 6;
  public static final int ROBOT_LEAN2 = 7;
  public static final int ROBOT_LEAN3 = 8;
  public static final int ROBOT2_X_PARAM = 9;
  public static final int ROBOT2_Z_PARAM = 10;
  public static final int ROBOT2_ROTATION = 11;
  public static final int ROBOT2_ROTATION2 = 12;

  public static final int MAX_PARAMS = 15;
  private Anim[] param;
  private int numParams;
  private double globalStartTime, localTime, repeatTime, savedLocalTime; 
    
  /**
   * Constructor.
   *
   * @param keys List of key info, i.e. list of pairs {key frame value, key parameter value}
   */    
  public AnimationScene() {
    param = new Anim[MAX_PARAMS];
    
    param[ROBOT_X_PARAM] = create(0.0,15.0,true,true, new double[]{0.0,0.0, 0.33,9.0, 0.66,9.0, 1.0,0.0});
    param[ROBOT_Z_PARAM] = create(0.0,15.0,true,true, new double[]{0.0,0.0, 0.33,5.0, 0.66,-5.0, 1.0,0.0});
    param[ROBOT_ROTATION] = create(4.5,1.0,true,true, new double[]{0.0,0.0, 1.0,119});
    param[ROBOT_ROTATION2] = create(9.0,1.0,true,true, new double[]{0.0,0.0, 1.0,119});
    param[ROBOT_ROTATION3] = create(13.5,1.0,true,true, new double[]{0.0,0.0, 1.0,121});

    param[ROBOT_ARM] = create(0.0,15.0,true,true, new double[]{0.0,0.0, 0.25,-60, 0.5,60, 0.75,-60, 1.0,0.0});
    param[ROBOT_LEAN] = create(4.0,2.0,true,true, new double[]{0.0,0.0, 0.5,-30, 1.0,0.0});
    param[ROBOT_LEAN2] = create(8.5,2.0,true,true, new double[]{0.0,0.0, 0.5,-30, 1.0,0.0});
    param[ROBOT_LEAN3] = create(13,2.0,true,true, new double[]{0.0,0.0, 0.5,-30, 1.0,0.0});

    param[ROBOT2_X_PARAM] = create(0.0,15.0,true,true, new double[]{0.0,0.0, 0.5,6.0, 0.9,0.0, 1.0,0.0});
    param[ROBOT2_Z_PARAM] = create(0.0,15.0,true,true, new double[]{0.0,0.0, 0.5,6.0, 0.9,0.0, 1.0,0.0});
    param[ROBOT2_ROTATION] = create(6.5,1.0,true,true, new double[]{0.0,0.0, 1.0,180});
    param[ROBOT2_ROTATION2] = create(13.0,1.0,true,true, new double[]{0.0,0.0, 1.0,180});

    numParams = ROBOT2_ROTATION2 + 1;
    localTime = 0;
    savedLocalTime = 0;
    repeatTime = 15;
    globalStartTime = getSeconds();
  }
  
  public Anim create (double start, double duration, boolean pre, boolean post, double[] data) {
    KeyInfo[] k = new KeyInfo[data.length/2];
    for (int i=0; i<data.length/2; ++i) {
      k[i] = new KeyInfo(data[i*2], data[i*2+1]);
    }    
    return new Anim(start, duration, pre, post, k);
  }
  
  public void startAnimation() {
    globalStartTime = getSeconds() - savedLocalTime;
  }
  
  public void pauseAnimation() {
    savedLocalTime = getSeconds() - globalStartTime;
  }
  
  public void reset() {
    globalStartTime = getSeconds();
    savedLocalTime = 0;
    for (int i=0; i<numParams; ++i) {
      param[i].reset();
    }
  }
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
  /**
   * 
   */ 
  public void update() {
    localTime = getSeconds() - globalStartTime;
    if (localTime > repeatTime) {
      globalStartTime = getSeconds();
      localTime = 0;
      savedLocalTime = 0;
    }  
    for (int i=0; i<numParams; ++i) {
      param[i].update(localTime);
    }
  }

 /**
   * 
   *
   * @return The current parameter value
   */   
  public double getParam(int i) {
    if (i<0 || i>=numParams) {
      System.out.println("EEError: parameter out of range");
      System.out.println(i);
      return 0;
    }
    else {
      return param[i].getCurrValue();
    }
  }
  
  /**
   * Standard use of toString method
   * 
   * @return A string representing the key data
   */      
  public String toString() {
    String s = "Anim manager: ";
    return s;
  }

  public static void main(String[] args) {
    AnimationScene a = new AnimationScene();  
    //System.out.println(a.getParam(a.LIGHT_PARAM));
    double start = a.getSeconds();
    double t=start;
    while (t<start+20) {
      double ls = a.getSeconds();
      double lt = ls;
      while (lt < ls+0.2) lt = a.getSeconds();
      a.update();    
     // System.out.println(a.localTime + ", " + a.getParam(a.LIGHT_PARAM));
      t = a.getSeconds();
    }
  }
  
}
