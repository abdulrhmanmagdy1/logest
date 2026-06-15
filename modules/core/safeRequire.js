function safeRequire(modulePath) {
  try {
    return require(modulePath);
  } catch (error) {
    console.warn(`[safeRequire] Skipping module "${modulePath}" due to load error: ${error.message}`);
    return null;
  }
}

module.exports = { safeRequire };
