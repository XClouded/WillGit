package com.willin.ui.glview;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.willin.ui.shape.GLSquare;

import android.opengl.GLSurfaceView.Renderer;

public class MyGLRenderer implements Renderer {

	private boolean translucentBackground;
	private GLSquare square;
	private float transY;
	private float angle;
	
	
// ===================================================================================================== //	
	
	// constructor
	public MyGLRenderer( boolean useTranslucentBackground ) {
	
		translucentBackground = useTranslucentBackground;
		
		// mark 3
		square = new GLSquare();
		
	}
	
	
	
	@Override
	public void onDrawFrame(GL10 gl) {

        // mark 5
        gl.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );

        // mark 6
        gl.glMatrixMode( GL10.GL_MODELVIEW );
        // mark 7
        gl.glLoadIdentity();
        // mark 8
        gl.glTranslatef( 0.0f, (float)Math.sin(transY), -3.0f );
        
        // mark 9
        gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
        gl.glEnableClientState( GL10.GL_COLOR_ARRAY );
        
        // mark 10
        square.draw(gl);
        
        transY += 0.075f;
        
	}
	
	

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		// mark 12
		gl.glViewport(0, 0, width, height);
		
		float ratio = (float) (width / height);
		// mark 13
		gl.glMatrixMode( GL10.GL_PROJECTION );
		
		gl.glLoadIdentity();
		// mark 14
		gl.glFrustumf( -ratio, ratio, -1, 1, 1, 10 );
		
	}
	
	

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		// mark 16
		gl.glDisable( GL10.GL_DITHER );
		
		// mark 17
		gl.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST );
		
		// mark 18
		if ( translucentBackground ) {
	        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		}
		else {
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
		
		// mark 19
        gl.glEnable( GL10.GL_CULL_FACE );
        // mark 20
        gl.glShadeModel( GL10.GL_SMOOTH );
        // mark 21
        gl.glEnable( GL10.GL_DEPTH_TEST );
		
		
	}

	
	
}


// end of file