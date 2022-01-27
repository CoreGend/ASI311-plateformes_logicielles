import { Film } from "../models/film";
import { Realisateur } from "../models/realisateur";
import { FILMS, REALISATEURS } from "./mock";

let copy = (x: any[]) => JSON.parse(JSON.stringify(x)) as any[];

export function getAllFilms(): Film[] {
  return copy(FILMS);
}

export function getFilmById(id: number): Film | undefined {
  return getAllFilms().find(f => f.id === id);
}

export function getRealisateurByNomPrenom(nom: string, prenom: string): Realisateur | undefined {
  return copy(REALISATEURS).find(r => r.nom === nom && r.prenom === prenom);
}

export function getFilmByRealisateur(r: Realisateur): Film[] {
  return getAllFilms().filter(f=> f.realisateur.id === r.id);
}
