package com.example.trapmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

public class GwssActivity  extends Activity {
	
	private ServiceManager datasource;
	private String serviceData  = null;
	private String grid 		= null;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gwss);
        
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
		if ( this.serviceData != null )
			datasource.createTrapService( grid, "GWSS", this.serviceData );
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
	    		   GwssActivity.this.finish();
				}
			  }
		);
		
		AlertDialog dlg = adb.create();
		dlg.show();
	}
	
	private void compileServiceData(){
		EditText etGridSubgrid = (EditText) findViewById( R.id.etGridSubgrid );
		this.grid = etGridSubgrid.getText().toString();
		
		RadioGroup rbg = (RadioGroup) findViewById( R.id.rbg_placed_or_serviced );
		
	    CheckBox cbNewtrap 	 = (CheckBox) findViewById( R.id.cbNewtrap   );
	    CheckBox cbRelocated = (CheckBox) findViewById( R.id.cbRelocated );
	    CheckBox cbRemoved   = (CheckBox) findViewById( R.id.cbRemoved   );
	    CheckBox cbRotated   = (CheckBox) findViewById( R.id.cbRotated   );
	    CheckBox cbSubmitted = (CheckBox) findViewById( R.id.cbSubmitted );
	    
	    switch ( rbg.getCheckedRadioButtonId() ){
		    case R.id.rbServiced:
		    	appendServiceActivity( "X:" );
		    	break;
		    case R.id.rbMissing:
		    	appendServiceActivity( "M:" );
		    	break;
	    }
	    
	    if ( cbNewtrap.isChecked() ){
	    	appendServiceActivity( "NT:" );
	    	cbRemoved.setEnabled( false );
	    }
	    if ( cbRelocated.isChecked() ){
	    	appendServiceActivity( "RL:" );
	    	cbRemoved.setEnabled( false );
	    }
	    if ( cbRemoved.isChecked() ){
	    	appendServiceActivity( "RM:" );
	    	cbRelocated.setEnabled( false );
	    }
	    if ( cbRotated.isChecked() ){
	    	appendServiceActivity( "R:" );
	    }
	    if ( cbSubmitted.isChecked() ){
	    	appendServiceActivity( "S:" );
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

