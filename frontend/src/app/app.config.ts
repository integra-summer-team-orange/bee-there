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

const IntegraPreset = definePreset(Aura, {
  primitive: {
    red: {
      50: '#ffedea',
      100: '#ffdad6',
      200: '#ffb4ab',
      300: '#ff897d',
      400: '#de3730',
      500: '#ba1a1a',
      600: '#a31217',
      700: '#93000a',
      800: '#690005',
      900: '#410002',
      950: '#2d0001',
    },
  },
  semantic: {
    primary: {
      50: '#f6edff',
      100: '#e9ddff',
      200: '#cfbcff',
      300: '#b69df8',
      400: '#7f67be',
      500: '#4f378a',
      600: '#452f7c',
      700: '#381e72',
      800: '#2c1466',
      900: '#21005d',
      950: '#16003d',
    },
    colorScheme: {
      light: {
        surface: {
          0: '#ffffff',
          50: '#fdf7ff',
          100: '#f8f2fa',
          200: '#e6e0e9',
          300: '#cbc4d2',
          400: '#a8a2b0',
          500: '#7a7582',
          600: '#494551',
          700: '#1d1b20',
          800: '#141218',
          900: '#0f0d13',
          950: '#000000',
        },
      },
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
