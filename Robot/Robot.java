import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

/*
 *  I declare that this code is my own work 
 *  Author Matthew Hughes mphughes1@sheffield.ac.uk
 */
public class Robot {

	//resolution for drawing different parts of robot
	private int lowResolution=20;
	private int midResolution=50;
	private int highResolution=100;

	//Parameter to size robot components
	private float bodyRadius=1;
	private float headRadius=1;
	private float eyeHeight=30;
	private double shoulderRadius=0.5;
	private float armRadius=0.18f;
	private float armHeight=1.5f;
	private float forearmRadius=0.18f;
	private float forearmHeight=1f;
	private double eyeradius=0.18;
	private float elbowRadius=0.3f;
	private float handRadius=0.25f;
	private float fingerHeight=0.6f;
	private float trayHeight=1f;

	//used in moving arm and body during animation
	private double waggle=0;
	private double lean=0;

	public Robot(){
	}

 
	//robot heirarchy is shown via intentation
	public void display(GL2 gl,GLUT glut){  
		gl.glTranslated(0,(bodyRadius+(2*headRadius)),0);
		gl.glPushMatrix();

		drawBody(gl, glut);
		gl.glPopMatrix();

		gl.glPushMatrix();
		drawHead(gl, glut);
			gl.glPushMatrix();
			drawEye(gl,glut);
			gl.glPopMatrix();
			
			//the right parts of body
			gl.glPushMatrix();
			drawRightShoulder(gl, glut);
		  		drawRightUpArm(gl, glut);
		  			drawElbow(gl, glut);
		  				drawRightLowArm(gl, glut);
		  					drawHand(gl, glut);
		  						drawClaw(gl, glut);
		  							drawTray(gl, glut);
		  								drawCups(gl, glut);
		  	gl.glPopMatrix();
		  	//the left parts of body
		  	gl.glPushMatrix();
		  	drawLeftShoulder(gl, glut);
		  		drawLeftUpArm(gl, glut);
		  			drawElbow(gl, glut);
		  				drawLeftLowArm(gl, glut);
		  					drawHand(gl, glut);
		  						drawClaw(gl, glut);
		  	gl.glPopMatrix();
		 gl.glPopMatrix();
	
	
}
	
	//update robot animation params
	public void update(double waggle, double lean)
	{
		this.waggle= waggle;
		this.lean=lean;
	}
	
	//used for moving eye spotlight (and eye)
	public void transformForEye(GL2 gl)
	{
		gl.glRotatef(-45, 0, 1, 0);
		gl.glRotatef(this.eyeHeight, 0, 0, 1);
		gl.glTranslatef(this.headRadius, 0, 0);
	}
	

	//drawing robot components 
	private void drawHead(GL2 gl,GLUT glut)
	{
		setHeadMaterial(gl);
		gl.glRotated(lean,0,0,1);
		glut.glutSolidSphere(this.headRadius, highResolution, highResolution);
	}

	private void drawBody(GL2 gl,GLUT glut){
			setBodyMaterial(gl);
			gl.glTranslated(0,-2*(bodyRadius),0);
			glut.glutSolidSphere(1.0f,20,20);
	}


	private void drawEye(GL2 gl,GLUT glut)
	{
		setEyeMaterial(gl);
		transformForEye(gl);
		glut.glutSolidSphere(eyeradius, lowResolution, lowResolution);
	}
	

	private void drawRightShoulder(GL2 gl,GLUT glut)
	{
		setArmMaterial(gl); 
		gl.glRotatef(-60, 0, 1, 0);
		gl.glTranslatef(0, 0,this.headRadius);
		glut.glutSolidSphere(shoulderRadius, midResolution, midResolution);
	}

	private void drawRightUpArm(GL2 gl,GLUT glut)
	{
		
		setArmMaterial(gl);
		glut.glutSolidCylinder(armRadius, armHeight, midResolution, midResolution);
	}
	private void drawRightLowArm(GL2 gl,GLUT glut)
	{
		gl.glRotatef(90, 0, 1, 0);
		glut.glutSolidCylinder(forearmRadius, forearmHeight, midResolution, midResolution);
	}
	
	private void drawLeftUpArm(GL2 gl,GLUT glut)
	{
		setArmMaterial(gl); 
		gl.glRotated(this.waggle, 0, 1, 0);
		glut.glutSolidCylinder(armRadius, armHeight, midResolution, midResolution);
	}

	private void drawLeftLowArm(GL2 gl,GLUT glut)
	{
		gl.glRotatef(-90, 0, 1, 0);
		gl.glRotated(this.waggle, 1, 0, 0);
		glut.glutSolidCylinder(forearmRadius, forearmHeight, midResolution, midResolution);
	}
	private void drawLeftShoulder(GL2 gl,GLUT glut)
	{
		setArmMaterial(gl);   
		gl.glRotatef(150, 0, 1, 0);//left
		gl.glTranslatef(0, 0, this.headRadius);
		glut.glutSolidSphere(shoulderRadius, midResolution, midResolution);
	}
	private void drawElbow(GL2 gl,GLUT glut)
	{
		setForearmMaterial(gl);
		gl.glTranslatef(0, 0, armHeight);
		glut.glutSolidSphere(elbowRadius, midResolution, midResolution);
	}
	

	private void drawHand(GL2 gl,GLUT glut)
	{
		setHandMaterial(gl);
		gl.glTranslatef(0, 0, forearmHeight);
		glut.glutSolidSphere(handRadius, midResolution, midResolution);
	}
	private void drawOneFinger(GL2 gl,GLUT glut)
	{
		gl.glRotated(40, 1, 0, 0);
		gl.glTranslatef(0, 0, fingerHeight/2);
		gl.glScalef(0.1f, 0.1f, fingerHeight);
		glut.glutSolidCube(1f);
	}
	
	private void drawClaw(GL2 gl,GLUT glut)
	{   
		gl.glPushMatrix();
		drawOneFinger(gl, glut);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(120, 0, 0, 1);
		drawOneFinger(gl, glut);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(240, 0, 0, 1);
		drawOneFinger(gl, glut);
		gl.glPopMatrix();
	}
	
	private void drawTray(GL2 gl,GLUT glut){

		gl.glScaled(1,0.3,1);
		gl.glTranslated(0,0,0.5*trayHeight + handRadius);
		glut.glutSolidCube(1);

	}

	private void drawCups(GL2 gl,GLUT glut){
		setEyeMaterial(gl);

		gl.glPushMatrix();
		gl.glTranslated(0.2,0.5,0);
		gl.glRotated(-90,1,0,0);
		glut.glutSolidCylinder(0.1,1,20,20);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(-0.2,0.5,0);
		gl.glRotated(-90,1,0,0);
		glut.glutSolidCylinder(0.1,1,20,20);
		gl.glPopMatrix();
	}


	
	//set materials methods (based off of setMaterial function by Steve Maddock)
	private void setBodyMaterial(GL2 gl)
	{
		
		float[] matAmbient = {0.2f, 0.0f, 0.0f, 1.0f};
		float[] matDiffuse = {0.66f, 0.0f, 0.0f, 1.0f};
		float[] matShininess = {100.0f};//0~128
		float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, matDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, matEmission, 0);
		
	}

	private void setHandMaterial(GL2 gl)
	{
		
		float[] matAmbient = {0, 0.3f, 0, 1.0f};
		float[] matDiffuse = {0, 0.75f, 0, 1.0f};
		float[] matShininess = {100.0f};//0~128
		float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, matDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, matEmission, 0);
		
	}

	private void setHeadMaterial(GL2 gl)
	{
		float[] matAmbient = {0.0f, 0.0f, 0.0f, 1.0f};
		float[] matDiffuse = {0.0f, 0.0f, 0.0f, 1.0f};
		float[] matShininess = {100.0f};//0~128
		float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, matDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, matEmission, 0);
	}
	public static void setEyeMaterial(GL2 gl)
	{
		float[] matAmbient = {0.1f, 0.1f, 0.1f, 1.0f};
		float[] matDiffuse = {1, 1, 1, 1.0f};
		float[] matShininess = {100.0f};//0~128
		float[] matEmission = {0.5f, 0.5f, 0.5f, 1.0f};

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, matDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, matEmission, 0);
	}

	private void setArmMaterial(GL2 gl)
	{
		float[] matAmbient = {0.0f, 0.05f, 0.25f, 1.0f};
		float[] matDiffuse = {0.0f, 0.11f, 0.7f, 1.0f};
		float[] matShininess ={100.0f};//0~128
		float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, matDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, matEmission, 0);
	}

	private void setForearmMaterial(GL2 gl)
	{
		float[] matAmbient = {0.2f, 0.5f, 0.5f, 1.0f};
		float[] matDiffuse ={0.5f, 1, 1, 1.0f};
		float[] matShininess = {100.0f};//0~128
		float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, matDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, matEmission, 0);
	}
}