import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideNgxMask } from 'ngx-mask';

import { routes } from './app.routes';
import { BsModalService } from 'ngx-bootstrap/modal';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes), 
    provideHttpClient(), 
    provideNgxMask(),
    BsModalService,  
  ]
};
