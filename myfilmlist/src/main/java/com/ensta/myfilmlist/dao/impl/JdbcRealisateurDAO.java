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

import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.model.Realisateur;

@Repository
public class JdbcRealisateurDAO implements RealisateurDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate ;
	
	private static final String FIND_ALL_QUERY = "SELECT id, nom, prenom, date_naissance, celebre FROM Realisateur" ;
	private static final String FIND_BY_NOM_AND_PRENOM_QUERY = 
			"SELECT id, nom, prenom, date_naissance, celebre FROM Realisateur WHERE nom=? AND prenom=?" ;
	private static final String FIND_BY_ID_QUERY =
			"SELECT id, nom, prenom, date_naissance, celebre FROM Realisateur WHERE id=?" ;
	private static final String UPDATE_REALISATEUR_QUERY =
			"UPDATE Realisateur SET nom=?,prenom=?,date_naissance=?,celebre = ? WHERE id=?";
	private static final String CREATE_REALISATEUR_QUERY =
			"INSERT INTO Realisateur (prenom, nom, date_naissance, celebre) VALUES (?,?,?,?)" ;
//	private static final String UPDATE_REAL_QUERY =
//			"UPDATE Realisateur SET nom=?, prenom=?, date_naissance=?, celebre=? WHERE id=?";
	private static final String DELETE_QUERY =
			"DELETE FROM Realisateur WHERE id=?";

	private static final RowMapper<Realisateur> rowMapper = 
	(rs, rownum) -> {
		Realisateur real = new Realisateur();
		real.setId(rs.getLong("id"));
		real.setNom(rs.getString("nom"));
		real.setPrenom(rs.getString("prenom"));
		Date d = new Date(Timestamp.valueOf(rs.getString("date_naissance")).getTime());
		real.setDateNaissance(d.toLocalDate());
		boolean b = Boolean.parseBoolean(rs.getString("celebre"));
		real.setCelebre(b);
		return real;
	};
	
	@Override
	public List<Realisateur> findAll(){
		List<Realisateur> r = jdbcTemplate.query(FIND_ALL_QUERY, rowMapper);
		return r;
	}
	
	@Override
	public Realisateur findByNomAndPrenom(String nom, String prenom) {
		Realisateur r ;
		try { 
			r = jdbcTemplate.queryForObject(FIND_BY_NOM_AND_PRENOM_QUERY, rowMapper, nom, prenom);
		} catch(EmptyResultDataAccessException e) {
			r = null;
		}
		return r;
	}
	
	@Override
	public Optional<Realisateur> findById(long id){
		Optional<Realisateur> r;
		try { 
			Realisateur real = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, rowMapper, id);
			r = Optional.ofNullable(real);
		} catch(EmptyResultDataAccessException e) {
			r = java.util.Optional.empty();
		}
		return r;
	}

	@Override
	public Realisateur save(Realisateur r) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator creator = conn->{
			PreparedStatement statement = conn.prepareStatement(
					CREATE_REALISATEUR_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1,r.getPrenom());
			statement.setString(2, r.getNom());
			statement.setDate(3, Date.valueOf(r.getDateNaissance()));
			statement.setBoolean(4, r.getCelebre());
			return statement;
		};
		jdbcTemplate.update(creator, keyHolder);
		r.setId(keyHolder.getKey().longValue());
		return r;
	}
	
	@Override
	public Realisateur update(Realisateur realisateur) {
		jdbcTemplate.update(UPDATE_REALISATEUR_QUERY, realisateur.getNom(), realisateur.getPrenom(), realisateur.getDateNaissance(),realisateur.getCelebre(), realisateur.getId());
		return realisateur;
	}
	
	@Override
	public void deleteReal(Realisateur r) {
		jdbcTemplate.update(DELETE_QUERY, r.getId());
	}
}
