package com.example.trapmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

public class LbamActivity  extends Activity {
	
	private ServiceManager datasource;
	private String serviceData = null;
	private String grid = null;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lbam);
        
        datasource = new ServiceManager( this );
        datasource.open();
	}
	
	public void regainFocus( View view ){
		view.requestFocus();
	}
	
	public void commit( View view ){
		
		this.compileServiceData();
		/**
		// add the service activity to the database and return a TrapService object
		if ( this.serviceData != null && grid != null )
			datasource.createTrapService( grid, "LBAM", this.serviceData );
		*/
		AlertDialog.Builder adb = new AlertDialog.Builder( this );
		adb.setMessage( "Service Activity added!" );
		adb.setPositiveButton(
		  "Close", 
		  new DialogInterface.OnClickListener()
			{
			  public void onClick(DialogInterface arg0, int arg1) 
				{
				  // close the Dialog and its parent activity, EgvmActivity.
	    		   LbamActivity.this.finish();
				}
			  }
		);
		
		AlertDialog dlg = adb.create();
		dlg.show();
	}
	
	private void compileServiceData(){
		
		RadioGroup rbg = (RadioGroup) findViewById( R.id.rbg_placed_or_serviced );
		
	    CheckBox cbBaited 	   = (CheckBox) findViewById( R.id.cbBaited 	 );
	    CheckBox cbInsert	   = (CheckBox) findViewById( R.id.cbInsert		 );
	    CheckBox cbNewtrap 	   = (CheckBox) findViewById( R.id.cbNewtrap   	 );
	    CheckBox cbRelocated   = (CheckBox) findViewById( R.id.cbRelocated   );
	    CheckBox cbRemoved     = (CheckBox) findViewById( R.id.cbRemoved     );
	    CheckBox cbRotated     = (CheckBox) findViewById( R.id.cbRotated     );
	    CheckBox cbSubmitted   = (CheckBox) findViewById( R.id.cbSubmitted   );
	    EditText etGridSubgrid = (EditText) findViewById( R.id.etGridSubgrid );
	    
		this.grid = etGridSubgrid.getText().toString();
	    
	    switch ( rbg.getCheckedRadioButtonId() ){
		    case R.id.rbServiced:
		    	appendServiceActivity( "X:" );
		    	break;
		    case R.id.rbMissing:
		    	appendServiceActivity( "M:" );
		    	break;
		    case R.id.rbUS:
		    	appendServiceActivity( "US:" );
		    	break;
	    }
	    
	    if ( cbBaited.isChecked() ){
	    	appendServiceActivity( "B:" );
	    }
	    
	    if ( cbNewtrap.isChecked() ){
	    	appendServiceActivity( "NT:" );
	    }
	    if ( cbRelocated.isChecked() ){
	    	appendServiceActivity( "RL:" );
	    }
	    if ( cbRemoved.isChecked() ){
	    	appendServiceActivity( "RM:" );
	    }
	    if ( cbRotated.isChecked() ){
	    	appendServiceActivity( "R:" );
	    }
	    if ( cbSubmitted.isChecked() ){
	    	appendServiceActivity( "S:" );
	    }
	    if ( cbInsert.isChecked()    ){
	    	appendServiceActivity( "I:" );
	    }
	    
	    
	    //System.out.println( this.serviceData + "=CRL");
	}
	
	private void appendServiceActivity( String activity ){
		if ( this.serviceData == null )
			this.serviceData = activity;
		else 
			this.serviceData = this.serviceData + activity;
	}
	
	public void cancelServiceActivity( View view ){
		finish();
	}
	
	protected void onResume() {
	    datasource.open();
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
	    datasource.close();
	    super.onPause();
	  }
}

