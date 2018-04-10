import { Role } from './role';

export interface User {
  id?: string;
  username: string;
  password?: string;
  mobile: string;
  email: string | null;
  name: string;
  avatar: string;
  roles: Role[];
}
