package com.example.trapmanagement;

public class Trap {
	
	private long 	   trap_id;
	private String   trap_prog;
	private String 	 trap_grid;
	private String 	 trap_city;
	private String 	 trap_addr;
	private String 	 trap_host;
	private String adding_insp;
	private String  date_added;
	private String   last_serv;
	private double   trap_long;
	private double   trap_latt;
	
	// trap entry id getter and setter
	public long getTrap_id() {
		return trap_id;
	}
	public void setTrap_id( long trap_entry_id) {
		this.trap_id = trap_entry_id;
	}
	
	// trap prog getter and setter
	public String getTrap_prog() {
		return trap_prog;
	}
	public void setTrap_prog(String trap_prog) {
		this.trap_prog = trap_prog;
	}
	
	// trap grid getter and setter
	public String getTrap_grid() {
		return trap_grid;
	}
	public void setTrap_grid(String tgrid) {
		this.trap_grid = tgrid;
	}
	
	// adding insp getter and setter
	public String getAdding_insp() {
		return adding_insp;
	}
	public void setAdding_insp(String adding_insp) {
		this.adding_insp = adding_insp;
	}
	
	// date added getter and setter
	public String getDate_added() {
		return date_added;
	}
	public void setDate_added(String date_added) {
		this.date_added = date_added;
	}
	
	// last service getter and setter
	public String getLast_serv() {
		return last_serv;
	}
	public void setLast_serv(String last_serv) {
		this.last_serv = last_serv;
	}
	
	// longitude getter and setter
	public double getTrap_long() {
		return trap_long;
	}
	public void setTrap_long(double trap_long) {
		this.trap_long = trap_long;
	}
	
	// lattitude getter and setter
	public double getTrap_latt() {
		return trap_latt;
	}
	public void setTrap_latt(double trap_latt) {
		this.trap_latt = trap_latt;
	}
	public String getTrap_city() {
		return trap_city;
	}
	public void setTrap_city(String trap_city) {
		this.trap_city = trap_city;
	}
	public String getTrap_addr() {
		return trap_addr;
	}
	public void setTrap_addr(String trap_addr) {
		this.trap_addr = trap_addr;
	}
	public String getTrap_host() {
		return trap_host;
	}
	public void setTrap_host(String trap_host) {
		this.trap_host = trap_host;
	}
	
	
	
	
}
