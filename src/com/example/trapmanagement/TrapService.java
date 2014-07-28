package com.example.trapmanagement;

public class TrapService {
	private long serv_id;
	private String serv_prog;
	private String trap_grid;
	private String serv_insp;
	private String serv_host;
	private String serv_data;
	private String serv_date;
	
	public long get_servId(){
		return serv_id;
	}

	public void set_servId( long serv_id ){
		this.serv_id = serv_id;
	}

	public String get_servData(){
		return this.serv_data;
	}

	public void set_servData( String servData ){
		this.serv_data = servData;
	}

  	public String toString(){
		return this.serv_data;
  	}

	public String get_servProg() {
		return serv_prog;
	}
	
	public void set_servProg(String serv_prog) {
		this.serv_prog = serv_prog;
	}

	public String get_servDate(){
		return this.serv_date;
	}

	public void set_servDate( String serv_date) {
		this.serv_date = serv_date;
	}

	public String get_trapGrid() {
		return trap_grid;
	}

	public void set_trapGrid(String trap_grid) {
		this.trap_grid = trap_grid;
	}

	public String get_servHost() {
		return serv_host;
	}

	public void set_servHost(String serv_host) {
		this.serv_host = serv_host;
	}

	public String get_servInsp() {
		return serv_insp;
	}

	public void set_servInsp(String serv_insp) {
		this.serv_insp = serv_insp;
	}
}

