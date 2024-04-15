db.createUser(
        {
            user: "user-test",
            pwd: "test",
            roles: [
                {
                    role: "readWrite",
                    db: "logs"
                }
            ]
        }
);
db.createCollection("logs");