import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';

import { routes } from './app.routes';
import { providePrimeNG } from 'primeng/config';
import { provideApi } from '../api/generated';
import { authInterceptor } from './core/interceptors/auth.interceptor';

import Aura from '@primeuix/themes/aura';
import { definePreset } from '@primeuix/themes';

/**
 * Aura with the violet from the design as the primary colour. Aura ships with emerald, which is why every
 * button came out green. Only the palette is overridden; every other Aura default is left alone.
 */
const IntegraPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '#f5f3ff',
      100: '#ede9fe',
      200: '#ddd6fe',
      300: '#c4b5fd',
      400: '#a78bfa',
      500: '#8b5cf6',
      600: '#7c3aed',
      700: '#6d28d9',
      800: '#5b21b6',
      900: '#4c1d95',
      950: '#2e1065',
    },
  },
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    // Requests go out same-origin and the dev server proxies /api to the backend (see proxy.conf.json),
    // which keeps the browser away from CORS entirely.
    provideApi(''),
    MessageService,
    ConfirmationService,
    providePrimeNG({
      theme: {
        preset: IntegraPreset,
        options: {
          darkModeSelector: '.my-app-dark',
        },
      },
    }),
  ],
};
