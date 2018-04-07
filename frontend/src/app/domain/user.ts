import { Role } from './role';

export interface User {
  id: string;
  username: string;
  mobile: string;
  email: string | null;
  roles: Role[];
}
