package com.ensta.myfilmlist.service.impl;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.dao.GenreDAO;
import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.GenreDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.form.GenreForm;
import com.ensta.myfilmlist.form.RealisateurForm;
import com.ensta.myfilmlist.mapper.FilmMapper;
import com.ensta.myfilmlist.mapper.GenreMapper;
import com.ensta.myfilmlist.mapper.RealisateurMapper;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Genre;
import com.ensta.myfilmlist.model.Realisateur;
import com.ensta.myfilmlist.service.MyFilmsService;

@Service
public class MyFilmsServiceImpl implements MyFilmsService{
    private static final int NB_FILMS_MIN_REALISATEUR_CELEBRE = 3;
    @Autowired
    private FilmDAO filmDAO;
    @Autowired
    private RealisateurDAO realisateurDAO;
	@Autowired
	private GenreDAO genreDAO;
    
    private Realisateur updateReal(Realisateur realisateur) {
    	boolean celebre;
        if(filmDAO.findByRealisateurId(realisateur.getId()).size() >= NB_FILMS_MIN_REALISATEUR_CELEBRE){
            celebre = true;
        }
        else{
            celebre = false;
        }

        realisateur.setCelebre(celebre);
        
        return realisateur;
    }
    
    @Override
    public Realisateur updateRealisateurCelebre(Realisateur realisateur)
        throws ServiceException{
        if(realisateur == null){
            throw new ServiceException("realisateur null") ;
        }
        return updateReal(realisateur);
    }

    @Override
    public List<Realisateur> updateRealisateurCelebres
    	(List<Realisateur> l){
    	return l.stream()
    		.map(r->updateReal(r))
    		.filter(Objects::nonNull)
    		.filter(r->r.getCelebre())
    		.collect(Collectors.toList())
    		;
    }
    
    @Override
    public int calculerDureeTotale(List<Film> l){
    	if (l.size() == 0) { return 0; }
    	int totalTime = 
    			l.stream()
    				.map(Film::getDuree)
    				.reduce((a,b)->a+b)
    				.get();
        return totalTime;
    }

    @Override
    public double calculerNoteMoyenne(double[] d){
    	if (d.length == 0) {
    		return 0;
    	}
    	double moyenne = 
    		  Arrays.stream(d)
    				.average()
    				.getAsDouble() ;
    	moyenne = Math.round(moyenne*100);
    	moyenne /= 100;
        return moyenne;
    }
    
    @Override
    public List<FilmDTO> findAllFilms()
    	throws ServiceException{
    	List<Film> films ;
    	try {
    		films = filmDAO.findAll();
    	} catch (DataAccessException e) {
//    		System.out.println("Impossible de récupérer les films");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    	List<FilmDTO> filmsDTO;
    	filmsDTO = FilmMapper.convertFilmToFilmDTOs(films);
    	return filmsDTO;
    }
    
    @Override
    public FilmDTO findFilmById(long id) throws ServiceException{
    	Film f;
    	try {
    		f = filmDAO.findById(id).orElse(null);
    	} catch (Exception e) {
//    		System.out.println("Impossible de trouver le film");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    	if(f == null) {
    		return null;
    	}
    	return FilmMapper.convertFilmToFilmDTO(f);
    }
    
	@Override
	public List<FilmDTO> findFilmByRealisateurId(long id)
		throws ServiceException{
		List<Film> f;
		try{
			f = filmDAO.findByRealisateurId(id);
		} catch (Exception e){
//			System.out.println("Impossible de trouver la liste des films réalisés par le réalisateur");
			// e.printStackTrace();
			throw new ServiceException();
		}
		return FilmMapper.convertFilmToFilmDTOs(f);
	}
	
	@Override
	public List<FilmDTO> findFilmByGenreId(long genreId)
			throws ServiceException{
		List<Film> f;
		try{
			f = filmDAO.findByGenreId(genreId);
		} catch (Exception e){
//			System.out.println("Impossible de trouver la liste des films réalisés par le réalisateur");
			// e.printStackTrace();
			throw new ServiceException();
		}
		return FilmMapper.convertFilmToFilmDTOs(f);
	}
    
    @Override
	public FilmDTO createFilm(FilmForm f) 
    	throws ServiceException{
    	Realisateur real = realisateurDAO
    			.findById(f.getRealisateurId())
    			.orElseThrow(()->new ServiceException("Le réalisateur n'existe pas dans la base de données"));
    	
    	Genre genre = genreDAO
    			.findById(f.getGenreId())
    			.orElseThrow(()->new ServiceException("Le genre n'existe pas dans la base de données"));
    	
    	Film film = FilmMapper.convertFilmFormToFilm(f);
    	film.setRealisateur(real);
    	film.setGenre(genre);
    	
    	try {
    		film = filmDAO.save(film);
    	} catch (DataAccessException e) {
//    		System.out.println("Erreur de récupération des données");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    	
    	List<Film> films = filmDAO.findByRealisateurId(real.getId());
    	real.setFilmRealises(films);
    	real.isCelebre();
    	realisateurDAO.update(real);
    	film.setRealisateur(real);
    	
    	return FilmMapper.convertFilmToFilmDTO(film);
    }
    
	public FilmDTO updateFilm(FilmForm f, long id) 
	    	throws ServiceException{
	    	Realisateur real = realisateurDAO
	    			.findById(f.getRealisateurId())
	    			.orElseThrow(()->new ServiceException("Le réalisateur n'existe pas dans la base de données"));
	    	Genre genre = genreDAO
	    			.findById(f.getGenreId())
	    			.orElseThrow(()->new ServiceException("Le genre n'existe pas dans la base de données"));
	    	Film film = FilmMapper.convertFilmFormToFilm(f);
	    	film.setRealisateur(real);
	    	film.setGenre(genre);
	    	film.setId(id);
	    	
	    	Film returnedFilm;
	    	try {
	    		returnedFilm = filmDAO.updateFilm(film);
	    	} catch (DataAccessException e) {
//	    		System.out.println("Erreur de récupération des données");
//	    		e.printStackTrace();
	    		throw new ServiceException();
	    	}
	    	
	    	List<Film> films = filmDAO.findByRealisateurId(real.getId());
	    	real.setFilmRealises(films);
	    	real.isCelebre();
	    	realisateurDAO.update(real);
	    	film.setRealisateur(real);
	    	
	    	return FilmMapper.convertFilmToFilmDTO(returnedFilm);
	    }
	
    @Override
    public void deleteFilm(long id) throws ServiceException{
    	Film film = filmDAO.findById(id).orElse(null);
    	if (film == null) {
    		return ;
    	}
    	
    	Realisateur r = film.getRealisateur();
        try {
        	filmDAO.delete(film);
        } catch(Exception e) {
//    		System.out.println("Impossible de supprimer le film");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
        
        List<Film> films = filmDAO.findByRealisateurId(r.getId());
    	r.setFilmRealises(films);
    	r.isCelebre();
    	realisateurDAO.update(r);	
    }
    
    @Override
    public List<RealisateurDTO> findAllRealisateurs() throws ServiceException{
    	List<Realisateur> reals;
    	try {
    		reals = realisateurDAO.findAll();
    	} catch(DataAccessException e) {
//    		System.out.println("Impossible de récupérer les réalisateurs");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    	List<RealisateurDTO> realsDTO = RealisateurMapper
    			.convertRealisateurToRealisateurDTOs(reals);
    	return realsDTO;
    }
    
    @Override
    public RealisateurDTO findRealisateurByNomAndPrenom(String nom, String prenom)
    	throws ServiceException{
    	Realisateur real;
    	try {
    		real = realisateurDAO.findByNomAndPrenom(nom, prenom);
    	} catch(DataAccessException e) {
//    		System.out.println("Impossible de récupérer le réalisateur");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    	if (real == null) {
    		return null;
    	}
		return RealisateurMapper.convertRealisateurToRealisateurDTO(real);
    }
    
    @Override
    public RealisateurDTO findRealById(long id) throws ServiceException{
    	Realisateur r;
    	try {
    		r = realisateurDAO.findById(id).orElse(null);
    	} catch (Exception e) {
//    		System.out.println("Impossible de trouver le réalisateur");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    	if (r==null) {
    		return null;
    	}
    	return RealisateurMapper.convertRealisateurToRealisateurDTO(r);
    }
    
    @Override
    public RealisateurDTO createRealisateur(RealisateurForm r) 
    	throws ServiceException{
    	Realisateur real ;
    	try{
    		real = RealisateurMapper.convertRealisateurFormToRealisateur(r);
    		real = realisateurDAO.save(real);
    	}catch(Exception e) {
//    		System.out.println("Impossible de créer le réalisateur");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    	return RealisateurMapper.convertRealisateurToRealisateurDTO(real);
    }
    
    @Override
    public RealisateurDTO updateRealisateur(long id) 
    	throws ServiceException{
    	Realisateur r = realisateurDAO.findById(id).orElseThrow(()->new ServiceException());
    	r.setFilmRealises(filmDAO.findByRealisateurId(id));
    	r.isCelebre();
    	try {
    		 r = realisateurDAO.update(r);
    	}catch(Exception e) {
//    		System.out.println("Impossible de mettre à jour le réalisateur");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    	return RealisateurMapper.convertRealisateurToRealisateurDTO(r);
    }
    
	@Override
	public RealisateurDTO updateReal(RealisateurForm f, long id) 
	    	throws ServiceException{
	    	Realisateur r = RealisateurMapper.convertRealisateurFormToRealisateur(f);
	    	RealisateurDTO real = this.findRealById(id);
	    	r.setId(id);
	    	r.setCelebre(real.isCelebre());
	    	
	    	Realisateur returnedReal;
	    	try {
	    		returnedReal = realisateurDAO.update(r);
	    	} catch (DataAccessException e) {
//	    		System.out.println("Erreur de récupération des données");
//	    		e.printStackTrace();
	    		throw new ServiceException();
	    	}
	    	
	    	return RealisateurMapper.convertRealisateurToRealisateurDTO(returnedReal);	    
	   }
	
	@Override
	public void deleteReal(long id) throws ServiceException{
		try {
			List<Film> films = filmDAO.findByRealisateurId(id);
	    	if (films != null) {
	    		films.forEach(f -> filmDAO.delete(f));
	    	}
    	    
	    	Realisateur r = realisateurDAO.findById(id).orElseThrow(()->new ServiceException());
        	realisateurDAO.deleteReal(r);
        } catch(Exception e) {
//    		System.out.println("Impossible de supprimer le film");
//    		e.printStackTrace();
    		throw new ServiceException();
    	}
    }

	@Override
	public List<GenreDTO> findAllGenres() throws ServiceException{
		List<Genre> genres;
		try {
			genres = genreDAO.findAll();
		} catch (DataAccessException e) {
			System.out.println("Impossible de récupérer les genres");
			e.printStackTrace();
			throw new ServiceException();
		}
		List<GenreDTO> genresDTO = GenreMapper
				.convertGenreToGenreDTOs(genres);
		return genresDTO;
	}
	
	@Override
	public GenreDTO findGenreById(long id) throws ServiceException{
		Genre g;
		try {
			g = genreDAO.findById(id).orElse(null);
		} catch (Exception e) {
//			System.out.println("Impossible de trouver le genre");
//			e.printStackTrace();
			throw new ServiceException();
		}
		if(g==null) {
			return null;
		}
		return GenreMapper.convertGenreToGenreDTO(g);
	}

	@Override
	public GenreDTO createGenre(GenreForm g) 
		throws ServiceException{
		Genre genre;
		try {
			genre = GenreMapper.convertGenreFormToGenre(g);
			genre = genreDAO.save(genre);
		}catch(Exception e) {
			System.out.println("Impossible de créer le genre");
			e.printStackTrace();
			throw new ServiceException();
		}
		return GenreMapper.convertGenreToGenreDTO(genre);
	}
	
	@Override
	public GenreDTO updateGenre(GenreForm g, long id)
		throws ServiceException{
		Genre genre = GenreMapper.convertGenreFormToGenre(g);
		genre.setId(id);
		
		Genre returnedGenre;
		try {
			returnedGenre = genreDAO.updateGenre(genre);
		}catch(DataAccessException e) {
			System.out.println("Erreur de récupération des données");
			e.printStackTrace();
			throw new ServiceException();
		}
		return GenreMapper.convertGenreToGenreDTO(returnedGenre);
	}
	
	@Override
	public void deleteGenre(long id) throws ServiceException{
		Genre genre = genreDAO.findById(id).orElse(null);
		if (genre == null) {
			return;
		}
		
		try {
			genreDAO.delete(genre);
		}catch(Exception e) {
			System.out.println("Impossible de supprimer le genre");
			e.printStackTrace();
			throw new ServiceException();
		}
	}
}
