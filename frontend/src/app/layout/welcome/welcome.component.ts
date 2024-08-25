import {Component, HostListener, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
	selector: 'app-welcome',
	templateUrl: './welcome.component.html',
	styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent implements OnInit {

	public compactPageMode: boolean = false;
	private REPOSITION_LAYOUT_MIN_WIDTH: number = 1400;

	private getIsPageCompactMode(): void {
		this.compactPageMode = window.innerWidth < this.REPOSITION_LAYOUT_MIN_WIDTH;
	}

	public constructor(private router: Router) {
	}

	public ngOnInit() {
		this.getIsPageCompactMode();  // Set the page to compact mode if the window is too small
	}


	@HostListener('window:resize', ['$event'])
	onResize(event: Event): void {
		this.compactPageMode = window.innerWidth < this.REPOSITION_LAYOUT_MIN_WIDTH;
	}
}
