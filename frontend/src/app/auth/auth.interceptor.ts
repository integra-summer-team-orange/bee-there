import { HttpInterceptorFn } from '@angular/common/http';

//TODO: delete this when login and register is implemented
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25lbDFAZ21haWwuY29tIiwidXNlcklkIjoxLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3ODc4MzE2ODYsImV4cCI6MTc4NzkxODA4Nn0.g84O9c2iIRGZi-1it0oo2usVJzjLAVTpNToLrK_plHk';


  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
