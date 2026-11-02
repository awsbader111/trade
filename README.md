# AtlasFX — Java Applications Lecture Homework

**John von Neumann University**

**Developers:**
- Salokhov Jahongir — PQJEK6
- Bader Aws Basel Saleem — ZUKGOB

## Project Description

AtlasFX is a Java Spring Boot web application for exploring currency exchange rates and practising Forex trading.

The application provides Hungarian National Bank exchange rates through SOAP, interactive Chart.js visualizations, and OANDA practice-account functionality.

## Requirements

- Java JDK 25
- IntelliJ IDEA or another compatible Java IDE
- Internet connection
- OANDA practice account and API token for Forex features

## How to Run the Application

1. Download the project ZIP from GitHub and extract it.
2. Open IntelliJ IDEA.
3. Select **File → Open** and choose the extracted folder containing `pom.xml`.
4. Confirm that the project uses **Java JDK 25**.
5. Allow Maven to load the project dependencies.

## Configure OANDA Practice Credentials

In the main project folder (the same folder containing `pom.xml`), create a file named `password.txt`.

Add the following lines:

```properties
ACCOUNT_ID=YOUR_OANDA_PRACTICE_ACCOUNT_ID
TOKEN=YOUR_OANDA_API_TOKEN
```

Replace the placeholder values with valid OANDA practice credentials. Do not include quotation marks.

**Security:** `password.txt` is excluded from Git version control. Never upload a real API token to a public repository.

The application can also read credentials from the `OANDA_ACCOUNT_ID` and `OANDA_TOKEN` environment variables.

## Start the Application

In IntelliJ, open `TradeApplication.java` and run its `main()` method.

Alternatively, from the project root, run:

```bash
java -jar homework.jar
```

Open your browser and visit:

http://localhost:8080

The application should display the AtlasFX homepage.

## Available Features

1. Home — company homepage and navigation
2. SOAP – MNB — exchange rates and Chart.js visualization
3. Forex Account — OANDA practice-account information
4. Actual Prices — current currency quotations
5. Historical Prices — last 10 historical prices
6. Open Trade — create a practice market order
7. Open Positions — display open trades
8. Close Trade — close a selected open trade

**Note:** Opening and closing Forex trades depends on market availability. The application uses the OANDA practice environment, not a real-money account.

## Repository

https://github.com/awsbader111/trade
