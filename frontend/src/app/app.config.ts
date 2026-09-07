import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { providePrimeNG } from 'primeng/config';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import {provideHttpClient} from '@angular/common/http';

import {provideApi} from '../api/generated';

import { routes } from './app.routes';
import { authInterceptor } from './auth/auth.interceptor';

import Aura from '@primeuix/themes/aura';
import { definePreset } from '@primeuix/themes';

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
    provideHttpClient(
      withInterceptors([authInterceptor])//TODO:modify this when login and register is implemented
    ),
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(),
    provideApi(''),
    providePrimeNG({
      theme: {
        preset: IntegraPreset,
        options: {
          darkModeSelector: '.my-app-dark'
        }
      }
    })
  ]
};
