package com.example.trapmanagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

/** 
 * NEED TO CREATE AN ERROR CLASS TO EXTEND FROM THIS ONE 
 * SO WE CAN GET ERROR MESSAGES FROM ACTIVITY
 * @author kleber
 */
public class TrapsManager {
  private Context context;
  private SQLiteDatabase db;
  private MySQLiteHelper dbHelper;
  private long insertId;
	
  private String[] allColumns = { 
	MySQLiteHelper.COLUMN_SERV_ID,
	MySQLiteHelper.COLUMN_TRAP_GRID,
	MySQLiteHelper.COLUMN_SERV_PROGRAM, 
	MySQLiteHelper.COLUMN_SERV_DATA, 
	MySQLiteHelper.COLUMN_SERV_DATE 
  };
  
  public TrapsManager( Context context ){
	this.context = context;
    dbHelper = new MySQLiteHelper( context );
  }

  public void open() throws SQLException {
    db = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }
  
  final SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd");
  
  public void updateLastService( String trapGrid ){
	Date date = new Date();
	ContentValues values = new ContentValues();
    values.put( MySQLiteHelper.COLUMN_LAST_SERVICE, dateFormat.format( date ).toString() );
		
    db.update( MySQLiteHelper.TABLE_TRAPS, values, "trap_grid=" + trapGrid, null );
  }
  
  /**
   * This function creates a new service entry into the database
   * @param serv_program
   * @param serv_data
   * @return
   */
  public long addTrap( 
	String prog, 
	String grid,
	String city,
	String addr,
	String host,
	String insp,
	double gpsLong, 
	double gpsLatt 
  ){
	  insertId = -1;
      ContentValues values = new ContentValues();
      Date date 			 = new Date();

      if ( 
    	null != insp && null != grid && 
    	null != prog && null != city &&
    	null != addr && null != host
      ){
    	  // trap entry id is on autoincrement
          values.put( MySQLiteHelper.COLUMN_TRAP_PROGRAM, 	prog 					 		 	 );
          values.put( MySQLiteHelper.COLUMN_TRAP_GRID_ID, 	grid							 	 );
          values.put( 	 MySQLiteHelper.COLUMN_TRAP_CITY, 	city							 	 );
          values.put( 	 MySQLiteHelper.COLUMN_TRAP_ADDR, 	addr							 	 );
          values.put( 	 MySQLiteHelper.COLUMN_TRAP_HOST, 	host							 	 );
          values.put(  MySQLiteHelper.COLUMN_ADDING_INSP, 	insp  						     	 );
          values.put( 	MySQLiteHelper.COLUMN_DATE_ADDED, 	dateFormat.format( date ).toString() );
          values.put( MySQLiteHelper.COLUMN_LAST_SERVICE, 	dateFormat.format( date ).toString() );
          values.put( 	 MySQLiteHelper.COLUMN_TRAP_LONG, 	gpsLong  						     );
          values.put( 	 MySQLiteHelper.COLUMN_TRAP_LATT, 	gpsLatt  						     );
      
          try 
            {
              // insert new service into services table and save the entry id
    	      insertId = db.insertOrThrow(
    	        MySQLiteHelper.TABLE_TRAPS,
                null, values
              );
            } 
          catch ( SQLiteException e )
            {
    	      Toast.makeText( 
  			    context, 
  			    e.getMessage().toString(),
  			    Toast.LENGTH_LONG
  		      ).show();
            }
        }  // end if

    return insertId;
  }

  public void deleteTrap( Trap trap ){
    long id = trap.getTrap_id();

    db.delete(
      MySQLiteHelper.TABLE_TRAPS,
	  MySQLiteHelper.COLUMN_TRAP_ENTRY_ID + " = " + id,
	  null
	);
  }
  
  public void deleteTrapById( String trapId ){
	db.delete(
	  MySQLiteHelper.TABLE_TRAPS,
	  MySQLiteHelper.COLUMN_TRAP_ENTRY_ID + " = " + trapId,
	  null
	);
  }
  
  
  /** this function get all traps in database */
  public List<Trap> getAllTraps(){
	  
    List<Trap> traps = new ArrayList<Trap>();
    String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_TRAPS;
	    
	Cursor cursor = db.rawQuery( selectQuery, null );
	    
      // looping through each row returned from db and adding it to list
    if ( cursor.moveToFirst() ){
      do 
        {
          Trap t = new Trap();
          
	      t.setTrap_id( 	Integer.parseInt( cursor.getString( 0 )    ) );
	      t.setTrap_prog( 	cursor.getString( 1 ) 						 );
	      t.setTrap_grid( 	cursor.getString( 2 ) 					     );
	      t.setTrap_city( 	cursor.getString( 3 ) 					     );
	      t.setTrap_addr( 	cursor.getString( 4 ) 					     );
	      t.setTrap_host( 	cursor.getString( 5 ) 					     );
	      t.setAdding_insp( cursor.getString( 6 ) 					     );
	      t.setDate_added( 	cursor.getString( 7 ) 					     );
	      t.setLast_serv( 	cursor.getString( 8 ) 					     ); 
	      t.setTrap_long( 	Double.parseDouble( cursor.getString( 9  ) ) );
	      t.setTrap_latt( 	Double.parseDouble( cursor.getString( 10 ) ) );
          
          // Adding trap service to list
          traps.add( t );
        } 
      while ( cursor.moveToNext() );
    }

    return traps;
  }
  
  /** 
   * This function get all traps in database from a certain trapping program
   * 
   **/
  public List<Trap> getAllTrapsByProg( String prog ){
	  
    List<Trap> traps = new ArrayList<Trap>();
	    
    Cursor cursor = db.rawQuery( 
      "SELECT * FROM " + 
	  MySQLiteHelper.TABLE_TRAPS + 
	  " WHERE trap_prog = ? ", 
	  new String[] {prog + ""}
	);
		    
	// looping through each row returned from db and adding it to list
	if ( cursor.getCount() > 0 && cursor.moveToFirst() ){
	  do 
	    {
	      Trap t = new Trap();

	      t.setTrap_id( 	Integer.parseInt( cursor.getString( 0 )    ) );
	      t.setTrap_prog( 	cursor.getString( 1 ) 						 );
	      t.setTrap_grid( 	cursor.getString( 2 ) 					     );
	      t.setTrap_city( 	cursor.getString( 3 ) 					     );
	      t.setTrap_addr( 	cursor.getString( 4 ) 					     );
	      t.setTrap_host( 	cursor.getString( 5 ) 					     );
	      t.setAdding_insp( cursor.getString( 6 ) 					     );
	      t.setDate_added( 	cursor.getString( 7 ) 					     );
	      t.setLast_serv( 	cursor.getString( 8 ) 					     ); 
	      t.setTrap_long( 	Double.parseDouble( cursor.getString( 9  ) ) );
	      t.setTrap_latt( 	Double.parseDouble( cursor.getString( 10 ) ) );
	          
	      // Adding trap service to list
	      traps.add( t );
	    } 
	  while ( cursor.moveToNext() );
	}
	
	return traps;
  }
  
  public Trap getTrapByGrid( String grid ){
    Cursor cursor = null;
    Trap t = null;
    try
	  {
	    cursor = db.rawQuery( 
	      "SELECT * FROM " + 
	      MySQLiteHelper.TABLE_TRAPS + 
	      " WHERE trap_grid = ? ", 
	      new String[] {grid + ""}
	    );
	
	    if ( cursor.getCount() > 0 )
	      {
	        cursor.moveToFirst();
	
	        t = new Trap();
	        
	        t.setTrap_id( 	Integer.parseInt( cursor.getString( 0 )    ) );
		    t.setTrap_prog( 	cursor.getString( 1 ) 						 );
		    t.setTrap_grid( 	cursor.getString( 2 ) 					     );
		    t.setTrap_city( 	cursor.getString( 3 ) 					     );
		    t.setTrap_addr( 	cursor.getString( 4 ) 					     );
		    t.setTrap_host( 	cursor.getString( 5 ) 					     );
		    t.setAdding_insp( cursor.getString( 6 ) 					     );
		    t.setDate_added( 	cursor.getString( 7 ) 					     );
		    t.setLast_serv( 	cursor.getString( 8 ) 					     ); 
		    t.setTrap_long( 	Double.parseDouble( cursor.getString( 9  ) ) );
		    t.setTrap_latt( 	Double.parseDouble( cursor.getString( 10 ) ) );
	      }

	      return t;
	    }
      finally 
	    {
	      cursor.close();
	    }
		
	}
	
	public Trap getTrapByID( long servId ){
		
	  Cursor cursor = null;
      Trap t = null;
      
      try 
        {
	      cursor = db.rawQuery( 
	        "SELECT * FROM " 			+ 
	        MySQLiteHelper.TABLE_TRAPS 	+ 
	        " WHERE trap_id = ? ", 
	        new String[] {servId + ""}
	      );
	
	      if ( cursor.getCount() > 0 )
	        {
		      cursor.moveToFirst();
	
	          t = new Trap();
	          
	          t.setTrap_id( 	Integer.parseInt( cursor.getString( 0 )    ) );
		      t.setTrap_prog( 	cursor.getString( 1 ) 						 );
		      t.setTrap_grid( 	cursor.getString( 2 ) 					     );
		      t.setTrap_city( 	cursor.getString( 3 ) 					     );
		      t.setTrap_addr( 	cursor.getString( 4 ) 					     );
		      t.setTrap_host( 	cursor.getString( 5 ) 					     );
		      t.setAdding_insp( cursor.getString( 6 ) 					     );
		      t.setDate_added( 	cursor.getString( 7 ) 					     );
		      t.setLast_serv( 	cursor.getString( 8 ) 					     ); 
		      t.setTrap_long( 	Double.parseDouble( cursor.getString( 9  ) ) );
		      t.setTrap_latt( 	Double.parseDouble( cursor.getString( 10 ) ) );
	        }
	
	      return t;
	            
	   }
     finally 
	   {
	     cursor.close();
	   }
	}
	
	public boolean trapExistByGrid( String grid ){

	  Cursor cursor = null;
	  boolean trapExist = false;
	    
	  try
		{
		  cursor = db.rawQuery( 
		    "SELECT * FROM " + 
		    MySQLiteHelper.TABLE_TRAPS + 
		    " WHERE trap_grid = ? ", 
		    new String[] {grid}
		  );

		  if ( cursor.getCount() > 0 )
		    {
		      trapExist = true;
		      /**
		      try
		        {
				  Toast.makeText( 
					context, 
					cursor.getString( 0 ).toString(),
					Toast.LENGTH_SHORT
				  ).show();
		        }
		      catch ( CursorIndexOutOfBoundsException e )
		        {
		    	  Toast.makeText( 
					context, 
					e.getMessage().toString(),
					Toast.LENGTH_SHORT
				  ).show();
		        }
		        */
		    }
		}
	  finally 
		{
		  if ( cursor != null || !cursor.isClosed() )
		    cursor.close();
		}
	  
	  return trapExist;
	}
	
}
