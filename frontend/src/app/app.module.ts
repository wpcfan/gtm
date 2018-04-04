import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { CoreModule } from './core/core.module';
import { AppComponent } from './core/containers/app/app.component';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './reducers';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';


@NgModule({
  imports: [
    BrowserModule.withServerTransition({appId: 'gtm'}),
    CoreModule,
    StoreModule.forRoot(reducers, { metaReducers }),
    !environment.production ? StoreDevtoolsModule.instrument() : [],
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
