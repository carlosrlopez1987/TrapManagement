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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceTrapActivity extends Activity implements LocationListener {
	
	private LocationManager locationManager;
	private String provider;
	private TextView tvLongData;
	private TextView tvLattData;
	private double gpsLong;
	private double gpsLatt;
	
	private TrapsManager trapMan;
	
	private String grid = null;
	private String host = null;
	private String city = null;
	private String addr = null;
	private String prog = null;
	
	private EditText etGrid;
	private EditText etProg;
	private EditText etCity;
	private EditText etHost;
	private EditText etAddr;
	
	protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.place_trap);
      
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
      if ( null != location )
        onLocationChanged( location );
      else 
        {// we have no gps lock
          tvLongData.setText( "Location not available" );
          tvLattData.setText( "Location not available" );
        }
      
      try 
	    {// start the sqlite database managers for traps and services
	        trapMan.open();
	    } // end try block
	  catch ( SQLException e )
	    {// if we can't start the DB managers catch the error
	    	// create an alert dialog for the error/exception
	      AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
	
	      dlgAlert.setMessage( e.getMessage().toString() );
	      dlgAlert.setTitle( "SQLException" );
	    	
	      dlgAlert.setPositiveButton(
	    	"OK", 
	    	new DialogInterface.OnClickListener(){
	          public void onClick( DialogInterface dialog, int which ){
	            PlaceTrapActivity.this.finish();
	          }
	        }
	      );
	    	
	      dlgAlert.setCancelable(true);
	      AlertDialog dialog = dlgAlert.create();
	      dialog.show();
	    	
	      Toast.makeText( 
	  	    this, 
	  	    e.getMessage().toString(),
	  	    Toast.LENGTH_LONG
	  	  ).show();
	    	
	    } // end catch block
      
      fieldsSetup();
    }
	
	private void fieldsSetup(){
	  this.etGrid = (EditText) findViewById( R.id.etGrid );
	  this.etProg = (EditText) findViewById( R.id.etProg );
	  this.etHost = (EditText) findViewById( R.id.etHost );
	  this.etCity = (EditText) findViewById( R.id.etCity );
	  this.etAddr = (EditText) findViewById( R.id.etAddr );
	}
	
	public void commit( View view ){
		String adMsg = "";
		long insertTrapID = 0;
		
		this.prog = etProg.getText().toString();
		this.grid = etGrid.getText().toString() + "-" + this.prog;
		
		this.host = etHost.getText().toString();
		this.city = etCity.getText().toString();
		this.addr = etAddr.getText().toString();
		
		// if trap has not been placed already, place it
		if ( !trapMan.trapExistByGrid( grid ) )
		  {
			//addTrap( prog, grid, city, addr, host, insp, long, latt )
			insertTrapID = trapMan.addTrap( prog, grid, city, addr, host, "CRL", gpsLong, gpsLatt );
			if ( insertTrapID > 0 )
			  adMsg = "Trap successfully added!";
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
	}
	
	public void cancelServiceActivity( View view ){
	  finish();
	}
	
	protected void onResume(){
	  trapMan.open();
	  super.onResume();
	  locationManager.requestLocationUpdates( provider, 400, 1, this );
	}
	
	protected void onPause(){
	  trapMan.close();
	  super.onPause();
	  locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged( Location location ) {
	  // TODO Auto-generated method stub
	  this.gpsLong = location.getLatitude();
	  this.gpsLatt = location.getLongitude();
	  
	  tvLongData.setText( String.valueOf( this.gpsLong ) 	);
	  tvLattData.setText( String.valueOf( this.gpsLatt ) 	);
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
