import { Genre } from './genre';
import {Realisateur} from './realisateur';

export interface Film {
// let avatar: Film = { id: 1, titre: 'Avatar', duree: 162, realisateur: cameron };
    id: number;
    titre: string;
    duree: number;
    realisateur: Realisateur;
    genre: Genre;
}
