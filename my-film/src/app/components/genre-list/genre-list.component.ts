import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';

import { Genre } from 'src/app/models/genre';
import { GenreService } from 'src/app/services/genre.service';

@Component({
  selector: 'app-genre-list',
  templateUrl: './genre-list.component.html',
  styleUrls: ['./genre-list.component.css']
})
export class GenreListComponent implements OnInit {
  list!: Genre[];
  genreForm!: FormGroup;
  sub!: Subscription;
  currentGenre: number=-1;
  editMode: boolean=false;

  constructor(
    private genreService: GenreService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.sub = this.genreService.genreSubject.subscribe({
      next:(genres:Genre[])=>this.list=genres
    });
    this.genreService.getAllGenres();
    this.initGenreForm();
  }

  initGenreForm():void{
    this.genreForm=this.formBuilder.group({
      genre:['', Validators.required]
    })
  }

  onCreateUpdateSubmit():void{
    if(this.editMode){
      this.genreService.editGenre(this.currentGenre, this.genreForm);
      this.resetCurrentItem();
    }else{
      this.genreService.createGenre(this.genreForm);
      this.resetCurrentItem();
    }
  }

  resetCurrentItem(): void{
    this.currentGenre=-1;
    this.editMode=false;
    this.genreForm.reset();
  }

  updateCurrentItem(id:number):void{
    this.currentGenre=id;
    this.editMode=true;
    if(this.editMode){
      this.genreService.getGenreById(this.currentGenre).subscribe({
        next:(g:Genre)=>{
          this.genreForm.get('genre')?.setValue(g.genre);
        },
        error: (err:any)=>console.error(err)
      })
    }
  }

  onDeleteUpdateSubmit():void{
    this.genreService.deleteGenre(this.currentGenre);
    this.currentGenre=-1;
  }
}
