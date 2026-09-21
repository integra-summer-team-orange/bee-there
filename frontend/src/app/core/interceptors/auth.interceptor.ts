import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { Session } from '../services/session';

/** Attaches the stored bearer token to every request that leaves the app. */
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const token = inject(Session).readToken();

  if (!token) {
    return next(request);
  }

  return next(request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
