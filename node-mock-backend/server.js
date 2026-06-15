const http = require("http");

const server = http.createServer((req, res) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);

  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    res.writeHead(204);
    res.end();
    return;
  }

  if (req.url === "/api/v1/auth/login" && req.method === "POST") {
    let body = "";
    req.on("data", chunk => { body += chunk; });
    req.on("end", () => {
      console.log(`Login attempt for: ${body}`);

      let role = "customer";
      let firstName = "User";
      let lastName = "Test";

      if (body.includes("supervisor")) {
          role = "supervisor";
          firstName = "أحمد";
          lastName = "المشرف";
      } else if (body.includes("accountant")) {
          role = "accountant";
          firstName = "سارة";
          lastName = "المحاسبة";
      } else if (body.includes("driver")) {
          role = "driver";
          firstName = "خالد";
          lastName = "السائق";
      } else if (body.includes("workshop")) {
          role = "workshop";
          firstName = "محمد";
          lastName = "الورشة";
      }

      const response = {
        success: true,
        message: "Login successful",
        data: {
          accessToken: "mock-access-token-" + Date.now(),
          refreshToken: "mock-refresh-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          user: {
            id: Math.floor(Math.random() * 1000),
            email: role + "@edham.com",
            firstName: firstName,
            lastName: lastName,
            role: role,
            phone: "+966500000000",
            isActive: true,
            createdAt: new Date().toISOString()
          }
        }
      };
      res.writeHead(200, { "Content-Type": "application/json" });
      res.end(JSON.stringify(response));
    });
  } else {
    res.writeHead(404);
    res.end(JSON.stringify({ success: false, message: "Not found" }));
  }
});

server.listen(8080, "0.0.0.0", () => {
  console.log("Mock server running on port 8080 (Multi-Role Support)");
});
