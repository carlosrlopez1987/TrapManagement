package com.example.trapmanagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class ServiceManager {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Context context;
	
	private String[] allColumns = { 
		MySQLiteHelper.COLUMN_SERV_ID,
		MySQLiteHelper.COLUMN_SERV_PROGRAM,
		MySQLiteHelper.COLUMN_TRAP_GRID,
		MySQLiteHelper.COLUMN_SERV_INSP,
		MySQLiteHelper.COLUMN_SERV_HOST,
	    MySQLiteHelper.COLUMN_SERV_DATA, 
	    MySQLiteHelper.COLUMN_SERV_DATE 
	};

	public ServiceManager( Context context ){
		this.context = context;
	    dbHelper = new MySQLiteHelper( context );
	}

	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}

	public void close() {
	    dbHelper.close();
	}
	  
	final SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd");
	  
	  
	  
	  /**
	   * This function creates a new service entry into the database
	   * @param serv_program
	   * @param serv_data
	   * @return
	   */
	public long createTrapService( String prog, String grid, String insp, String host, String serv_data ){
		
		String 			data = null;
		long 		insertId = -1;
		ContentValues values = new ContentValues();
	    Date 			date = new Date();
		
		// remove the last ':' from the string
		if ( serv_data.length() > 0 && serv_data.charAt( serv_data.length()-1 ) == ':') {
		  data = serv_data.substring( 0, serv_data.length()-1 );
		}  
		
	    values.put( MySQLiteHelper.COLUMN_SERV_PROGRAM, prog 						 );
	    values.put( MySQLiteHelper.COLUMN_TRAP_GRID, 	grid 							 	 );
	    values.put( MySQLiteHelper.COLUMN_SERV_INSP, 	insp 							 	 );
	    values.put( MySQLiteHelper.COLUMN_SERV_HOST, 	host 							 	 );
	    values.put( MySQLiteHelper.COLUMN_SERV_DATA, 	data 							 	 );
	    values.put( MySQLiteHelper.COLUMN_SERV_DATE, 	dateFormat.format( date ).toString() );
	    
	    try 
	      {
	        // insert new service into services table and save the entry id
	    	insertId = database.insertOrThrow( 
	    	  MySQLiteHelper.TABLE_SERVICES, 
	    	  null,
	          values
	        );
	      }
	    catch ( SQLException e )
	      {
	    	Toast.makeText( 
	    	  context, 
	    	  e.getMessage().toString(),
	    	  Toast.LENGTH_LONG
	    	).show();
	      }
	    
	    return insertId;
	}

	public void deleteService( TrapService service ){
		
	  long id = service.get_servId();
	    
	  database.delete(
	    MySQLiteHelper.TABLE_SERVICES, 
	    MySQLiteHelper.COLUMN_SERV_ID
	    + " = " + id, null 
	  );
	}
      
	public List<TrapService> getAllServices(){
		Cursor cursor = null;
	    List<TrapService> services = null;
	    String 		   selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_SERVICES;
	    
	    cursor = database.rawQuery( selectQuery, null );
	    
        // looping through each row returned from db and adding it to list
        if ( cursor.moveToFirst() ){
          services =  new ArrayList<TrapService>();
        	
          do 
            {
              TrapService ts = new TrapService();
              
              ts.set_servId( 	Integer.parseInt( cursor.getString( 0 ) ) );
              ts.set_servProg( 	cursor.getString( 1 ) 					  );
              ts.set_trapGrid( 	cursor.getString( 2 ) 					  );
              ts.set_servInsp( 	cursor.getString( 3 ) 					  );
              ts.set_servHost( 	cursor.getString( 4 ) 					  );
              ts.set_servData( 	cursor.getString( 5 ) 					  );
              ts.set_servDate( 	cursor.getString( 6 ) 					  );
              
              // Adding trap service to list
              services.add( ts );
            } 
          while ( cursor.moveToNext() );
        }
 
        // return services list
        return services;
	}
	/*
	private TrapService cursorToTrapService( Cursor cursor ){
		  
	    TrapService trap_service = new TrapService();
	    trap_service.set_servId(   cursor.getLong(   0 ) );
	    trap_service.set_servData( cursor.getString( 1 ) );
	    
	    return trap_service;
	}
	*/
}

