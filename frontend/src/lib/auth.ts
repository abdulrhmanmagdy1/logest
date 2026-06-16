export type UserRole = 'admin' | 'supervisor' | 'employee' | 'driver' | 'client' | 'accountant';

export interface AuthUser {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  role: UserRole;
  status?: string;
  companyName?: string;
  driverInfo?: unknown;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: AuthUser;
}
