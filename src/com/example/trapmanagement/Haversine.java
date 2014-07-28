package com.example.trapmanagement;

public class Haversine {
	
  public static final int FEET 	 = 1;
  public static final int METERS = 2;
  public static final int YARDS  = 3;
  public static final int KM 	 = 4;
  public static final int MILES  = 5;
  
  //==   American Units
  private static final double   feetR = 20903520;
  private static final double  milesR = 3959;
  private static final double  yardsR = 6967840;
  
  //==   International Units
  private static final double metersR = 6371000; 	// In meters
  private static final double 	  kmR = 6371; 		// In kilometers
  
  
  private double earthRadius;
  private String sUnits = null;
  private int iUnits = 5;  // default to miles
  
  private double distance;
    
  public Haversine haversine( double lat1, double lon1, double lat2, double lon2, int units ){
	double c;
	
    double dLat = Math.toRadians( lat2 - lat1 );
    double dLon = Math.toRadians( lon2 - lon1 );
    
    // set units to used for earth radius
    this.setEarthRadius( units );
    
    lat1 = Math.toRadians( lat1 );
    lat2 = Math.toRadians( lat2 );
 
    double a = 
      Math.sin( dLat / 2 ) * Math.sin( dLat / 2 ) + 
      Math.sin( dLon / 2 ) * Math.sin( dLon / 2 ) * 
      Math.cos( lat1     ) * Math.cos( lat2     );
    
    c = 2 * Math.asin( Math.sqrt( a ) );
    
    this.setDistance( ( this.earthRadius * c ) );
    
    return this;
  }

  // returns the distance between the points as a double
  public double getDistance(){
	return distance;
  }
  
  // returns the distance between the points as a string
  public String getDistanceString(){
	  String sd = new String( this.distance + " " + this.sUnits );
	  return sd;
  }

  // sets the distance variable
  private void setDistance( double distance ){
	this.distance = distance;
  }

  // returns the earth's radius used
  public double getEartRadius(){
	return earthRadius;
  }
  
  // return a string specifying what units are being used in earths radius
  public String getERUnitsString(){
	  return sUnits;
  }
  
  // returns the units used in earth radius
  public int getERUnits(){
	  return iUnits;
  }
  
  // sets the earth's radius to use by the specified units
  public void setEarthRadius( int units ) {
	iUnits = units;
	
	switch ( units ){
	    case Haversine.FEET:
	  	  this.earthRadius = Haversine.feetR;
	  	  this.sUnits = "feet";
	    break;
	    
	    case Haversine.KM:
	      this.earthRadius = Haversine.kmR;
		  this.sUnits = "Kilometers";
	    break;
	    
	    case Haversine.METERS:
	      this.earthRadius = Haversine.metersR;
		  this.sUnits = "meters";
	    break;
	    
	    case Haversine.YARDS:
	      this.earthRadius = Haversine.yardsR;
		  this.sUnits = "yards";
	    break;
	    
	    case Haversine.MILES:
	      this.earthRadius = Haversine.milesR;
		  this.sUnits = "miles";
	    break;
    }
  }
 
}
