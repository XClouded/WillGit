package com.willin.ui.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//mark 1
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class GLSquare {

	private FloatBuffer vertexBuffer;
	private ByteBuffer  colorBuffer;
	private ByteBuffer  indexBuffer;

	// ================================================================================================================================ //		
				
	
	// constructor
	public GLSquare() {
		
		// mark 2
		// 4 vertices. 4 pointers
		float vertices[] = {
				
				-1.0f, -1.0f,
				1.0f, -1.0f,
				-1.0f, 1.0f,
				1.0f, 1.0f
				
		};
		
		
		byte maxColor = (byte)255;
		
		// mark 3
		byte colors[] = {
				maxColor, maxColor, 0, maxColor,
				0, maxColor, maxColor, maxColor,
				0, 0, 0, maxColor,
				maxColor, 0, maxColor, maxColor,				
		};
		
		// mark 4
		byte indices[] = {
				0, 3, 1,
				0, 2, 3,
		};
		
		// mark 5
		ByteBuffer vbb = ByteBuffer.allocateDirect( vertices.length * 4 );
		vbb.order( ByteOrder.nativeOrder() );
		
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put( vertices );
		vertexBuffer.position( 0 );
		
		colorBuffer = ByteBuffer.allocateDirect( colors.length * 4 );
		colorBuffer.put( colors );
		colorBuffer.position( 0 );
		
		indexBuffer = ByteBuffer.allocateDirect( indices.length * 4 );
		indexBuffer.put( indices );
		indexBuffer.position( 0 );
		
	}
	
	
	
	public void draw( GL10 gl ) {
		
		// mark 7
		gl.glFrontFace( GL11.GL_CW );
		
		// mark 8
		gl.glVertexPointer( 2, GL11.GL_FLOAT, 0, vertexBuffer );
		// mark 9
		gl.glColorPointer( 4, GL11.GL_UNSIGNED_BYTE, 0, colorBuffer );
		// mark 10
		gl.glDrawElements( GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, indexBuffer );
		
		// mark 11
		gl.glFrontFace( GL11.GL_CW );
		
	}
	
	
	
	
	
	
	
}


// end of file