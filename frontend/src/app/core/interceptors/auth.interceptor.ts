import { HttpInterceptorFn } from '@angular/common/http';

// TODO: Replace hardcoded token/interceptor once Login/Register integration is ready
const HARDCODED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25lbDFAZ21haWwuY29tIiwidXNlcklkIjoxLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3ODg3ODUxMjcsImV4cCI6MTc4ODg3MTUyN30.bH3OS7aX6gBcx5rQZRIF289tmFubgwhnJE6UX1QnqNU";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Skip auth endpoints for now
  if (req.url.includes('/api/auth/')) {
    return next(req);
  }

  // Read from localStorage, fallback to the hardcoded token
  const token = HARDCODED_TOKEN;

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
