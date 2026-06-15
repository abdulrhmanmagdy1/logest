interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary';
  size?: 'default' | 'sm';
}

const variantClasses = {
  primary: 'bg-cyan-400 text-slate-950 hover:bg-cyan-300',
  secondary: 'border border-white/10 bg-white/5 text-slate-100 hover:bg-white/10',
};

const sizeClasses = {
  default: 'h-12 px-5 text-sm',
  sm: 'h-10 px-4 text-sm',
};

export function Button({
  className = '',
  variant = 'primary',
  size = 'default',
  ...props
}: ButtonProps) {
  return (
    <button
      className={`inline-flex items-center justify-center rounded-full font-semibold transition focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-400/60 disabled:pointer-events-none disabled:opacity-60 ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
      {...props}
    />
  );
}
