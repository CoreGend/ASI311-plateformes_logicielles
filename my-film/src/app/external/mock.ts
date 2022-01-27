import { Film } from "../models/film";
import { Realisateur } from "../models/realisateur";

let cameron: Realisateur = { id: 1, nom: "Cameron", prenom: "James", dateNaissance: "1954-08-16", filmRealises: [], celebre: false };
let jackson: Realisateur = { id: 2, nom: 'Jackson', prenom: 'Peter', dateNaissance: '1961-10-31', filmRealises: [], celebre: true };

let avatar: Film = { id: 1, titre: 'Avatar', duree: 162, realisateur: cameron };
let lotr1: Film = { id: 2, titre: 'La communauté de l\'anneau', duree: 178, realisateur: jackson };
let lotr2: Film = { id: 3, titre: 'Les deux tours', duree: 179, realisateur: jackson };
let lotr3: Film = { id: 4, titre: 'Le retour du roi', duree: 201, realisateur: jackson };

export const FILMS: Film[] = [
  { ...avatar, realisateur: cameron },
  { ...lotr1, realisateur: jackson },
  { ...lotr2, realisateur: jackson },
  { ...lotr3, realisateur: jackson }
];

export const REALISATEURS: Realisateur[] = [
  { ...cameron, filmRealises: [avatar] },
  { ...jackson, filmRealises: [lotr1, lotr2, lotr3] }
];
