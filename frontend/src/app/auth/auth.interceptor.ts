import { HttpInterceptorFn } from '@angular/common/http';

//TODO: delete this when login and register is implemented
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25lbDFAZ21haWwuY29tIiwidXNlcklkIjoxLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3ODg4ODQwNjQsImV4cCI6MTc4ODk3MDQ2NH0.WGkCYlQPfGvwXTEgl4p9F8nQf6GeryxFN0KIMn0XbLg';


  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
