package com.example.trapmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AddServiceActivity extends Activity implements LocationListener {
	
	private int Layout2Display = 0;
	private int buttID = 0;
	
	//
	private ServiceManager servMan;
	private   TrapsManager trapMan;
	
	private String   serviceData = null;
	private String 			grid = null;
	private String 			prog = null;
	private String 			host = null;
	
	private LocationManager locationManager;
	private Criteria criteria;
	private Location location;
	private String   provider;
	
	private double gpsLong;
	private double gpsLatt;
	
	// radio buttons and their group
	private RadioGroup 			 rbg;
	private RadioButton rbServiced;
	private RadioButton rbMissing;
	private RadioButton rbUnable2Service;
	
	//
	private   CheckBox 	 	cbBaited; // applies to egvm, lbam, and JB
	private   CheckBox 	   cbNewWick; // applies to acp, i think
	private   CheckBox 	 cbNewInsert; // applies to lbam, acp, mex-fly
	private   CheckBox 	   cbNewtrap; // applies to all
	private   CheckBox 	 cbRelocated; // applies to all
	private   CheckBox 	   cbRemoved; // applies to all
	private   CheckBox 	   cbRotated; // applies to all
	private   CheckBox 	 cbSubmitted; // applies to all
	
	private   EditText etGrid;
	private   EditText etHost;
	
	private   TextView 	  tvLongData;
	private   TextView 	  tvLattData;
	
	public String activeProg = "";
	
	/** ============================================================================
	 * 		Create activity
	 ** ============================================================================ */
	protected void onCreate( Bundle savedInstanceState ){
	  super.onCreate( savedInstanceState );
	  
	  // open PROG_ID message to decide what layout to show
	  this.buttID = getIntent().getIntExtra( "PROG_ID", -1 );
	  this.Layout2Display = 0;
	  
	  // set the layout that will be shown in activity
	  setLayout2Display( buttID );
	  
	  // open layout set by sent message 'PROG_ID'
	  if ( Layout2Display > 0 )
	   {
		  // display layout
	     setContentView( Layout2Display );
	     
	     Toast.makeText( 
	       this, 
	       "Activity created by 'AddServiceActivity' class!",
	       Toast.LENGTH_LONG
	     ).show();
	     
	     // start all setup functions
	     beginSetup();
	   }
	  else
	    {
		  String title = "Layout Problem";
		  String msg = "There was a problem displaying the layout.";
		  
		  showAlertBox( msg, title );
	    }
	}
	
	private void setLayout2Display( int buttID ){
	  if ( buttID > 0 )
	  {
		switch( buttID )
		  {
		    case R.id.egvm:
		    	activeProg = "EGVM";
		    	Layout2Display = R.layout.egvm;
		      break;
			case R.id.addTrap_butt:
				Layout2Display = R.layout.place_trap;
			  break;
			case R.id.lbam:
				activeProg = "LBAM";
				Layout2Display = R.layout.lbam;
			  break;
			case R.id.gwss:
				activeProg = "GWSS";
				Layout2Display = R.layout.gwss;
			  break;
			case R.id.acp:
				activeProg = "ACP";
				Layout2Display = R.layout.acp;
			  break;
			case R.id.stats:
				Layout2Display = R.layout.stats;
			  break;
			default:
				showAlertBox(
				  "The layout chosen does not exist!",
				  "Layout Activity"
				);
			  break;
		  }// END SWITCH
	  }// END IF STMNT
	}
	
	private void showAlertBox( final String msg, String title ){
	  
	  String alertMSG =  msg;
		
	  AlertDialog.Builder dlgAlert = new AlertDialog.Builder( this );

      dlgAlert.setMessage( alertMSG );
      dlgAlert.setTitle( title );
    	
      dlgAlert.setPositiveButton( 
        "OK", 
    	new DialogInterface.OnClickListener()
        {
          public void onClick( DialogInterface dialog, int which )
          {
        	Toast.makeText( 
          	  AddServiceActivity.this, 
          	  msg,
          	  Toast.LENGTH_LONG
          	).show();  
        	  
            //AddServiceActivity.this.finish();
          }
        }
      );
      
      dlgAlert.setCancelable(true);
  	  AlertDialog dialog = dlgAlert.create();
  	  dialog.show();
	}// end of showAlertBox()
	
	private void beginSetup(){
		
	  etGrid = (EditText) findViewById( R.id.etGrid );
      etHost = (EditText) findViewById( R.id.etHost );
		
      tvLongData = (TextView) findViewById( R.id.tvLongData );
      tvLattData = (TextView) findViewById( R.id.tvLattData );
      
	  initializeRadioButtons();
	  initializeCheckBoxes();
      initializeDBManagers();
      initializeLocation();
	}
	
	private void initializeLocation(){
	  // Get the location manager
	  locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
	      
	  // if no location is locked, use last known location
	  criteria = new Criteria();
	  provider = locationManager.getBestProvider( criteria, false );	
	  location = locationManager.getLastKnownLocation( provider );
	      
	  // Initialize the location fields
	  if ( location != null )
	    onLocationChanged( location );
	  else 
	    {
		  // we have no gps lock
	      tvLongData.setText( "Location not available" );
	      tvLattData.setText( "Location not available" );
	    }
	}// end of initializeLocation()
	
	private void initializeDBManagers(){
	  servMan = new ServiceManager( this );
	  trapMan = new TrapsManager(   this );
	  
	  try 
        {// start the sqlite database managers for traps and services
          servMan.open();
          trapMan.open();
        } // end try block
      catch ( SQLException e )
        {
    	  String title = "SQLExeption Problem";
    	  String msg = e.getMessage().toString();
    	  
    	  // if we can't start the DB managers catch the error
    	  // create an alert dialog for the error/exception
    	  showAlertBox( msg, title );
    	  
      } // end catch block
	}
	
	private void initializeRadioButtons(){
	  rbg = (RadioGroup) findViewById( R.id.rbGroup );
	}
	
	private void initializeCheckBoxes(){
	  // initialize it if trap is not gwss or acp
	  if ( this.Layout2Display != R.id.gwss || this.Layout2Display != R.id.acp )
	    cbBaited 		= (CheckBox) findViewById( R.id.cbBaited 	);
	  
	  // initialize it if trap is not gwss or acp 
	  if ( this.Layout2Display != R.id.gwss || this.Layout2Display != R.id.acp )
	    cbNewInsert = (CheckBox) findViewById( R.id.cbInsert    );
	  
	  
	  //cbNewWick		= (CheckBox) findViewById( R.id.cbNewWick 	);
	  
	  cbNewtrap 	= (CheckBox) findViewById( R.id.cbNewtrap   );
	  cbRelocated 	= (CheckBox) findViewById( R.id.cbRelocated );
	  cbRemoved 	= (CheckBox) findViewById( R.id.cbRemoved   );
	  cbRotated 	= (CheckBox) findViewById( R.id.cbRotated   );
	  cbSubmitted 	= (CheckBox) findViewById( R.id.cbSubmitted );
	  
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
		      // This code prevents the check box from being 
		      // unchecked if 'new trap' is still checked
		      if ( cbNewtrap.isChecked() )
			      checkCB( cbBaited );
		        break;
		    case R.id.cbInsert:
		      // This code prevents the check box from being 
		      // unchecked if 'new trap' is still checked
		      if ( cbNewtrap.isChecked() )
			      checkCB( cbBaited );
			    break;
		    case R.id.cbNewtrap:
		      if ( !cbNewtrap.isChecked() )
		        {
		    	  // cbNewTrap has been unchecked so it will not be submitted, but
		    	  // Only disable trap submit if it is not an insert applicable trap
		    	  // because traps with inserts submit the insert and not actual trap
		    	  if ( cbNewInsert == null )
		            {
		    	      uncheckCB( cbSubmitted );
		            }
		        }
		      else
		    	// cbNewtrap has been checked
		    	if ( cbBaited != null && !cbBaited.isChecked() )
	    		  checkCB( cbBaited );
		        
	    		if ( cbNewInsert != null && !cbNewInsert.isChecked() )
	    		  checkCB( cbNewInsert );
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
		    	  
		    	  uncheckCB( cbNewtrap );
		    	  disableCheckBox( cbNewtrap );
		    	  
		    	  if ( cbBaited != null )
		    	    {
		    		  uncheckCB( cbBaited );
		    	      disableCheckBox( cbBaited );
		    	    }
		    	  if ( cbNewInsert != null )
		    	    {
		    		  uncheckCB( cbNewInsert );
		    	      disableCheckBox( cbNewInsert );
		    	    }
		    	  
		        }
		      else
		        {
		    	  enableCheckBox( cbRotated );
		    	  enableCheckBox( cbRelocated );
		    	  enableCheckBox( cbNewtrap );
		    	  
		    	  if ( cbBaited != null )
		    	    enableCheckBox( cbBaited );
		    	  
		    	  if ( cbNewInsert != null )
			    	enableCheckBox( cbNewInsert );
		    	  
		    	  if ( R.id.rbMissing == rbg.getCheckedRadioButtonId() )
		    	    {
		    		  if ( cbNewInsert != null )
		    	        checkCB( this.cbNewtrap );
		    	      if ( cbBaited != null )
		    	        checkCB( this.cbBaited  );
		    	    }
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
		      if ( cbSubmitted.isChecked() && !cbRemoved.isChecked() )
		        {
		    	  checkCB( cbNewtrap );
		    	  
		    	  if ( cbBaited != null )
		    		checkCB( cbBaited  );
		    	  if ( cbNewInsert != null )
		    		checkCB( cbNewInsert );
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
			if ( cb != null )
			  {
				if ( !cb.isChecked() )
				  {
					cb.setChecked( true );
					cb.setTextColor( getResources().getColor( R.color.cbChecked ) );
				  }
				else
				  cb.setTextColor( getResources().getColor( R.color.cbChecked ) );
			  }
		}
		
		public void handleRBClick( View view ){
			switch ( rbg.getCheckedRadioButtonId() ){
		      case R.id.rbServiced:
		    	if ( cbBaited != null )
		    	  uncheckCB( this.cbBaited );
		    	if ( cbNewInsert != null )
		    		uncheckCB( cbNewInsert );
		    	  
		    	  enableCheckBox( this.cbNewtrap );
		    	  if ( cbBaited != null )
		    	    enableCheckBox( this.cbBaited );
		    	  
		    	  if ( cbNewInsert != null )
		    		enableCheckBox( this.cbNewInsert );
		    	  
		    	  enableCheckBox( this.cbRelocated );
		    	  enableCheckBox( this.cbRemoved );
		    	  enableCheckBox( this.cbRotated );
		    	  enableCheckBox( this.cbSubmitted );
		      break;
		      case R.id.rbMissing:
		    	if ( !cbRemoved.isChecked() )
		    	  {
		    	    enableCheckBox( this.cbNewtrap );
		    	    if ( cbBaited != null )
		    	      enableCheckBox( this.cbBaited );
		    	    if ( cbNewInsert != null )
		    	    	enableCheckBox( cbNewInsert );
		    	    enableCheckBox( this.cbRelocated );
		    	    enableCheckBox( this.cbRemoved );
		    	    enableCheckBox( this.cbRotated );
		    	  
		    	    uncheckCB( this.cbSubmitted );
		    	    disableCheckBox( this.cbSubmitted );
		    	    
		    	    if ( cbNewInsert != null )
		    	      checkCB( cbNewInsert );
		    	    checkCB( this.cbNewtrap );
		    	    if ( cbBaited != null )
		    	      checkCB( this.cbBaited  );
		    	  }
		    	else
		    	  {
		    		uncheckCB( this.cbNewtrap );
			    	disableCheckBox( this.cbNewtrap );
			    	  
			    	if ( cbBaited != null )
			    	  {
			    		uncheckCB( this.cbBaited );
			    		disableCheckBox( this.cbBaited );
			    	  }
			    	if ( cbNewInsert != null )
			    	  {
			    		uncheckCB( cbNewInsert );
			    		disableCheckBox( cbNewInsert );
			    	  }
		    	  }
		      break;
		      case R.id.rbUS:
		    	  uncheckCB( this.cbNewtrap );
		    	  disableCheckBox( this.cbNewtrap );
		    	  
		    	  if ( cbBaited != null )
		    	    {
		    		  uncheckCB( this.cbBaited );
		    		  disableCheckBox( this.cbBaited );
		    	    }
		    	  if ( cbNewInsert != null )
		    	    {
		    		  uncheckCB( cbNewInsert );
		    		  disableCheckBox( cbNewInsert );
		    	    }
		    	  
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
					    insertServID = servMan.createTrapService( activeProg, this.grid, "CRL", this.host,  this.serviceData );
					   
					    if ( insertServID > 0 )
					      adMsg = "Service Activity Added!";
					  }
					else 
					  {
						adMsg = "Service Data was not retrieved!";
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
					String title = "Trap Grid Problem";
					
					showAlertBox( alertMSG, title );
					
				  }
			}// end if grid != null
		  else
		    {
			  Toast.makeText( 
			    this, 
			    "Grid value cannot be null!, ending activity!",
			    Toast.LENGTH_SHORT
			  ).show();
			  
			  //AddServiceActivity.this.finish();
		    }
		}
		
		private void compileServiceData(){
			
		  AlertDialog.Builder dlgAlert;
		  String alertMSG = null;
			
		  this.host = etHost.getText().toString();
		  this.grid = etGrid.getText().toString() + "-" + activeProg;
			
		  if ( grid.isEmpty() )
		    {
			  alertMSG = "Grid field is empty! Please make sure to fill every field.";
				
			  dlgAlert = new AlertDialog.Builder( this );

	          dlgAlert.setMessage( alertMSG );
	          dlgAlert.setTitle( "Grid Field Problem" );
	        	
	          dlgAlert.setPositiveButton(
	        	"OK", 
	            new DialogInterface.OnClickListener(){
	              public void onClick( DialogInterface dialog, int which ){
	                // add an onFocusListener
	                AddServiceActivity.this.etGrid.setOnFocusChangeListener(
	                  // Highlight the text inside the textbox
	                  new OnFocusChangeListener(){
	                	public void onFocusChange( View v, boolean hasFocus ){
	                	  if ( hasFocus )
	                	    ((EditText)v).selectAll();
	                    }// end onfocuschange
	                  }//OnFocusChangeListener()
	                );	
	                	
	                AddServiceActivity.this.etGrid.requestFocus();
	              }
	            }
	          );//end onclicklistener
		    }
	        	
	        if ( host.isEmpty() )
		  	  {
		  	    alertMSG = "Host field is empty! Please make sure to fill every field.";
		  			
		  		dlgAlert = new AlertDialog.Builder( this );
		
		        dlgAlert.setMessage( alertMSG );
		        dlgAlert.setTitle( "Host Field Problem" );
		          	
		        dlgAlert.setPositiveButton(
		          "OK", 
		          new DialogInterface.OnClickListener(){
		          	public void onClick( DialogInterface dialog, int which ){
		              // add an onFocusListener
		              AddServiceActivity.this.etHost.setOnFocusChangeListener(
		                // Highlight the text inside the textbox
		                new OnFocusChangeListener(){
		                  public void onFocusChange( View v, boolean hasFocus ){
		                  	if ( hasFocus )
		                  	  ((EditText)v).selectAll();
		                  }
		                }
		              );	
		                  	
		              AddServiceActivity.this.etHost.requestFocus();
		            }
		          }
		        );
		        	
		        dlgAlert.setCancelable(true);
		        AlertDialog dialog = dlgAlert.create();
		        dialog.show();
			  }
	        
	        // gather service activity data
	          int tmpRBID = -1;
	          tmpRBID = rbg.getCheckedRadioButtonId();
	          
	          switch ( tmpRBID )
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
		    
		      if ( tmpRBID != R.id.rbUS )
		        {
			      if ( cbNewtrap.isChecked() )
			        {
			    	  appendServiceActivity( "NT:" );
			        }
			    
			      if ( cbBaited != null && cbBaited.isChecked() )
			        {
			    	  appendServiceActivity( "B:" );
			        }
			      
			      if ( cbNewInsert != null && cbNewInsert.isChecked() )
			        {
			    	  appendServiceActivity( "I:" );
			        }
			    
			      if ( cbRelocated.isChecked() )
			        {
			    	  appendServiceActivity( "RL:" );
			        }
			    
			      if ( cbRemoved.isChecked() )
			        {
			    	  appendServiceActivity( "RM:" );
			        }
			    
			      if ( cbRotated.isChecked() )
			        {
			    	  appendServiceActivity( "R:" );
			        }
			    
			      if ( cbSubmitted.isChecked() )
			        {
			    	  appendServiceActivity( "S:" );
			        }
		        }//END if ( rbg.getCheckedRadioButtonId() != R.id.rbUS )
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
