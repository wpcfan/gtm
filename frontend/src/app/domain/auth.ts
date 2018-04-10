import { Role } from './role';

export interface Auth {
  login: string;
  password: string;
  roles: Role[];
}
