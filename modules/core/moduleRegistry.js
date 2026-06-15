function registerModules(app, modules) {
  modules.forEach((mod) => {
    if (!mod || !mod.router) {
      return;
    }
    app.use(mod.basePath, mod.router);
  });
}

module.exports = { registerModules };
