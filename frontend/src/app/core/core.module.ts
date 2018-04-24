import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconRegistry } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { SharedModule } from '../shared/shared.module';
import { AppComponent } from './containers/app/app.component';
import { PageNotFoundComponent } from './components/page-not-found.component';
import { HeaderComponent } from './components/header.component';
import { FooterComponent } from './components/footer.component';
import { SidebarComponent } from './components/sidebar.component';
import { loadIconResources } from '../utils/icon';
import { AuthModule } from '../auth/auth.module';
import { environment } from '../../environments/environment';

@NgModule({
  declarations: [
    AppComponent,
    PageNotFoundComponent,
    HeaderComponent,
    FooterComponent,
    SidebarComponent
  ],
  imports: [
    SharedModule,
    HttpClientModule,
    AuthModule,
    AppRoutingModule,
    BrowserAnimationsModule
  ]
})
export class CoreModule {
  constructor(
    @Optional()
    @SkipSelf()
    parentModule: CoreModule,
    ir: MatIconRegistry,
    ds: DomSanitizer
  ) {
    if (parentModule) {
      throw new Error('CoreModule 已经装载，请仅在 AppModule 中引入该模块。');
    }
    loadIconResources(ir, ds);
    console.log(environment.apiBaseUrl);
  }
}
