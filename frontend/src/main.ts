import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';
import "ag-grid-enterprise";
import {LicenseManager} from "ag-grid-enterprise";
import {environment} from "./environments/environment";

// Set the license key for ag-grid-enterprise
LicenseManager.setLicenseKey(environment.agGridLicenseKey);

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
