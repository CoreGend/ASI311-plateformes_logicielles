import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FilmListComponent } from './components/film-list/film-list.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { FilmDetailsComponent } from './components/film-details/film-details.component';
import { RealDetailsComponent } from './components/real-details/real-details.component';
import { RealListComponent } from './components/real-list/real-list.component';
import { GenreListComponent } from './components/genre-list/genre-list.component';
import { GenreDetailsComponent } from './components/genre-details/genre-details.component';

const routes: Routes = [
  { path: "films", component: FilmListComponent },
  { path: "films/:id", component: FilmDetailsComponent },
  { path: "reals", component: RealListComponent },
  { path: "reals/:id", component: RealDetailsComponent },
  { path: "genre", component: GenreListComponent},
  { path: "genre/:id", component: GenreDetailsComponent},
  { path: "not-found", component: NotFoundComponent },
  { path: "", redirectTo:"/films", pathMatch: "full" },
  { path: "**", redirectTo: "/not-found", pathMatch: "full"}
];

@NgModule({
 imports: [RouterModule.forRoot(routes)],
 exports: [RouterModule]
})
export class AppRoutingModule { }