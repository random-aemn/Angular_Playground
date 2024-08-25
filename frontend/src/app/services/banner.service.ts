import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of, take, tap} from "rxjs";
import {ClassifiedBannerDTO} from "../models/classified-banner-dto";
import {GetBannerDTO} from "../models/banners/get-banner-dto";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AddBannerDTO} from "../models/banners/add-banner-dto";

@Injectable({
	providedIn: 'root'
})
export class BannerService {

	private headerBannerHeight: number = 0;
	private totalBannerHeight: number = 0;
	private bsHeightInPixels: BehaviorSubject<string> = new BehaviorSubject("0px");

	constructor(private httpClient: HttpClient) {
	}

	public getClassifiedBanner(): Observable<ClassifiedBannerDTO> {
		let data: ClassifiedBannerDTO = new ClassifiedBannerDTO();

		data.banner_mode = 'demo';    // Possible values are 'demo', 'cui', or 'off'

		return of(data);
	}

	public getHeaderBanners(): Observable<GetBannerDTO[]> {
		// Construct the URL for the REST endpoint (to get all answers for this section)
		const restUrl = environment.baseUrl + '/api/banners/visible/2';

		// NOTE:  The REST call is not invoked you call subscribe() on this observable
		return this.httpClient.get <GetBannerDTO[]> (restUrl).pipe(
			tap((banners:GetBannerDTO[])=>{
				if(banners != null && banners.length > 0){
					//the rest call came back with a few banners

					//send a message out that the banner is 20 pixels taller
					this.headerBannerHeight = 20;
					this.totalBannerHeight = this.headerBannerHeight;
					let msg: string = this.totalBannerHeight + 'px';
					this.bsHeightInPixels.next(msg);
				}
				else {
					this.headerBannerHeight = 0;
					this.totalBannerHeight = this.headerBannerHeight;
				}

			}),
			take(1)  //grab the first value and it unsubscribes
		);
	}

	public getHeightInPixelsObs(): Observable<string> {
		return this.bsHeightInPixels.asObservable();
	}

	public getAllBanners(): Observable<GetBannerDTO[]> {
		// Construct the URL to get the data to load the Contract grid
		const restUrl = environment.baseUrl + '/api/banners/all'

		// Return an observable that will hold a list of GetBannerDTO objects
		return this.httpClient.get <GetBannerDTO[]>(restUrl);
	}

	public addBanner(addBannerDTO: AddBannerDTO): Observable<string> {
		// Construct the URL to get the data to load the Contract grid
		const restUrl = environment.baseUrl + '/api/banners/add'

		// Return an observable that will hold a string
		return this.httpClient.post <string>(restUrl, addBannerDTO);
	}

	public deleteBanner(bannerId: number): Observable<string> {
		// Construct the URL to get the data to load the Contract grid
		const restUrl = environment.baseUrl + '/api/banners/delete/' + bannerId;

		// Return an observable that will hold a string
		return this.httpClient.post <string>(restUrl, {});
	}
}
