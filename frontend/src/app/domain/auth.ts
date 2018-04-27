import { Authority } from './authority';

export interface Auth {
  login: string;
  password: string;
  authorities?: Authority[];
}
