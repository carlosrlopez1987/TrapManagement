package com.example.trapmanagement;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MapPane extends  ActionBarActivity {

    protected void onCreate( Bundle savedInstanceState ) {
      
      try 
        {
    	  super.onCreate( savedInstanceState );
          setContentView( R.layout.map );
        }
      catch ( RuntimeException e )
        {
          Toast.makeText( 
    		this, 
    		e.getMessage().toString(),
    		Toast.LENGTH_LONG
    	  ).show();
        }

        
                
    }
}
