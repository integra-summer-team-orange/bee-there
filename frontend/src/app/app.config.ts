import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {providePrimeNG} from 'primeng/config';

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
