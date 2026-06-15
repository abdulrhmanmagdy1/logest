/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  // output: 'standalone' is only needed for Docker/self-hosted deployments.
  // Vercel builds its own optimised output — do NOT set this for Vercel.
  // Re-add `output: 'standalone'` if you switch back to Hetzner/Docker.
};

export default nextConfig;
