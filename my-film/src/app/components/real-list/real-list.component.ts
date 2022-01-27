import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { Realisateur } from 'src/app/models/realisateur';
import { RealService } from 'src/app/services/real.service';

@Component({
  selector: 'app-real-list',
  templateUrl: './real-list.component.html',
  styleUrls: ['./real-list.component.css']
})
export class RealListComponent implements OnInit {
  list!: Realisateur[];
  sub!: Subscription;
  realForm!:FormGroup;
  currentReal: number = -1;
  editMode: boolean = false;

  constructor(
    private realService: RealService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.sub = this.realService.filmsSubject.subscribe({
      next:(reals:Realisateur[]) => { this.list = reals }
    });
    this.realService.getAllReals();
    this.initFilmForm();
  }

  initFilmForm(): void{
    this.realForm = this.formBuilder.group({
      nom: ['', Validators.required],
      prenom: ['', [Validators.required]],
      dateNaissance:['', [Validators.required]]
    });
  }

  onCreateUpdateSubmit(): void{
    if(this.editMode){
      this.realService.editReal(this.currentReal, this.realForm);
      this.resetCurrentItem();
    } else {
      this.realService.createReal(this.realForm);
      this.resetCurrentItem();
    }
  }

  updateCurrentItem(id:number): void{
    this.currentReal = id;
    this.editMode = true;
    if(this.editMode){
      this.realService.getRealById(this.currentReal).subscribe({
        next:(r: Realisateur) => {
          this.realForm.get('nom')?.setValue(r.nom);
          this.realForm.get('prenom')?.setValue(r.prenom);
          this.realForm.get('dateNaissance')?.setValue(r.dateNaissance);
        },
        error:(err:any) => console.error(err)
      })
    }
  }

  resetCurrentItem(): void{
    this.currentReal = -1;
    this.editMode = false;
    this.realForm.reset();
  }

  onDeleteUpdateSubmit(): void{
    this.realService.deleteReal(this.currentReal);
    this.currentReal = -1;
  }
}
