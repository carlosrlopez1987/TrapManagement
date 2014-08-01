package com.example.trapmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	//SHA1
	//5B:61:66:AF:33:05:71:B0:31:1F:39:63:9D:6E:30:80:F4:3B:7E:5E;com.example.trapmanagement
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
    }
    
    public void handleButton( View view ){
    	int buttID = view.getId();
    	
      if ( buttID != R.id.egvm && buttID != R.id.acp )
        switch ( buttID ){
		  case R.id.lbam:
			  startAddServiceActivity( buttID );
		  break;
		  case R.id.addTrap_butt:
			  startNewActivity( PlaceTrapActivity.class, R.id.addTrap_butt );
		  break;
		  case R.id.gwss:
			  startNewActivity( GwssActivity.class, R.id.gwss );
		  break;
		  case R.id.stats:
			  startNewActivity( TrapStatsActivity.class, R.id.stats );
		  break;
		  case R.id.acp:
			  startNewActivity( AcpActivity.class, R.id.acp );
		  break;
		  case R.id.viewTrap_butt:
			  startNewActivity( TrapViewActivity.class, R.id.viewTrap_butt );
			  break;
		  case R.id.closeProg:
			finish();
		  break;
		  case R.id.view_map:
			startNewActivity( MapPane.class, R.id.view_map );
		  break;
        }
      else
        {
    	  startAddServiceActivity( buttID );
        }
    }
    
    public void startAddServiceActivity( int buttID ){
      Intent asa = new Intent( this, AddServiceActivity.class );
      asa.putExtra( "PROG_ID", buttID );
        
      try 
        {
      	  startActivity( asa );
        }
      catch ( ActivityNotFoundException e )
        {
      	  Toast.makeText( 
      		this, 
      		e.getMessage().toString(),
      		Toast.LENGTH_LONG
      	  ).show();
        }
    }
    
    public void startNewActivity( Class<?> cls, int buttID ){
    	
      Intent newActivity = new Intent( this, cls );
      newActivity.putExtra( "PROG_ID", buttID );
      
      try 
        {
    	  startActivity( newActivity );
        }
      catch ( ActivityNotFoundException e )
        {
    	  Toast.makeText( 
    		this, 
    		e.getMessage().toString(),
    		Toast.LENGTH_LONG
    	  ).show();
        }
    }
}

