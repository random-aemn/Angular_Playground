import { Component } from '@angular/core';

@Component({
  selector: 'app-exercise-page1b',
  templateUrl: './exercise-page1b.component.html',
  styleUrls: ['./exercise-page1b.component.scss']
})
export class ExercisePage1bComponent {

 public counter: number = 6;
resetCounter(){
this.counter = 0;
}

incrementCounter(){
this.counter++;
}

}
