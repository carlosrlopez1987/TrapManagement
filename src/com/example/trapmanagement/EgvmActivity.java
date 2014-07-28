package com.example.trapmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EgvmActivity  extends Activity implements LocationListener {
	
	private ServiceManager servMan;
	private TrapsManager trapMan;
	private String serviceData = null;
	private String grid = null;
	private String prog = null;
	private String host = null;
	
	private LocationManager locationManager;
	private String provider;
	private TextView tvLongData;
	private TextView tvLattData;
	private double gpsLong;
	private double gpsLatt;
	
	private RadioGroup rbg;
	private CheckBox cbBaited;
	private CheckBox cbNewtrap;
	private CheckBox cbRelocated;
	private CheckBox cbRemoved;
	private CheckBox cbRotated;
	private CheckBox cbSubmitted;
	private EditText etGridSubgrid;
	private EditText etHost;
	public static final String PROG = "EGVM";
	
	protected void onCreate( Bundle savedInstanceState ) {
		
        super.onCreate( savedInstanceState );
        
        setContentView( R.layout.egvm );
        
        servMan = new ServiceManager( this );
        trapMan = new TrapsManager(   this );
        
        tvLongData = (TextView) findViewById( R.id.tvLongData );
        tvLattData = (TextView) findViewById( R.id.tvLattData );
        
        // Get the location manager
        locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        
        // if no location is locked, use last known location
        Criteria criteria = new Criteria();
        		 provider = locationManager.getBestProvider( criteria, false );	
        Location location = locationManager.getLastKnownLocation( provider );
        
        // Initialize the location fields
        if ( location != null )
          onLocationChanged( location );
        else 
          {// we have no gps lock
            tvLongData.setText( "Location not available" );
            tvLattData.setText( "Location not available" );
          }
        
        try 
          {// start the sqlite database managers for traps and services
            servMan.open();
            trapMan.open();
          } // end try block
        catch ( SQLException e )
          {// if we can't start the DB managers catch the error
        	// create an alert dialog for the error/exception
        	AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

        	dlgAlert.setMessage( e.getMessage().toString() );
        	dlgAlert.setTitle( "SQLException" );
        	
        	dlgAlert.setPositiveButton("OK", 
        	  new DialogInterface.OnClickListener(){
                public void onClick( DialogInterface dialog, int which ){
                  EgvmActivity.this.finish();
                }
            });
        	
        	dlgAlert.setCancelable(true);
        	AlertDialog dialog = dlgAlert.create();
        	dialog.show();
        	
        	Toast.makeText( 
      		  this, 
      		  e.getMessage().toString(),
      		  Toast.LENGTH_LONG
      		).show();
        	
          } // end catch block
        
        		  rbg = (RadioGroup) findViewById( R.id.rbGroup );
        	 cbBaited = (CheckBox) findViewById( R.id.cbBaited 	 	);
        	cbNewtrap = (CheckBox) findViewById( R.id.cbNewtrap   	);
          cbRelocated = (CheckBox) findViewById( R.id.cbRelocated   );
            cbRemoved = (CheckBox) findViewById( R.id.cbRemoved     );
            cbRotated = (CheckBox) findViewById( R.id.cbRotated     );
	      cbSubmitted = (CheckBox) findViewById( R.id.cbSubmitted   );
	    etGridSubgrid = (EditText) findViewById( R.id.etGrid );
	    etHost = (EditText) findViewById( R.id.etHost );
	}
	
	public void regainFocus( View view ){
		view.requestFocus();
	}
	
	private void disableCheckBox( CheckBox cb ){
	  if ( cb.isEnabled() )
	    {
	      cb.setEnabled(false);
	      cb.setTextColor( getResources().getColor( R.color.cb_disabledColor ) );
	    }
	}
	
	private void enableCheckBox( CheckBox cb ){
	  if ( !cb.isEnabled() )
	    {
		  cb.setEnabled( true );
		  cb.setTextColor( getResources().getColor( R.color.cb_enabledColor ) );
	    }
	}
	
	public void uncheckCB( CheckBox cb ){
		if ( cb.isChecked() )
		  {
		    cb.setChecked( false );
		    cb.setTextColor( getResources().getColor( R.color.cbUnChecked ) );
		  }
	}
	
	public void handleCBClick( View v ){
		
	  int cbID = v.getId();
	  CheckBox cb = (CheckBox) findViewById( cbID );
	  changeCBColor( cb );
	  
	  switch ( cbID ){
	    case R.id.cbBaited:
	    	if ( cbNewtrap.isChecked() )
	    	  checkCB( cbBaited );
	      break;
	    case R.id.cbInsert:
	    	
		      break;
	    case R.id.cbNewtrap:
	    	if ( !cbNewtrap.isChecked() )
	    		uncheckCB( cbSubmitted );
	    	else
	    		if ( !cbBaited.isChecked() )
	    		  checkCB( cbBaited );
		      break;
	    case R.id.cbRelocated:
	    	if ( cbRelocated.isChecked() )
	    	  {
	    	    disableCheckBox( cbRotated );
	    	    disableCheckBox( cbRemoved );
	    	  }
	    	else
	    	  {
	    		enableCheckBox( cbRotated );
	    	    enableCheckBox( cbRemoved );
	    	  }
		      break;
	    case R.id.cbRemoved:
	      if ( cbRemoved.isChecked() )
	        {
	    	  disableCheckBox( cbRotated );
	    	  disableCheckBox( cbRelocated );
	        }
	      else
	        {
	    	  enableCheckBox( cbRotated );
	    	  enableCheckBox( cbRelocated );
	        }
		      break;
	    case R.id.cbRotated:
	      if ( cbRotated.isChecked() )
	        {
	    	  disableCheckBox( cbRemoved );
	    	  disableCheckBox( cbRelocated );
	        }
	      else
	        {
	    	  enableCheckBox( cbRemoved );
	    	  enableCheckBox( cbRelocated );
	        }
		      break;
	    case R.id.cbSubmitted:
	      if ( cbSubmitted.isChecked() )
	        {
	    	  checkCB( cbNewtrap );
	    	  checkCB( cbBaited  );
	        }
		      break;
	  }
	}
	
	private void changeCBColor( CheckBox cb ){
      if ( cb.isChecked() )
		cb.setTextColor( getResources().getColor( R.color.cbChecked ) );
      else
    	cb.setTextColor( getResources().getColor( R.color.cbUnChecked ) );
	}
	
	public void checkCB( CheckBox cb ){
		if ( !cb.isChecked() )
		  {
		    cb.setChecked( true );
		    cb.setTextColor( getResources().getColor( R.color.cbChecked ) );
		  }
	}
	
	public void handleRBClick( View view ){
		switch ( rbg.getCheckedRadioButtonId() ){
	      case R.id.rbServiced:
	    	  uncheckCB( this.cbNewtrap );
	    	  uncheckCB( this.cbBaited );
	    	  
	    	  enableCheckBox( this.cbNewtrap );
	    	  enableCheckBox( this.cbBaited );
	    	  enableCheckBox( this.cbRelocated );
	    	  enableCheckBox( this.cbRemoved );
	    	  enableCheckBox( this.cbRotated );
	    	  enableCheckBox( this.cbSubmitted );
	      break;
	      case R.id.rbMissing:
	    	  enableCheckBox( this.cbNewtrap );
	    	  enableCheckBox( this.cbBaited );
	    	  enableCheckBox( this.cbRelocated );
	    	  enableCheckBox( this.cbRemoved );
	    	  enableCheckBox( this.cbRotated );
	    	  
	    	  uncheckCB( this.cbSubmitted );
	    	  disableCheckBox( this.cbSubmitted );
	    	  
	    	  checkCB( this.cbNewtrap );
	    	  checkCB( this.cbBaited  );
	      break;
	      case R.id.rbUS:
	    	  uncheckCB( this.cbNewtrap );
	    	  disableCheckBox( this.cbNewtrap );
	    	  
	    	  uncheckCB( this.cbBaited );
	    	  disableCheckBox( this.cbBaited );
	    	  
	    	  uncheckCB( this.cbRelocated );
	    	  disableCheckBox( this.cbRelocated );
	    	  
	    	  uncheckCB( this.cbRemoved );
	    	  disableCheckBox( this.cbRemoved );
	    	  
	    	  uncheckCB( this.cbRotated );
	    	  disableCheckBox( this.cbRotated );
	    	  
	    	  uncheckCB( this.cbSubmitted );
	    	  disableCheckBox( this.cbSubmitted );
	      break;
		}
	}
	
	public void commit( View view ){
		String adMsg = "";
		this.compileServiceData();			// gather all info from user
		/**
		 * IF STRING IS NOT NULL OR IN OTHER WORDS DO WE HAVE A GRID STRING?
		 */
		if ( null != grid )
		  {
			if ( trapMan.trapExistByGrid( grid ) )
			  {
				long insertServID = 0;
				
				Trap tt = trapMan.getTrapByGrid( this.grid );
				
				// add the service activity to the database and return a TrapService object
				if ( this.serviceData != null )
				  {
				    insertServID = servMan.createTrapService( "EGVM", this.grid, "CRL", this.host,  this.serviceData );
				   
				    if ( insertServID > 0 )
				      adMsg = "Service Activity Added!";
				  }
				
				Toast.makeText( 
				  this, 
				  adMsg,
				  Toast.LENGTH_SHORT
				).show();
				
				// pause the ending process a little bit
				try  { Thread.sleep( 200 ); } catch ( InterruptedException e ){}
				
				// end activity and go to main activity
				this.finish();
			  }// end if trap exists
			else
			  {
				String alertMSG = "Trap " + grid + " does not exist, make sure to add trap before servicing!";
				
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder( this );

	        	dlgAlert.setMessage( alertMSG );
	        	dlgAlert.setTitle( "Trap Grid Problem" );
	        	
	        	dlgAlert.setPositiveButton("OK", 
	        	  new DialogInterface.OnClickListener(){
	        		
	                public void onClick( DialogInterface dialog, int which ){
	                  // add an onFocusListener
	                  EgvmActivity.this.etGridSubgrid.setOnFocusChangeListener(
	                	// Highlight the text inside the textbox
	                    new OnFocusChangeListener()
	                    {
	                	  public void onFocusChange(View v, boolean hasFocus)
	                	  {
	                	    if ( hasFocus )
	                	      ((EditText)v).selectAll();
	                      }
	                    }
	                  );	
	                	
	                  EgvmActivity.this.etGridSubgrid.requestFocus();
	                }
	            });
	        	
	        	dlgAlert.setCancelable(true);
	        	AlertDialog dialog = dlgAlert.create();
	        	dialog.show();
				
				Toast.makeText( 
				  this, 
				  alertMSG,
				  Toast.LENGTH_SHORT
				).show();
			  }
		}// end if grid != null
	  else
	    {
		  Toast.makeText( 
		    this, 
		    "Grid value cannot be null!, ending activity!",
		    Toast.LENGTH_SHORT
		  ).show();
		  
		  EgvmActivity.this.finish();
	    }
	}
	
	private void compileServiceData(){
		
		AlertDialog.Builder dlgAlert;
		String alertMSG = null;
		
		this.host = etHost.getText().toString();
		this.grid = etGridSubgrid.getText().toString() + "-" + EgvmActivity.PROG;
		
		if ( grid.isEmpty() )
		  {
			alertMSG = "Grid field is empty! Please make sure to fill every field.";
			
			dlgAlert = new AlertDialog.Builder( this );

        	dlgAlert.setMessage( alertMSG );
        	dlgAlert.setTitle( "Grid Field Problem" );
        	
        	dlgAlert.setPositiveButton("OK", 
        	  new DialogInterface.OnClickListener(){
        		
                public void onClick( DialogInterface dialog, int which ){
                  // add an onFocusListener
                  EgvmActivity.this.etGridSubgrid.setOnFocusChangeListener(
                	// Highlight the text inside the textbox
                    new OnFocusChangeListener()
                    {
                	  public void onFocusChange(View v, boolean hasFocus)
                	  {
                	    if ( hasFocus )
                	      ((EditText)v).selectAll();
                      }
                    }
                  );	
                	
                  EgvmActivity.this.etGridSubgrid.requestFocus();
                }
            });
        	
        	if ( host.isEmpty() )
	  		  {
	  			alertMSG = "Host field is empty! Please make sure to fill every field.";
	  			
	  			dlgAlert = new AlertDialog.Builder( this );
	
	          	dlgAlert.setMessage( alertMSG );
	          	dlgAlert.setTitle( "Host Field Problem" );
	          	
	          	dlgAlert.setPositiveButton("OK", 
	          	  new DialogInterface.OnClickListener(){
	          		
	                  public void onClick( DialogInterface dialog, int which ){
	                    // add an onFocusListener
	                    EgvmActivity.this.etHost.setOnFocusChangeListener(
	                  	// Highlight the text inside the textbox
	                      new OnFocusChangeListener()
	                      {
	                  	  public void onFocusChange(View v, boolean hasFocus)
	                  	  {
	                  	    if ( hasFocus )
	                  	      ((EditText)v).selectAll();
	                        }
	                      }
	                    );	
	                  	
	                    EgvmActivity.this.etHost.requestFocus();
	                  }
	              });
	        	
	        	dlgAlert.setCancelable(true);
	        	AlertDialog dialog = dlgAlert.create();
	        	dialog.show();
			  }

	    switch ( rbg.getCheckedRadioButtonId() )
	    {
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
	    
	    if ( rbg.getCheckedRadioButtonId() != R.id.rbUS )
	      {
		    if ( cbNewtrap.isChecked() )
		      {
		    	appendServiceActivity( "NT:" );
		      }
		    
		    if ( cbBaited.isChecked() )
		      {
		    	appendServiceActivity( "B:" );
		    	cbRemoved.setActivated( false );
		      }
		    
		    if ( cbRelocated.isChecked() )
		      {
		    	appendServiceActivity( "RL:" );
		    	cbRemoved.setActivated( false );
		      }
		    
		    if ( cbRemoved.isChecked() )
		      {
		    	appendServiceActivity( "RM:" );
		    	cbRotated.setActivated( false );
		    	cbRelocated.setActivated( false );
		      }
		    
		    if ( cbRotated.isChecked() )
		      {
		    	appendServiceActivity( "R:" );
		    	cbRemoved.setActivated( false );
		      }
		    
		    if ( cbSubmitted.isChecked() )
		      {
		    	appendServiceActivity( "S:" );
		      }
	      }
		}
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
	
	protected void onResume(){
	  servMan.open();
	  trapMan.open();
	  super.onResume();
	  locationManager.requestLocationUpdates( provider, 400, 1, this );
	}

	  @Override
	protected void onPause(){
	  servMan.close();
	  trapMan.close();
	  super.onPause();
	  locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		
	  try 
	    {
		  this.gpsLong = location.getLatitude();
		  this.gpsLatt = location.getLongitude();
		  
		  tvLongData.setText( String.valueOf( this.gpsLong ) 	);
		  tvLattData.setText( String.valueOf( this.gpsLatt ) 	);
	    }
	  catch ( NullPointerException e )
	    {
		  Toast.makeText(
			this, 
			e.getMessage().toString(),
			Toast.LENGTH_SHORT
		  ).show();
	    }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Enabled new provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}
}

