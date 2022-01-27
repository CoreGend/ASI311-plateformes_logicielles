package com.ensta.myfilmlist.dto;

public class GenreDTO {

	private long id;
	private String genre;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	@Override
	public String toString() {
		return "GenreDTO [id=" + id + ", genre=" + genre + "]";
	}
	
}
