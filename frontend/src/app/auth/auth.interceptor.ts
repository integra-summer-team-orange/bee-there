import { HttpInterceptorFn } from '@angular/common/http';

//TODO: delete this when login and register is implemented
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25lbDFAZ21haWwuY29tIiwidXNlcklkIjoyLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3ODg0NDU3MDEsImV4cCI6MTc4ODUzMjEwMX0.n131iY0XWsNoAUUTCzz7rDl0Ki9688u-BdRWtjiPyeI';


  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
