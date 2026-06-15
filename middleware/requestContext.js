const { randomUUID } = require("crypto");

function requestContext(req, res, next) {
  const correlationId = req.headers["x-correlation-id"] || randomUUID();
  req.context = {
    correlationId,
    requestStartedAt: Date.now(),
  };
  res.setHeader("x-correlation-id", correlationId);
  next();
}

module.exports = requestContext;
