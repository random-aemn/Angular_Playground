import {Component, OnInit} from '@angular/core';
import {BannerService} from "../../services/banner.service";
import {ClassifiedBannerDTO} from "../../models/classified-banner-dto";
import {Observable} from "rxjs";

@Component({
  selector: 'app-classification-banner',
  templateUrl: './classification-banner.component.html',
  styleUrls: ['./classification-banner.component.scss']
})
export class ClassificationBannerComponent implements OnInit {

  public classifiedBannerObs: Observable<ClassifiedBannerDTO>;

  public constructor(private bannerService: BannerService) {
  }

  public ngOnInit(): void {
    // Initialize the observable to get classification banner info
    this.classifiedBannerObs = this.bannerService.getClassifiedBanner();
  }


}
