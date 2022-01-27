import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Film } from 'src/app/models/film';
import { Genre } from 'src/app/models/genre';
import { FilmService } from 'src/app/services/film.service';
import { GenreService } from 'src/app/services/genre.service';

@Component({
  selector: 'app-genre-details',
  templateUrl: './genre-details.component.html',
  styleUrls: ['./genre-details.component.css']
})
export class GenreDetailsComponent implements OnInit {
  id!: number;
  genre!:Genre;
  films!: Film[];
  exists!: boolean;
  emptyList!: boolean;
  
  constructor(
    private route: ActivatedRoute,
    private router:Router,
    private genreService: GenreService,
    private filmService: FilmService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe({
      next:(params:ParamMap)=>{
        this.id = +params.get('id')!;
        if(isNaN(this.id)){
          this.router.navigate(["not-found"]);
        }
        this.genreService.getGenreById(this.id)
          .subscribe({
            next:(genre:Genre) => this.genre = genre,
            error:(err:any)=>console.error(err),
            complete:()=>{
              console.log("Genre récupéré");
              if(this.genre===undefined){
                this.exists=false;
              }else{
                this.exists=true;
                this.filmService.getFilmByGenre(this.id)
                  .subscribe({
                    next:(films:Film[]) => this.films = films,
                    error:(err:any)=>console.log(err),
                    complete:()=>{
                      console.log("Films récupérés");
                      console.log(this.films);
                      if(this.films===undefined || this.films.length === 0){
                        this.emptyList = true;
                      }else if(this.films.length > 0){
                        this.emptyList = false;
                      }
                    }
                  })
              }
            }
          })
      }
    })
    
  }

}