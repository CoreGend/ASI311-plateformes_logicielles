package com.ensta.myfilmlist.dao.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Genre;
import com.ensta.myfilmlist.model.Realisateur;

@Repository
public class JdbcFilmDAO implements FilmDAO{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final String FIND_ALL_JOIN_QUERY = 
			"SELECT Film.id, titre, duree, realisateur_id, genre_id, realisateur.nom as nom, prenom, date_naissance, celebre, genre.nom as nom_genre FROM Film, Realisateur, Genre WHERE Realisateur.id=realisateur_id AND Genre.id = Genre_id";
	private static final String CREATE_FILM_QUERY = 
			"INSERT INTO Film (titre, duree,  realisateur_id, genre_id) VALUES (?,?,?,?)";
	private static final String FIND_BY_ID_QUERY =
			"SELECT Film.id, titre, duree, realisateur_id, genre_id, realisateur.nom as nom, prenom, date_naissance, celebre, genre.nom as nom_genre FROM Film, Realisateur, Genre WHERE Realisateur.id=realisateur_id AND Genre.id=genre_id AND Film.id=?" ;
	private static final String DELETE_QUERY =
			"DELETE FROM Film WHERE id=?";
	private static final String FIND_BY_REALISATEUR_ID =
			"SELECT Film.id, titre, duree, realisateur_id, realisateur.nom as nom, prenom, date_naissance, celebre, genre_id, genre.nom as nom_genre FROM Film, Realisateur, Genre WHERE Realisateur.id=realisateur_id AND genre_id=genre.id AND realisateur_id=?";
	private static final String UPDATE_FILM_QUERY =
			"UPDATE Film SET titre=?, duree=?, realisateur_id=?, genre_id=? WHERE id=?";
	private static final String FIND_BY_GENRE_ID = 
			"SELECT Film.id, titre, duree, realisateur_id, realisateur.nom as nom, prenom, date_naissance, celebre, genre_id, genre.nom as nom_genre FROM Film, Realisateur, Genre WHERE Realisateur.id=realisateur_id AND genre_id=genre.id AND genre_id=?";
	
	private final RowMapper<Film> rowMapper = 
			(rs,rownum)->{
				Film newFilm = new Film();
				newFilm.setDuree(rs.getInt("duree"));
				newFilm.setTitre(rs.getString("titre"));
				newFilm.setId(rs.getInt("id"));
				
				Realisateur real = new Realisateur();
				real.setId(rs.getLong("realisateur_id"));
				real.setNom(rs.getString("nom"));
				real.setPrenom(rs.getString("prenom"));
				Date d = new Date(Timestamp.valueOf(rs.getString("date_naissance")).getTime());
				real.setDateNaissance(d.toLocalDate());
				real.setCelebre(Boolean.parseBoolean(rs.getString("celebre")));
				
				Genre genre = new Genre();
				genre.setId(rs.getLong("genre_id"));
				genre.setGenre(rs.getString("nom_genre"));
				
				newFilm.setRealisateur(real);
				newFilm.setGenre(genre);
				return newFilm;
			};

	@Override
	public List<Film> findAll(){		
		return jdbcTemplate.query(FIND_ALL_JOIN_QUERY, rowMapper);
	}
	
	@Override
	public Optional<Film> findById(long id){
		Optional<Film> f;
		try {
			f = Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, rowMapper, id));
		} catch(EmptyResultDataAccessException e) {
			f = Optional.empty();
		}
		return f;
	}
	
	@Override
	public List<Film> findByRealisateurId(long realisateurId){
		List<Film> f = jdbcTemplate.query(FIND_BY_REALISATEUR_ID, rowMapper, realisateurId) ;
		return f;
	}
	
	@Override
	public List<Film> findByGenreId(long genreId){
		List<Film> f = jdbcTemplate.query(FIND_BY_GENRE_ID, rowMapper, genreId) ;
		return f;
	}	
	
	@Override
	public Film save(Film f) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator creator = conn ->{
			PreparedStatement statement = conn.prepareStatement(
					CREATE_FILM_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, f.getTitre());
			statement.setInt(2, f.getDuree());
			statement.setLong(3, f.getRealisateur().getId());
			statement.setLong(4, f.getGenre().getId());
			return statement;
		} ;
		jdbcTemplate.update(creator, keyHolder);
		f.setId(keyHolder.getKey().longValue());
		return f;
	}
	
	@Override
	public Film updateFilm(Film f) {
		jdbcTemplate.update(UPDATE_FILM_QUERY, f.getTitre(), f.getDuree(), f.getRealisateur().getId(), f.getGenre().getId(), f.getId());
		return f;
	}
	@Override
	public void delete(Film film) {
		jdbcTemplate.update(DELETE_QUERY, film.getId());
	}
}
