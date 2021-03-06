package com.example.trapmanagement;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TrapStatsActivity extends Activity {
	
	public static final int EGVM = 1;
	public static final int LBAM = 2;
	public static final int GWSS = 3;
	public static final int ACP = 4;
	public static final int JB = 5;
	public static final int GM = 6;
	
	private ServiceManager servMan;
	private TrapsManager trapsMan;
	
	private List< List<TrapService> > 	   groupedServices;
	private List< List<Trap> > 		  	   groupedTraps;
	private List< Pair<String, Integer> > dataTotals;
	
	private LinearLayout statsLO;			// main linear layout
	private LinearLayout scrLO;		// scroll linear layout
	
	private List<Trap> traps;
	private List<TrapService> services;
	
	private List<String> allTrapPrograms;
	private List<String> allServicePrograms;
	
	
	private TextView tvTProgData;
	
	private LinearLayout wrapperLO;
	
	private LinearLayout headerLO;
	private TextView 	 tvHeader;
	
	private LinearLayout 	tTrapsLO;
	private TextView   tvTTrapsLabel;
	private TextView    tvTTrapsData;
	private Button 		  bViewTraps;
	
	private LinearLayout  tServicesLO;
	private TextView tvTServicesLabel;
	private TextView tvTServicessData;
	private Button 		bViewServices;
	
	private LinearLayout materialsLO;
	private Button 	  bViewMaterials;
	
	protected void onCreate( Bundle savedInstanceState ){
		/**
		 * GET ALL TRAPS AND INFO FROM TRAPS TABLE
		 * GET ALL SERVICES FROM SERVICES TABLE
		 * 
		 * SEPPARATE ALL TRAPS BY PROGRAM AND SORT THEM BY DATE AND ID
		 * SEPARATE ALL SERVICES BY PROGRAM AND SORT THEM BY DATE AND ID
		 * 
		 * PARSE ALL SERVICE DATA BY PROGRAM AND COMPILE MATERIALS USED FROM IT
		 * ADD MATERIALS USED FROM PLACED TRAPS
		 * 
		 * CREATE REPORT
		 * SHOW REPORT
		 */
		
		super.onCreate(savedInstanceState);
        setContentView( R.layout.stats );
        
        // Main layout
        statsLO  = (LinearLayout) this.findViewById( R.id.statsLayout );
        scrLO = (LinearLayout) this.findViewById( R.id.scroll_LO   );
		
		//to save all different programs
		allTrapPrograms = new ArrayList<String>();
		allServicePrograms = new ArrayList<String>();
		
		groupedServices = new ArrayList< List<TrapService> >();
    	groupedTraps 	= new ArrayList< List<Trap> >();
    	
    	// initiate services & traps groups
    	groupedTraps.add( 	 new ArrayList<Trap>() 		  );
    	groupedServices.add( new ArrayList<TrapService>() );
    	
    	//========================================================
    	//STATS SECTION FOR DYNAMIC STATS INFO
    	//========================================================
    	/*
		wrapperLO = new LinearLayout( this );
    	
    	headerLO = new LinearLayout( this );
    	tvHeader = new TextView( 	  this );
    	
    	tTrapsLO 	  = new LinearLayout( this );
    	tvTTrapsLabel = new TextView( 	 this );
    	tvTTrapsData  = new TextView( 	 this );
    	bViewTraps 	  = new Button(   	 this );
    	
    	tServicesLO 	 = new LinearLayout( this );
    	tvTServicesLabel = new TextView( 	  this );
    	tvTServicessData = new TextView( 	  this );
    	bViewServices 	 = new Button(   	  this );
    	
    	materialsLO    = new LinearLayout( this );
    	bViewMaterials = new Button(   	 this );
    	*/
    	// END SECTION FOR DYNAMIC STATS
        
        trapsMan = new TrapsManager(   this );
        servMan  = new ServiceManager( this );
        
        trapsMan.open();
        servMan.open();
        
        
        // retrieve all traps and services from respective tables
        services = servMan.getAllServices();
        traps 	 = trapsMan.getAllTraps();
        
        // do I have any Services?
        // Note: do not use size method because it will have to count 
        // all the items in list and might take a while
        if ( !services.isEmpty() )
          {
        	try 
        	  {
        		groupServices( services );
        	  }
        	catch ( NullPointerException e )
        	  {
        		Toast.makeText( 
		  		  this, 
		  		  "Null pointer in services list!",
		  		  Toast.LENGTH_SHORT
		  	    ).show();
        	  }
          }
        
        // do I have any traps?
        // Note: do not use size method because it will have to count 
        // all the items in list and might take a while, when list is long
        if ( !traps.isEmpty() )
          {
        	// lets group all the traps by prog
        	groupTraps( traps );
          }
        
        if ( null != tvTProgData )
          tvTProgData.setText( " " + allTrapPrograms.size() + " Programs" );
        
        
        // create trap stats blocks
        int i = 0;
        for (; i < allTrapPrograms.size(); i++ )
          {
        	tvHeader = new TextView( this );
        	
        	tvHeader.setText( 
        	  allTrapPrograms.get( i ).toString() + 
        	  " Trap Totals: " +
        	  groupedTraps.get(i).size()
        	);
        	
        	LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams( 
        	  LayoutParams.WRAP_CONTENT, 
        	  LayoutParams.WRAP_CONTENT 
        	);
            llp.setMargins( 20, 0, 0, 1 );
        	tvHeader.setTypeface( Typeface.MONOSPACE, Typeface.BOLD );
        	tvHeader.setTextSize( TypedValue.COMPLEX_UNIT_SP, 20 );
        	tvHeader.setBackgroundColor( getResources().getColor( R.color.medTurq ) );
        	tvHeader.setGravity( Gravity.CENTER_HORIZONTAL );
        	
        	
        	tvHeader.setLayoutParams( llp );
        	
        	statsLO.addView( tvHeader );
          }
        
        i = 0;
        for (; i < allServicePrograms.size(); i++ )
          {
        	//================================
        	tvHeader = new TextView( this );
        	
        	tvHeader.setText( 
        	  allServicePrograms.get( i ).toString() + 
        	  " Service Totals: " +
        	  groupedServices.get(i).size()
        	);
        	
        	tvHeader.setLayoutParams( 
        	  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
              )
        	);
        	
        	statsLO.addView( tvHeader );
          }// end for loop
	}
	
	// puts all services together
	private void groupServices( List<TrapService> serv ) throws NullPointerException
	{
	  if ( serv != null )
	  {
	    for ( TrapService s : serv )
	  	  {
	  	    String prog = new String( s.get_servProg() );
	  	    
	  	    // to temporarily save the index location of the group i am 
	  	    // currently working on
	  	    int currentGIndex = -1;
	  		
	  	    // do I have the program registered already?
	  	    if ( !allServicePrograms.contains( prog )  )
	  	      {
	  		    // no, so register it
	  		    allServicePrograms.add( prog );
	  		    
  		        // there should only be one instance of the prog name in this list/group
  		        // so let me search for its index location and save it, I will need it
  		        currentGIndex = allServicePrograms.lastIndexOf( prog );
  		    
  		        // since I didn't have that program registered, it also means I haven't
  		        // started a group/list for the services belonging to that program, so 
  		        // create a new group/list for the newly registered program.
  		        try 
  		          {
  		    	    // create new group/list
  		    	    groupedServices.add( new ArrayList<TrapService>() );
  		    	
  		    	    // add service to its belonging group
  		    	    addService2Group( currentGIndex, s );
  		          }
  		        catch ( UnsupportedOperationException e )
  		          {
  		    	    Toast.makeText( 
	        		  this, 
	        		  e.getMessage().toString(),
	        		  Toast.LENGTH_LONG
	        	    ).show();
  		          }
	  		    
	  		  }// end if do I have prog registered?
	  		else
	  		  {
	  			// I do have that program registered so let me find its index
	  			currentGIndex = allServicePrograms.lastIndexOf( prog );
	  			
	  			// trap to its belonging group
	  			addService2Group( currentGIndex, s );
	  		  }
	  	  }// end for loop
	  }// end if
	}// end groupServices
	
	private void groupTraps( List<Trap> traps ){
	  for ( Trap t : traps )
  	    {
  		  String prog = new String( t.getTrap_prog() );
  		
  		  // to temporarily save the index location of the group i am 
  		  // currently working on
  		  int currentGIndex = -1;
  		
  		  // do I have the program registered already?
  		  if ( !allTrapPrograms.contains( prog ) )
  		    {
  			  // no, so register it
  		      allTrapPrograms.add( prog );
  		    
  		      // there should only be one instance of the prog name in this list/group
  		      // so let me search for its index location and save it, I will need it
  		      currentGIndex = allTrapPrograms.lastIndexOf( prog );
  		    
  		      // since I didn't have that program registered, it also means I haven't
  		      // started a group/list for the traps belonging to that program, so 
  		      // create a new group/list for the newly registered program.
  		      try 
  		        {
  		    	  // create new group/list
  		    	  groupedTraps.add( new ArrayList<Trap>() );
  		    	
  		    	  // trap to its belonging group
      			  addTrap2Group( currentGIndex, t );
  		        }
  		      catch ( UnsupportedOperationException e )
  		        {
  		    	  Toast.makeText( 
	        		this, 
	        		e.getMessage().toString(),
	        		Toast.LENGTH_LONG
	        	  ).show();
  		        }
  		    
  		    }// end if do I have prog registered?
  		  else
  		    {
  			  // I do have that program registered so let me find its index
  			  currentGIndex = allTrapPrograms.lastIndexOf( prog );
  			
  			  // trap to its belonging group
  			  addTrap2Group( currentGIndex, t );
  		    }
  	    }// end for loop
	}// end groupTraps
	
	
	/**
	 * Add a trap to a group at specified location
	 * 
	 * @param index integer
	 * @param t Trap object
	 */
	
	private void addTrap2Group( int index, Trap t )
	{
	  if ( index > -1 )
  	    {
  		  // add trap to its group which is located at index
  		  // but let me make sure I am not out of bounds, so catch the 
  		  // exception if it happens
  		  try
  		    {
  			  try
  			    {
  			      groupedTraps.get( index ).add( t );
  		    	}
  			  catch ( UnsupportedOperationException e )
			    {
			      Toast.makeText( 
		      		this, 
		      		e.getMessage().toString(),
		      		Toast.LENGTH_LONG
		      	  ).show();
			    }
  		    }
  		  catch ( IndexOutOfBoundsException e )
  		    {
  			  Toast.makeText( 
      		    this, 
      		    e.getMessage().toString(),
      		    Toast.LENGTH_LONG
      		  ).show();
  		    }
  	    }  // end if
	}
	
	private void addService2Group( int index, TrapService s )
	{
	  if ( index > -1 )
  	    {
  		  // add trap to its group which is located at index
  		  // but let me make sure I am not out of bounds, so catch the 
  		  // exception if it happens
  		  try
  		    {
  			  try
  			    {
  			      groupedServices.get( index ).add( s );
  		    	}
  			  catch ( UnsupportedOperationException e )
			    {
			      Toast.makeText( 
		      		this, 
		      		e.getMessage().toString(),
		      		Toast.LENGTH_LONG
		      	  ).show();
			    }
  		    }
  		  catch ( IndexOutOfBoundsException e )
  		    {
  			  Toast.makeText( 
      		    this, 
      		    e.getMessage().toString(),
      		    Toast.LENGTH_LONG
      		  ).show();
  		    }
  	    }  // end if
	}
	
	protected void onResume() {
      servMan.open();
      trapsMan.open();
      super.onResume();
    }

    @Override
    protected void onPause() {
      servMan.close();
      trapsMan.close();
      super.onPause();
    }
}

