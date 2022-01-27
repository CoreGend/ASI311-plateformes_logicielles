import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { Genre } from '../models/genre';

@Injectable({
  providedIn: 'root'
})
export class GenreService {
  urlGenre: string='http://localhost:8080/genre'

  genres!: Genre[];
  genreSubject: Subject<Genre[]> = new Subject();
  
  constructor(
    private http:HttpClient
  ) { }

  public emitGenres(): void{
    this.genreSubject.next(this.genres);
  }

  public getGenreById(id:number): Observable<Genre>{
    return this.http.get<Genre>(this.urlGenre+'/'+id);
  }

  public getAllGenres(): void{
    this.http.get<Genre[]>(this.urlGenre).subscribe({
      next:(value:Genre[]) => this.genres=value,
      error:(err:any)=>console.error(err),
      complete:()=>this.emitGenres()
    });
  }

  public createGenre(f:FormGroup): void{
    this.http.post<Genre>(this.urlGenre, f.getRawValue()).subscribe({
      next: (value:Genre)=>this.genres.push(value),
      error:(err:any)=>console.error(err),
      complete:()=>console.log("Genre ajouté à la liste")
    });
    this.emitGenres();
  }

  public deleteGenre(id:number): void{
    this.http.get<void>(this.urlGenre+'/d/'+id)
      .subscribe({
        next:()=>this.genres.splice(this.genres.findIndex((g)=>g.id===id), 1),
        error:(err:any)=>console.error(err),
        complete:()=>console.log("Genre supprimé avec succès")
      });
      this.emitGenres();
  }

  public editGenre(id: number, f: FormGroup): void{
    this.http.post<Genre>(this.urlGenre+'/'+id, f.getRawValue()).subscribe({
      next:(value:Genre)=>this.genres.splice(this.genres.findIndex((g)=>g.id===id),1,value),
      error:(err:any)=>console.error(err),
      complete:()=>console.log("Genre modifié avec succès")
    });
    this.emitGenres();
  }
}