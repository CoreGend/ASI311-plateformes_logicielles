import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Film } from 'src/app/models/film';
import { Genre } from 'src/app/models/genre';
import { Realisateur } from 'src/app/models/realisateur';
import { FilmService } from 'src/app/services/film.service';

@Component({
  selector: 'app-film-details',
  templateUrl: './film-details.component.html',
  styleUrls: ['./film-details.component.css']
})
export class FilmDetailsComponent implements OnInit {
  id!: number;
  film!: Film;
  realisateur!: Realisateur;
  genre!: Genre;
  exists!: boolean;
  emptyList!:boolean;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private filmService: FilmService
  ) { 
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe({
      next:(params:ParamMap) => {
        this.id = +params.get('id')!;
        if (isNaN(this.id)){
          this.router.navigate(["not-found"]);
        }

        this.filmService.getFilmById(this.id)
          .subscribe({
            next: (film: Film) => this.film = film,
            error: (err:any) => console.error(err),
            complete: () => {
              console.log("Film récupéré"); 
              if(this.film === undefined){
                this.exists = false;
              } else {
                this.exists = true;
              }
              this.realisateur = this.film.realisateur!;
              this.genre = this.film.genre!;

              this.filmService.getFilmByRealisateur(this.realisateur.id)
                .subscribe({
                  next:
                    (films: Film[]) => this.realisateur.filmRealises = films.filter(f => f.id != this.film.id),
                  error: (err:any) => console.error(err),
                  complete:() => {
                    if(this.realisateur.filmRealises.length > 0) {
                      this.emptyList = false;
                    } else {
                      this.emptyList = true;
                    }
                  }
                })
            }
          })
      }
    });
  }
}
