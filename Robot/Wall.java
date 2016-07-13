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

/*
 *  I declare that this code is my own work 
 *  Author Matthew Hughes mphughes1@sheffield.ac.uk
 */

public class Wall{
  private GLU glu = new GLU();
  private GLUT glut = new GLUT();
  private float slices = 0;
  private int x = 0;
  private int y = 0;
  private Mesh meshWall;
  private Render wall, floor;
  private Texture tex;

 public Wall(int x, int y,Texture wallTex) {
	this.tex = wallTex;
	this.x = x;
	this.y = y;
 }
	public Render renderWall(GL2 gl) {              
		
		meshWall = ProceduralMeshFactory.createPlane(x,y,x*10,y*10,1,1);  
		Material mat = meshWall.getMaterial();   
		wall = new Render(meshWall,tex);    
		wall.initialiseDisplayList(gl, true);  
		return wall;
	
	}


	
	
}