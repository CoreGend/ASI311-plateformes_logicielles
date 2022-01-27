import {Film} from './film';

export interface Realisateur {
// let jackson: Realisateur = { id: 2, nom: 'Jackson', prenom: 'Peter', dateNaissance: '1961-10-31', filmRealises: [], celebre: true };
    id: number;
    nom: string;
    prenom: string;
    dateNaissance: string;
    filmRealises: Film[];
    celebre: boolean;
}
