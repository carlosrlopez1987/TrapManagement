package com.example.trapmanagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	/**
	 *************  DATABASE NAME **************************
	 */
	private static final String TRAPS_DB_NAME = "traps.db";
	
	/**
	 *************  DATABASE VERSION **************************
	 */
	private static final int DATABASE_VERSION = 6;
	
	/**========================================================
	 *************  "services" table column names *************
	 ==========================================================*/
	public static final String TABLE_SERVICES 	 	 = "services";
	public static final String COLUMN_SERV_ID   	 = "serv_id";
	public static final String COLUMN_SERV_PROGRAM   = "serv_program";
	public static final String COLUMN_TRAP_GRID		 = "trap_grid";
	public static final String COLUMN_SERV_INSP 	 = "serv_insp";
	public static final String COLUMN_SERV_HOST 	 = "serv_host";
	public static final String COLUMN_SERV_DATA 	 = "serv_data";
	public static final String COLUMN_SERV_DATE 	 = "serv_date";
	
	
	
	/**========================================================
	 *************  "traps" table column names *************
	 ==========================================================*/
	public static final String TABLE_TRAPS 			 = "traps";
	
	public static final String COLUMN_TRAP_ENTRY_ID  = "trap_id";
	public static final String COLUMN_TRAP_PROGRAM   = "trap_prog";
	public static final String COLUMN_TRAP_GRID_ID 	 = "trap_grid";
	public static final String COLUMN_TRAP_ADDR 	 = "trap_addr";
	public static final String COLUMN_TRAP_CITY 	 = "trap_city";
	public static final String COLUMN_TRAP_HOST 	 = "trap_host";
	public static final String COLUMN_ADDING_INSP    = "adding_insp";
	public static final String COLUMN_DATE_ADDED	 = "date_added";
	public static final String COLUMN_LAST_SERVICE	 = "last_serv";
	public static final String COLUMN_TRAP_LONG		 = "trap_long";
	public static final String COLUMN_TRAP_LATT		 = "trap_latt";

	/**========================================================
	 ************ "services" table creation query *************
	 ==========================================================*/
	private static final String CREATE_TB_SERVICES = "create table "
	    + TABLE_SERVICES + "(" 
			+ COLUMN_SERV_ID 		+ " integer primary key not null, "
			+ COLUMN_SERV_PROGRAM   + " text not null, " 
			+ COLUMN_TRAP_GRID 		+ " text not null, "
			+ COLUMN_SERV_INSP 		+ " text not null, "
			+ COLUMN_SERV_HOST 		+ " text not null, " 
			+ COLUMN_SERV_DATA 		+ " text not null, " 
			+ COLUMN_SERV_DATE 		+ " text not null" 
		+ ");";
	
	/**========================================================
	 ************** "traps" table creation query **************
	 ==========================================================*/
	private static final String CREATE_TB_TRAPS = "create table "
		    + TABLE_TRAPS + "(" 
				+ COLUMN_TRAP_ENTRY_ID 	+ " integer primary key not null, "
				+ COLUMN_TRAP_PROGRAM 	+ " text not null, "
				+ COLUMN_TRAP_GRID_ID 	+ " text unique not null, "
				+ COLUMN_TRAP_CITY 		+ " text not null, "
				+ COLUMN_TRAP_ADDR 		+ " text not null, "
				+ COLUMN_TRAP_HOST 		+ " text not null, "
				+ COLUMN_ADDING_INSP    + " text not null, " 
				+ COLUMN_DATE_ADDED 	+ " text not null, " 
				+ COLUMN_LAST_SERVICE 	+ " text null, "
				+ COLUMN_TRAP_LONG 		+ " double not null, "
				+ COLUMN_TRAP_LATT 		+ " double not null"
			+ ");";
	
	/**========================================================
	 ************ CREATE DATABASE FOR APPLICATION *************
	 ==========================================================*/
	public MySQLiteHelper( Context context ){
	    super( context, TRAPS_DB_NAME, null, DATABASE_VERSION);
	}

	/**========================================================
	 ************** CREATE TABLES IN NEW DATABASE *************
	 ==========================================================*/
	public void onCreate( SQLiteDatabase db ){
	    db.execSQL( CREATE_TB_SERVICES );
	    db.execSQL( CREATE_TB_TRAPS );
	}

	/**========================================================
	 ****** DELETES OLD TABLES WHEN DB VERSION UPGRADES *******
	 ==========================================================*/
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion){
		  
	    Log.w( 
	    	MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + 
	    	oldVersion  + 
	    	" to " 		+ 
	    	newVersion  + 
	    	", which will destroy all old data" 
	    );
	    
	    db.execSQL( "DROP TABLE IF EXISTS " + TABLE_SERVICES );
	    db.execSQL( "DROP TABLE IF EXISTS " + TABLE_TRAPS );
	    
	    onCreate( db );
	}

}

