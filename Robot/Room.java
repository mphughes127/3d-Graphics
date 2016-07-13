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

public class Room{
  private GLU glu = new GLU();
  private GLUT glut = new GLUT();
  
 
  private Render room,floor,ceiling; 

  public static final int wallHeight = 20;


/*
 *  I declare that this code is my own work 
 *  Author Matthew Hughes mphughes1@sheffield.ac.uk 
 */

 public Room() {

 }

	public void createRenderObjects(GL2 gl, Texture wallTex,Texture floorTex,Texture roofTex) {


		Wall wall = new Wall(wallHeight,wallHeight,wallTex);
		Wall base = new Wall(wallHeight,wallHeight,floorTex);
		Wall roof = new Wall(wallHeight,wallHeight,roofTex);
		room = wall.renderWall(gl);
		floor = base.renderWall(gl);
		ceiling = roof.renderWall(gl);

}
	public void drawRoom(GL2 gl){
	drawFloor(gl);
	drawWall(gl);
	drawCeiling(gl);
	drawObjects(gl);
	}

	private void drawWall(GL2 gl){
		
		//draw left wall
		gl.glPushMatrix();
		gl.glTranslated(-wallHeight/2, wallHeight/2, 0);
		gl.glRotated(-90, 0, 0, 1);
		room.renderDisplayList(gl);
		gl.glPopMatrix();

		//draw right wall
		gl.glPushMatrix();
		gl.glTranslated(wallHeight/2, wallHeight/2, 0);
		gl.glRotated(90, 0, 0, 1);
		room.renderDisplayList(gl);
		gl.glPopMatrix();
		
		
		//draw front wall
		gl.glPushMatrix();
		gl.glTranslated(0, wallHeight/2, -wallHeight/2);
		gl.glRotated(90, 0, 1, 0);
		gl.glRotated(90, 0, 0, 1);
		room.renderDisplayList(gl);
		gl.glPopMatrix();
		
		//draw back wall
		gl.glPushMatrix();
		gl.glTranslated(0, wallHeight/2, wallHeight/2);
		gl.glRotated(-90, 0, 1, 0);
		gl.glRotated(90, 0, 0, 1);
		room.renderDisplayList(gl);
		gl.glPopMatrix();
		
	}
	private void drawFloor(GL2 gl)
	{
		gl.glPushMatrix();
						
			floor.renderDisplayList(gl);
		
		gl.glPopMatrix();
		
	}
	private void drawCeiling(GL2 gl)
	{
		gl.glPushMatrix();
						
			gl.glTranslated(0,10,0);
			gl.glRotated(180,1,0,0);
			ceiling.renderDisplayList(gl);
			
		gl.glPopMatrix();
		
	}
	private void drawObjects(GL2 gl){
		
		gl.glPushMatrix();
		gl.glTranslated(7,0,-7);
 		drawTable(gl);
		gl.glPopMatrix();
	
		gl.glPushMatrix();
		gl.glTranslated(7,0,7);
 		drawTable(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(-7,0,0);
 		drawTable(gl);
		gl.glPopMatrix();
	
	}

	private void drawTable(GL2 gl){

		//base
		gl.glPushMatrix();
			gl.glTranslated(0,0.1,0);
			gl.glScaled(1,0.1,1);
			glut.glutSolidSphere(1.0f,20,20);

		gl.glPopMatrix();

		//pole
		gl.glPushMatrix();
			gl.glTranslated(0,1,0);
			gl.glScaled(0.2,1,0.2);
			glut.glutSolidSphere(1.0f,20,20);

		gl.glPopMatrix();

		//top
		gl.glPushMatrix();
			gl.glTranslated(0,2,0);
			gl.glScaled(1,0.1,1);
			glut.glutSolidSphere(2.0f,20,20);

		gl.glPopMatrix();

	}


	
	
}