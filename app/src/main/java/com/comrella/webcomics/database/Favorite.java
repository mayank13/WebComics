package com.comrella.webcomics.database;

public class Favorite {
	
	//private variables
	int _id;
	String _url;
	
	// Empty constructor
	public Favorite(){
		
	}
	// constructor
	public Favorite(int id, String url){
		this._id = id;
		this._url = url;
	}
	
	// constructor
	public Favorite(String url){
		this._url = url;
	}
	// getting ID
	public int getID(){
		return this._id;
	}
	
	// setting id
	public void setID(int id){
		this._id = id;
	}
	
	// getting url
	public String getUrl(){
		return this._url;
	}
	
	// setting url
	public void setUrl(String url){
		this._url = url;
	}
	
}
