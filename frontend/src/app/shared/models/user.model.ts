export  type Role= 'ADMIN'| 'VENUE_ADMIN' | 'PARTICIPANT';

export interface UserRequestDto{
  name: string  ,
  email: string ,
  password: string  ,
  phone: string,
  role: Role;
}

export interface UserResponseDto{
  id: number,
  name: string,
  email: string,
  phone: string,
  role: Role,
  createdAt: string;
}
