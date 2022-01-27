import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FilmListComponent } from './components/film-list/film-list.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { FilmDetailsComponent } from './components/film-details/film-details.component';
import { RealDetailsComponent } from './components/real-details/real-details.component';
import { RealListComponent } from './components/real-list/real-list.component';
import { GenreListComponent } from './components/genre-list/genre-list.component';
import { GenreDetailsComponent } from './components/genre-details/genre-details.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FilmListComponent,
    NotFoundComponent,
    FilmDetailsComponent,
    RealDetailsComponent,
    RealListComponent,
    GenreListComponent,
    GenreDetailsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
