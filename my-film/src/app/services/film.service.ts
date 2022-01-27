import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Film } from '../models/film';
import { Realisateur } from '../models/realisateur';
import { FormGroup } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class FilmService {
  urlFilm: string = 'http://localhost:8080/film';
  urlReal: string = 'http://localhost:8080/realisateur';

  films!:Film[];
  filmsSubject:Subject<Film[]> = new Subject();

  constructor(
    private http: HttpClient
  ) { 
  }

  public emitFilms(): void{
    this.filmsSubject.next(this.films);
  }

  public getFilmById(id: number): Observable<Film>{
    return this.http.get<Film>(this.urlFilm+'/'+id);
  }

  public getAllFilms(): void{
    this.http.get<Film[]>(this.urlFilm).subscribe({
      next:(value:Film[])=>this.films=value,
      error:(err:any)=>console.error(err),
      complete:()=>this.emitFilms()
    });
  }

  public getFilmByRealisateur(id: number): Observable<Film[]>{
    return this.http.get<Film[]>(this.urlFilm+'/r/'+id);
  }

  public getRealisateurById(id: number): Observable<Realisateur>{
    console.log('url='+this.urlReal+'/'+id)
    return this.http.get<Realisateur>(this.urlReal+'/'+id);
  }

  public createFilm(f:FormGroup): void{
    this.http.post<Film>(this.urlFilm, f.getRawValue()).subscribe(
      {
        next: (value: Film) => { this.films.push(value) },
        error:(err:any) => console.error(err),
        complete: () => console.log("Film ajouté à la liste")
      }
    );
    this.emitFilms();
  }

  public deleteFilm(id: number): void{
    this.http.get<void>(this.urlFilm+'/d/'+id)
      .subscribe({
        next: () => this.films.splice(this.films.findIndex((f) => f.id === id),1),
        error:(err:any) => console.error(err),
        complete: () => console.log("Film supprimé avec succès")
      });
      this.emitFilms();
  }

  public editFilm(id: number, f: FormGroup): void{
    this.http.post<Film>(this.urlFilm+'/'+id, f.getRawValue()).subscribe(
      {
        next: (value:Film) =>
          this.films.splice(this.films.findIndex((f) => f.id === id), 1, value),
        error: (err:any) => console.error(err),
        complete: () => console.log("Film modifié avec succès")
      }
    );
    this.emitFilms();
  }

  public getFilmByGenre(id: number): Observable<Film[]>{
    return this.http.get<Film[]>(this.urlFilm+'/g/'+id);
  }
}

// public getFilmByRealisateur(id: number): Observable<Film[]>{
//   return this.http.get<Film[]>(this.urlFilm+'/r/'+id);
// }
