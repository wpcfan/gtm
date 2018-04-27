import { Authority } from './authority';

export interface User {
  id?: string;
  login: string;
  password?: string;
  mobile: string;
  email: string | null;
  name: string;
  avatar: string;
  authorities?: Authority[];
}
