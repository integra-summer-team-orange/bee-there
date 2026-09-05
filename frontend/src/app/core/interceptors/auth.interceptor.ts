import { HttpInterceptorFn } from '@angular/common/http';

// TODO: Replace hardcoded token once Login/Register integration is ready
const HARDCODED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25lbDFAZ21haWwuY29tIiwidXNlcklkIjoyLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3ODg2MTgyNDUsImV4cCI6MTc4ODcwNDY0NX0.P3DyCu7SB-C1bkvjuogqd5WsTwszgJ_Oept7m40Fi_Y";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Skip auth endpoints for now
  if (req.url.includes('/api/auth/')) {
    return next(req);
  }

  // Read from localStorage, fallback to the hardcoded token
  const token = localStorage.getItem('auth_token') ?? HARDCODED_TOKEN;

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
