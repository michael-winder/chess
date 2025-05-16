# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

##Phase 2 diagram:
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqsyb0YKAE9VuImgDmMAAwA6AJybMxqBACu2GAGI0wKrpgAlFMaRVHTkkCDQ3AHcACyQwMURUUgBaAD5yShooAC4YAG0ABQB5MgAVAF0YAHpHVUoAHTQAbwAiGsoPAFsUZqzmmGaAGn7cdQjoDh6+wf6UDuAkBEn+gF9MYUyYVNZ2LkoclraoTu7e-qHmkdUxqAnT6ebZ+cW71bZOblgt9dEcqH9AuJQAAUfgCQUofgAjo41GAAJRrDKiTZpWTyJQqdQ5YwoMAAVVqwMOxwRaMUyjUqhRMA4ACMsgAxJCcGAEyhkmC07zE4BdTC0v7AADWrMJMAisSiMB5XRgwAQgo43hQAA8ARoyRjKZsvkiVDk2VAyYiRCodWl1nsYAoFShgEqSsL0ABRFUqbAEMImjZJLYJbQ5AAsZgAzA0Wl11MAcT1+s6oA5stLCcc5bb7d55EL0M1VugOJhNRT1Ob0qaUDk0I4EAhvcitkXMaosiBBXFDUDDmTSXJyU3qXSsgoOBxReze3WzQ3e1qsa27XEFI4wFEgcBl1Fu4WZ8WqVtB8PR0uV8bvlOtm9dknQQCIWpq1hLx9S5akwcU7zujAclNzuuVyUEDZmgfQ-is3qUKW-oYDkABMZhmOGrQfl0oH9Gc-T-lEgHAWhUx5pwtj2E4LiuNA7A4jAAAyEABOErjRLE8TINo1KvjkBTFOUVS1OooRoOGMooEMlzXBwqyvtST5Wo0QkicAozjK8OzPrq5Y5AgtHMkCNF0VCMJBAiZ4oNSjaUtiuIdkJPboruA70kyLKGhyXLJu0n78oKIqGqmHAQGoaAAOTMKqALbrZ-a+haeoVjAVY1pOJlRWWmQ5AAQva+mwq67qemgEGfH6LEwTAwaIU0zSRqo0YnHGCbQDktL2jAfzQrCuaYPm4V9tqyXGQahKnjFbExTkgT5ApVzjHA0B-OAnl2iKADqDgmDAE2KTciUvhkVp4h4G7QEgABeKAcDlKAevxBVQcVYBBmYACMSFVTVsbNPGiY5I4B0rkdp0TARBbGaZO5NhZYDHlEVkoSgNk9SW+70jAjlHhuLncrD3WznualUD81roxOIPJdJSa6cyfiqA+mBkzt1Ayf0QmTPcWE4TmvR9BJu2FWk0H3TA8HlY0TOwyzf4buzIGczAQNEQ4zhuPYKDoNRtFOMwDExHEmD8yNDNJrk0jOlRzolM6FSVLxqj8Q0bNAegN0XipVr28BtMu580XqTAmnGBrOnq8uWWGdt04ReZMA4pDG5rpLDtoPDOP2TkqOEyevact4buOwKi1yvHwE0v5qhBSFapBNjdl9aNcXVrWJN43tv1RP9Z0XVdXqScl-MPc9FWvTGP6fQ10ot23gOdYRZmI3jBNQ0N5almTOS6RrVM03TNcG-s3MGz3d1wQh4Zy5oxGK64fyjlRAIwAA4p+VJa0xut3frqV5HfZuW8Yn524Xjtu5pBXgXACCcPbvEgnPfUvsAQ6QBCHeEYdURg0jtHKGccwHASTtXNIg4UbMjRhneQWdQHYXAXnYUZCpbFwCsFGAoVK4z1xt7fGMD4oN2GtvD++0sITw7nlJ2RVEgC2DP3CMlI3rD3qkmH6fCoAnTOh1LqzD6ZsNigvYmXCUERyxLAoID9IxAhwZFPByM-DNXlAge+n5jSqO4QTKxNiuiL3UcvT2q8EH3gQI+T2ajGbNF-pGWMuQWhBJQAASWkLGR6sEQyBnOIxdsYtOYDAaM0WkCBQBCh8p+cW-RwkADlPyc2WDAMoe8fTCIDELcMBTH4hLCZ+KJMS4kJP6EklAuTUKpPSZk7J3STjNHuEUkpwyykVKngWM+CtSLYEcFAbA3B4BthMoYkyz8dZ6ybobIopQf5-xblLcMoyuiVKgcAjxOcBJNFOSgZSkCvYpQJvOOQKAgRwFWYgoy2iZCoL0eg2O1yTG9TManQh6dNyZ1ctchaVDrm0NLvQxhWB7HQNihwsOOyciFIgFDKWAjrrd2qSVMRL1JFDzql9Me8jFGTxUf8lhzyYGaPkMgv5ujmwwFeXEdZQJwlbnsWClZC41m2K0UvUmHiRVvI3j4iBV5-FvnqV0FpORYnxNlkIvmh9BbHyaCqyJ0T1VtK1V1GZJE3C6BQDWCAEQYAACkIDMmcRs-pIAhSvxEe-K0BQ8TcUqOE-+WD0DhiWcAa1UAZqaSgEMcJUTzlPJXtcsNWTI3RugHG5p0gHmKvRTkAAVs6tAQInWUxQG1UOJMdEIy5YClcmDyHYKrqYmkyM06suAKQ2FlCRQIr8nQ8uYU0WsIJpixurCrS4vxQnQlXcea3REX3clUZKUfRkd9ceCiAbKOnoytR88iZsvZcwiGfL43SBBYjYVad1kYyjtmuFIp1mIrLgwiuqL90OPYfXLFk6ky4vWXO-KxKdVLtKvqiRq7arrupU1UcrUDJgF3QWEdzLYp3oleo1I-UYCBAzVAR0wEgThvTRAGNaSTDivkAiXt8ByPQGtOmJUMAiOAJilFdiTHFS6DY2gYDBUoq9wg2GA1lUKUwZHkmeUPGYBZhzGawi1aOW1qyM4Dgby+VoBQBETDNGW2grbQabAGm4iutcfWZ2jychlrQHK3xjylW721fAXVtSminzsLMpWUAI2ubtLAYA2AlmEBCGESI2tmLeuxXkY2ptzaWzpE7S51nMVb1HTAkA3A8DGJPYynIHAbW4hQDaBAGCr17mFdIIrZmnFQ1M8APLnKCs1ZKzWQ0qhctCqMzIVraZrHOSw-WGtOMWvcEXDWdZnWKsp16+NkyTi9ONYndsaz-m8D2YVapGLjRE2LoDHq4WcsgA

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
