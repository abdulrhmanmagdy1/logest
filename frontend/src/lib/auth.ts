export type UserRole = 'admin' | 'operations' | 'dispatcher' | 'driver' | 'customer' | 'warehouse';

export interface AuthUser {
  id: string;
  name: string;
  email: string;
  role: UserRole;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: AuthUser;
}
