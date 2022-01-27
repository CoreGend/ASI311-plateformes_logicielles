import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Film } from 'src/app/models/film';
import { Realisateur } from 'src/app/models/realisateur';
import { FilmService } from 'src/app/services/film.service';

@Component({
  selector: 'app-real-details',
  templateUrl: './real-details.component.html',
  styleUrls: ['./real-details.component.css']
})
export class RealDetailsComponent implements OnInit {
  id!: number;
  films!: Film[];
  realisateur!: Realisateur;
  exists!: boolean;
  emptyList!:boolean;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private filmService: FilmService) 
  { }

  ngOnInit(): void {
    this.route.paramMap.subscribe({
      next:(params:ParamMap) => {
        this.id = +params.get('id')!;
        if (isNaN(this.id)){
          this.router.navigate(["not-found"]);
        }

        this.filmService.getRealisateurById(this.id)
          .subscribe({
            next: (real: Realisateur) => this.realisateur = real,
            error: (err:any) => console.error(err),
            complete: () => {
              console.log("Realisateur récupéré"); 
              if(this.realisateur === undefined){
                this.exists = false;
              } else {
                this.exists = true;
              }

              this.filmService.getFilmByRealisateur(this.realisateur.id)
                .subscribe({
                  next:
                    (films: Film[]) => this.realisateur.filmRealises = films,
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
