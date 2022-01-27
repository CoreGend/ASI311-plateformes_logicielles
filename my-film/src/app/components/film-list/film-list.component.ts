import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { Film } from 'src/app/models/film';
import { Genre } from 'src/app/models/genre';
import { Realisateur } from 'src/app/models/realisateur';
// import { getAllFilms } from 'src/app/external/functions';
import { FilmService } from 'src/app/services/film.service'
import { GenreService } from 'src/app/services/genre.service';
import { RealService } from 'src/app/services/real.service';

@Component({
  selector: 'app-film-list',
  templateUrl: './film-list.component.html',
  styleUrls: ['./film-list.component.css']
})
export class FilmListComponent implements OnInit {
  list!: Film[];
  realList!: Realisateur[];
  genreList!: Genre[];
  filmForm!:FormGroup;
  sub!: Subscription;
  currentFilm: number = -1;
  editMode: boolean = false;

  constructor(
    private filmService: FilmService,
    private realService: RealService,
    private genreService: GenreService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.sub = this.filmService.filmsSubject.subscribe({
      next:(films:Film[]) => { this.list = films }
    });
    this.filmService.getAllFilms();
    this.sub = this.realService.filmsSubject.subscribe({
      next:(reals:Realisateur[]) => { this.realList = reals }
    });
    this.realService.getAllReals();
    
    this.sub = this.genreService.genreSubject.subscribe({
      next:(genreList:Genre[]) => this.genreList = genreList
    })
    this.genreService.getAllGenres();
    this.initFilmForm();
  }

  initFilmForm(): void{
    this.filmForm = this.formBuilder.group({
      titre: ['', Validators.required],
      duree: ['', [Validators.required, Validators.min(1)]],
      realisateurId:['', [Validators.required, Validators.min(1)]],
      genreId:['', [Validators.required, Validators.min(1)]]
    });
  }

  onCreateUpdateSubmit(): void{
    console.log(this.filmForm);
    if(this.editMode){
      this.filmService.editFilm(this.currentFilm, this.filmForm);
      this.resetCurrentItem();
    } else {
      this.filmService.createFilm(this.filmForm);
      this.resetCurrentItem();
    }
  }

  updateCurrentItem(id:number): void{
    this.currentFilm = id;
    this.editMode = true;
    if(this.editMode){
      this.filmService.getFilmById(this.currentFilm).subscribe({
        next:(f: Film) => {
          this.filmForm.get('titre')?.setValue(f.titre);
          this.filmForm.get('duree')?.setValue(f.duree);
          this.filmForm.get('realisateurId')?.setValue(f.realisateur.id);
          this.filmForm.get('genreId')?.setValue(f.genre.id)
        },
        error:(err:any) => console.error(err)
      })
  }
  }

  resetCurrentItem(): void{
    this.currentFilm = -1;
    this.editMode = false;
    this.filmForm.reset();
  }

  onDeleteUpdateSubmit(): void{
    this.filmService.deleteFilm(this.currentFilm);
    this.currentFilm = -1;
  }

}
