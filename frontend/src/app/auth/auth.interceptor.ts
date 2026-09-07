import { HttpInterceptorFn } from '@angular/common/http';

//TODO: delete this when login and register is implemented
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25lbDFAZ21haWwuY29tIiwidXNlcklkIjoxLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3ODg3NzQ5MTMsImV4cCI6MTc4ODg2MTMxM30.YagaOGnPuRLf_qsqgrTxAxkpg8XQy-rgf4MHQ7I57G8';


  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
