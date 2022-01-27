import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { Film } from '../models/film';
import { Realisateur } from '../models/realisateur';

@Injectable({
  providedIn: 'root'
})
export class RealService {
  urlFilm: string = 'http://localhost:8080/film';
  urlReal: string = 'http://localhost:8080/realisateur';

  reals!:Realisateur[];
  filmsSubject:Subject<Realisateur[]> = new Subject();
  
  constructor(
    private http: HttpClient
  ) { }

  public emitReals(): void{
    this.filmsSubject.next(this.reals);
  }

  public getRealisateurs(): Observable<Realisateur[]>{
    return this.http.get<Realisateur[]>(this.urlReal)
  }

  public getFilmByRealisateur(id: number): Observable<Film[]>{
    return this.http.get<Film[]>(this.urlFilm+'/r/'+id);
  }

  public getRealById(id: number): Observable<Realisateur>{
    return this.http.get<Realisateur>(this.urlReal+'/'+id);
  }

  public getAllReals(): void{
    this.http.get<Realisateur[]>(this.urlReal).subscribe({
      next:(value:Realisateur[])=>this.reals=value,
      error:(err:any)=>console.error(err),
      complete:()=>this.emitReals()
    });
  }

  public createReal(f:FormGroup): void{
    this.http.post<Realisateur>(this.urlReal, f.getRawValue()).subscribe(
      {
        next: (value: Realisateur) => { this.reals.push(value) },
        error:(err:any) => console.error(err),
        complete: () => console.log("Realisateur ajouté à la liste")
      }
    );
    this.emitReals();
  }

  public editReal(id: number, f: FormGroup): void{
    this.http.post<Realisateur>(this.urlReal+'/'+id, f.getRawValue()).subscribe(
      {
        next: (value:Realisateur) =>
          this.reals.splice(this.reals.findIndex((r) => r.id === id), 1, value),
        error: (err:any) => console.error(err),
        complete: () => console.log("Realisateur modifié avec succès")
      }
    );
    this.emitReals();
  }

  public deleteReal(id: number): void{
    this.http.get<void>(this.urlReal+'/d/'+id)
      .subscribe({
        next: () => this.reals.splice(this.reals.findIndex((r) => r.id === id),1),
        error:(err:any) => console.error(err),
        complete: () => console.log("Real supprimé avec succès")
      });
      this.emitReals();
  }

}
