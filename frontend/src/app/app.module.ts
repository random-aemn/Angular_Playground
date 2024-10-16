import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {RouterModule, Routes} from "@angular/router";
import {WelcomeComponent} from "./layout/welcome/welcome.component";
import {NotFoundComponent} from "./errors/not-found/not-found.component";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {ErrorInterceptor} from "./errorHandler/error.interceptor";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatCardModule} from "@angular/material/card";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatNativeDateModule} from "@angular/material/core";
import {MatChipsModule} from "@angular/material/chips";
import {MatDialogModule} from "@angular/material/dialog";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatInputModule} from "@angular/material/input";
import {MatListModule} from "@angular/material/list";
import {MatMenuModule} from "@angular/material/menu";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatRadioModule} from "@angular/material/radio";
import {MatSelectModule} from "@angular/material/select";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatStepperModule} from "@angular/material/stepper";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatTabsModule} from "@angular/material/tabs";
import {MatToolbarModule} from "@angular/material/toolbar";
import { SamplePageComponent } from './features/sample-page/sample-page.component';
import {Constants} from "./utilities/constants";
import { SamplePage2Component } from './features/sample-page2/sample-page2.component';
import {ClassificationBannerComponent} from "./layout/classification-banner/classification-banner.component";
import {HeaderComponent} from "./layout/header/header.component";
import {NavbarExtendedComponent} from "./layout/navbar/navbar-extended/navbar-extended.component";
import {NavbarCollapsedComponent} from "./layout/navbar/navbar-collapsed/navbar-collapsed.component";
import { UserAdminComponent } from './features/admin/user-admin/user-admin.component';
import { UserSettingsComponent } from './features/user-settings/user-settings.component';
import {PageGuard} from "./guards/page.guard";
import {AgGridModule} from "ag-grid-angular";
import { ListExceptionsGridComponent } from './features/admin/list-exceptions/list-exceptions-grid/list-exceptions-grid.component';
import { ListExceptionsActionRendererComponent } from './features/admin/list-exceptions/list-exceptions-action-renderer/list-exceptions-action-renderer.component';
import { Exercise1aComponent } from './exercise1a/exercise1a.component';
import { ExercisePage1bComponent } from './exercise-page1b/exercise-page1b.component';
import { RegistrationApprovedComponent } from './registration-approved/registration-approved.component';
import { HtmlOverImageComponent } from './html-over-image/html-over-image.component';
import { MySettingsComponent } from './my-settings/my-settings.component';
import { StockTradesComponent } from './stock-trades/stock-trades.component';
import { ResponsiveLayoutComponent } from './responsive-layout/responsive-layout.component';
import { VariableHeightComponent } from './variable-height/variable-height.component';
import { FakeHolyGrailComponent } from './fake-holy-grail/fake-holy-grail.component';
import { RealHolyGrailComponent } from './real-holy-grail/real-holy-grail.component';

// Setup the routes.  If no route is found, then take the user to the NotFoundComponent
// NOTE:  The **ORDER** of these routes matters.  The NotFoundComponent should always be last
const appRoutes: Routes = [
  { path:  Constants.SAMPLE_PAGE_ROUTE,                  component: SamplePageComponent,                canActivate: [PageGuard.canActivate ] },
  { path:  Constants.SAMPLE_PAGE_ROUTE_2,                component: SamplePage2Component,               canActivate: [PageGuard.canActivate ] },
  { path:  Constants.LIST_EXCEPTIONS_ROUTE,              component: ListExceptionsGridComponent,        canActivate: [PageGuard.canActivate ] },
  { path:  Constants.USER_ADMIN_ROUTE,                   component: UserAdminComponent,                 canActivate: [PageGuard.canActivate ] },
  { path:  Constants.USER_SETTINGS_ROUTE,                component: UserSettingsComponent,              canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_1A_ROUTE,                  component: Exercise1aComponent,                canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_1B_ROUTE,                  component: ExercisePage1bComponent,            canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_1C_ROUTE,                  component: RegistrationApprovedComponent,      canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_1D_ROUTE,                  component: HtmlOverImageComponent,             canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_2_MY_Settings_ROUTE,       component: MySettingsComponent,                canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_3_STOCK_TRADES,            component: StockTradesComponent,               canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_4_RESPONSIVE_LAYOUT,            component: ResponsiveLayoutComponent,     canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_5_VARIABLE_HEIGHT,            component: VariableHeightComponent,         canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_5B_HOLY_GRAIL,            component: FakeHolyGrailComponent,              canActivate: [PageGuard.canActivate ] },
  { path:  Constants.EXERCISE_5B_HOLY_GRAIL_REAL,            component: RealHolyGrailComponent,              canActivate: [PageGuard.canActivate ] },

  { path:  '',                                component: WelcomeComponent,                   canActivate: [PageGuard.canActivate ] },
  { path:  '**',                              component: NotFoundComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    SamplePageComponent,
    SamplePage2Component,
    WelcomeComponent,
    ClassificationBannerComponent,
    HeaderComponent,
    NavbarExtendedComponent,
    NavbarCollapsedComponent,
    UserAdminComponent,
    UserSettingsComponent,
    ListExceptionsGridComponent,
    ListExceptionsActionRendererComponent,
    Exercise1aComponent,
    ExercisePage1bComponent,
    RegistrationApprovedComponent,
    HtmlOverImageComponent,
    MySettingsComponent,
    StockTradesComponent,
    ResponsiveLayoutComponent,
    VariableHeightComponent,
    FakeHolyGrailComponent,
    RealHolyGrailComponent
  ],


  imports: [
    AgGridModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatSlideToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatNativeDateModule,
    MatChipsModule,
    MatDialogModule,
    MatDatepickerModule,
    MatGridListModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatProgressBarModule,
    MatRadioModule,
    MatSelectModule,
    MatSidenavModule,
    MatStepperModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTabsModule,
    MatToolbarModule,
    ReactiveFormsModule,
    RouterModule.forRoot(appRoutes),
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
