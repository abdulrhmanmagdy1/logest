function tenantContext(req, res, next) {
  const tenantId =
    req.headers["x-tenant-id"] ||
    req.headers["x-company-id"] ||
    req.query.tenantId ||
    req.query.companyId ||
    null;

  req.tenant = {
    id: tenantId,
    isResolved: Boolean(tenantId),
  };

  if (tenantId) {
    res.setHeader("x-tenant-id", String(tenantId));
  }

  next();
}

module.exports = tenantContext;
