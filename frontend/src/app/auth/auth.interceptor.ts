import { HttpInterceptorFn } from '@angular/common/http';

//TODO: delete this when login and register is implemented
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25lbDFAZ21haWwuY29tIiwidXNlcklkIjoxLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3ODc5MjQ3MDYsImV4cCI6MTc4ODAxMTEwNn0.hK4Q9_Zg3YnnaPL0x_-PBbq05rTFpm7VqBraDRu5aqU';


  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
