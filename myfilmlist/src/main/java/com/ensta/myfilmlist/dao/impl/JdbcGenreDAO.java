package com.ensta.myfilmlist.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
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

import com.ensta.myfilmlist.dao.GenreDAO;
import com.ensta.myfilmlist.model.Genre;

@Repository
public class JdbcGenreDAO implements GenreDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final String FIND_ALL_QUERY = "SELECT id, nom FROM genre";
	private static final String CREATE_GENRE_QUERY = "INSERT INTO Genre(nom) VALUES (?)";
	private static final String FIND_BY_ID_QUERY = "SELECT id, nom FROM genre WHERE id = ?";
	private static final String DELETE_QUERY = "DELETE FROM genre WHERE id=?";
	private static final String UPDATE_GENRE_QUERY = "UPDATE genre SET nom=? WHERE id=?";
	
	private final RowMapper<Genre> rowMapper =
			(rs, rownum)->{
				Genre newGenre = new Genre();
				newGenre.setId(rs.getInt("id"));
				newGenre.setGenre(rs.getString("nom"));
				return newGenre;
			};
	
	@Override
	public List<Genre> findAll(){
		return jdbcTemplate.query(FIND_ALL_QUERY, rowMapper);
	}
	
	@Override
	public Optional<Genre> findById(long id){
		Optional<Genre> g;
		try {
			g= Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, rowMapper, id));
		} catch (EmptyResultDataAccessException e) {
			g = Optional.empty();
		}
		return g;
	}
	
	@Override
	public Genre save(Genre g) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator creator = conn->{
			PreparedStatement statement = conn.prepareStatement(
					CREATE_GENRE_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, g.getGenre());
			return statement;
		};
		jdbcTemplate.update(creator, keyHolder);
		g.setId(keyHolder.getKey().longValue());
		return g;
	}
	
	@Override
	public Genre updateGenre(Genre g) {
		jdbcTemplate.update(UPDATE_GENRE_QUERY, g.getGenre(), g.getId());
		return g;
	}
	
	@Override
	public void delete(Genre genre) {
		jdbcTemplate.update(DELETE_QUERY, genre.getId());
	}
}